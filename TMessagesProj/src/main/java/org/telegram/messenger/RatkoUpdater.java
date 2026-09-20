package org.telegram.messenger;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import org.json.JSONObject;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

public class RatkoUpdater {

    public static final String RELEASES_API = "https://api.github.com/repos/IzTrusheb/Ratkogram-/releases/latest";

    public static class ReleaseInfo {
        public String tagName;
        public String name;
        public String apkUrl;
        public long apkSize;
        public String body;
    }

    public interface Callback {
        void onResult(ReleaseInfo info, String error);
    }

    public static void checkForUpdates(Callback callback) {
        new Thread(() -> {
            ReleaseInfo info = null;
            String error = null;
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(RELEASES_API).openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(20000);
                connection.setRequestProperty("Accept", "application/vnd.github+json");
                int code = connection.getResponseCode();
                if (code == 200) {
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    JSONObject json = new JSONObject(builder.toString());
                    info = new ReleaseInfo();
                    info.tagName = json.optString("tag_name", "");
                    info.name = json.optString("name", "");
                    info.body = json.optString("body", "");
                    if (json.has("assets") && json.getJSONArray("assets").length() > 0) {
                        JSONObject asset = null;
                        for (int i = 0; i < json.getJSONArray("assets").length(); i++) {
                            JSONObject a = json.getJSONArray("assets").getJSONObject(i);
                            String an = a.optString("name", "");
                            if (an.endsWith(".apk")) {
                                String abi = getPreferredAbiSuffix();
                                if (abi != null && an.contains(abi)) {
                                    asset = a;
                                    break;
                                }
                                if (asset == null) {
                                    asset = a;
                                }
                            }
                        }
                        if (asset != null) {
                            info.apkUrl = asset.optString("browser_download_url", "");
                            info.apkSize = asset.optLong("size", 0);
                        }
                    }
                } else if (code == 404) {
                    error = "no_releases";
                } else {
                    error = "http_" + code;
                }
                connection.disconnect();
            } catch (Exception e) {
                FileLog.e(e);
                error = "network";
            }
            ReleaseInfo finalInfo = info;
            String finalError = error;
            AndroidUtilities.runOnUIThread(() -> callback.onResult(finalInfo, finalError));
        }).start();
    }

    private static String getPreferredAbiSuffix() {
        String[] abis = Build.SUPPORTED_ABIS;
        if (abis != null && abis.length > 0) {
            if (abis[0].contains("arm64")) return "v8";
            if (abis[0].contains("armeabi")) return "v7";
        }
        return null;
    }

    public static int compareVersions(String current, String release) {
        int[] a = parseVersion(current);
        int[] b = parseVersion(release);
        for (int i = 0; i < Math.max(a.length, b.length); i++) {
            int av = i < a.length ? a[i] : 0;
            int bv = i < b.length ? b[i] : 0;
            if (av != bv) return Integer.compare(av, bv);
        }
        return 0;
    }

    private static int[] parseVersion(String version) {
        try {
            String digitsOnly = version.replaceAll("[^0-9.]", "");
            if (TextUtils.isEmpty(digitsOnly)) {
                return new int[0];
            }
            String[] parts = digitsOnly.split("\\.");
            int[] result = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].isEmpty()) continue;
                result[i] = Integer.parseInt(parts[i]);
            }
            return result;
        } catch (Exception e) {
            return new int[0];
        }
    }

    public static String extractVersionFromTag(String tag) {
        if (tag == null) return "";
        return tag.replaceAll(".*v", "");
    }

    public static void downloadAndInstall(BaseFragment fragment, ReleaseInfo info) {
        Activity activity = fragment.getParentActivity();
        if (activity == null || TextUtils.isEmpty(info.apkUrl)) {
            return;
        }
        AlertDialog progressDialog = new AlertDialog(activity, 0, fragment.getResourceProvider());
        progressDialog.setCanCancel(true);
        progressDialog.setMessage("Скачиваю обновление…");
        Runnable cancel = downloadApk(info.apkUrl, file -> {
            progressDialog.dismiss();
            if (file != null) {
                installApk(activity, file);
            } else {
                AndroidUtilities.runOnUIThread(() -> {
                    org.telegram.ui.ActionBar.AlertDialog d = new org.telegram.ui.ActionBar.AlertDialog(activity, 0, fragment.getResourceProvider());
                    d.setTitle("Обновления");
                    d.setMessage("Не удалось скачать обновление. Проверь соединение");
                    d.setPositiveButton("OK", null);
                    d.show();
                });
            }
        });
        progressDialog.setOnCancelListener(dialog -> {
            if (cancel != null) {
                cancel.run();
            }
        });
        fragment.showDialog(progressDialog);
    }

    public interface DownloadCallback {
        void onFinished(File file);
    }

    public static Runnable downloadApk(String url, DownloadCallback callback) {
        AtomicReference<Runnable> cancelRef = new AtomicReference<>();
        Thread[] threadHolder = new Thread[1];
        threadHolder[0] = new Thread(() -> {
            File target = new File(ApplicationLoader.applicationContext.getCacheDir(), "ratkogram-update.apk");
            if (downloadToFile(url, target, threadHolder[0])) {
                AndroidUtilities.runOnUIThread(() -> callback.onFinished(target));
            } else {
                target.delete();
                AndroidUtilities.runOnUIThread(() -> callback.onFinished(null));
            }
        });
        cancelRef.set(threadHolder[0]::interrupt);
        threadHolder[0].start();
        return cancelRef.get();
    }

    private static boolean downloadToFile(String url, File target, Thread thread) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            if (connection.getResponseCode() != 200) {
                return false;
            }
            try (InputStream in = connection.getInputStream();
                 java.io.FileOutputStream out = new java.io.FileOutputStream(target)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    if (thread != null && thread.isInterrupted()) {
                        return false;
                    }
                    out.write(buffer, 0, read);
                }
            }
            return target.length() > 1000000;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void installApk(Activity activity, File file) {
        try {
            Uri uri = FileProvider.getUriForFile(activity, ApplicationLoader.getApplicationId() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
            AndroidUtilities.addToClipboard(file.getAbsolutePath());
        }
    }
}

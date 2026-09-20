package org.telegram.messenger;

import android.app.Activity;
import android.text.TextUtils;

import org.json.JSONArray;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class RatkoTranslator {

    public static final String ENDPOINT = "https://translate.googleapis.com/translate_a/single";

    public interface Callback {
        void onResult(String translated, String detectedLanguage, String error);
    }

    public static boolean shouldUseFreeTranslator(int currentAccount) {
        UserConfig config = UserConfig.getInstance(currentAccount);
        TLRPC.User user = config.getCurrentUser();
        boolean realPremium = user != null && user.premium;
        return !realPremium || RatkoConfig.getBoolean("free_translator", false);
    }

    public static String getTargetLanguage() {
        String lang = LocaleController.getInstance().getCurrentLocale().getLanguage();
        if (lang != null && lang.contains("-")) {
            lang = lang.split("-")[0];
        }
        return TextUtils.isEmpty(lang) ? "en" : lang;
    }

    public static void translate(final String text, final String targetLanguage, final Callback callback) {
        new Thread(() -> {
            String translated = null;
            String detected = null;
            String error = null;
            HttpURLConnection connection = null;
            try {
                String query = URLEncoder.encode(text, "UTF-8");
                String urlStr = ENDPOINT + "?client=gtx&sl=auto&tl=" + targetLanguage + "&dt=t&q=" + query;
                connection = (HttpURLConnection) new URL(urlStr).openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(25000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android)");
                int code = connection.getResponseCode();
                if (code == 200) {
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    JSONArray response = new JSONArray(builder.toString());
                    JSONArray segments = response.optJSONArray(0);
                    if (segments != null) {
                        StringBuilder result = new StringBuilder();
                        for (int i = 0; i < segments.length(); i++) {
                            JSONArray segment = segments.optJSONArray(i);
                            if (segment != null && segment.length() > 0) {
                                String piece = segment.optString(0, "");
                                if (!TextUtils.isEmpty(piece)) {
                                    result.append(piece);
                                }
                            }
                        }
                        translated = result.toString();
                    }
                    if (segments != null && segments.length() > 0) {
                        JSONArray first = segments.optJSONArray(0);
                        if (first != null && first.length() > 2) {
                            detected = first.optString(2, null);
                        }
                    }
                    if (TextUtils.isEmpty(translated) || translated.equals(text)) {
                        error = "no_translation";
                    }
                } else {
                    error = "http_" + code;
                }
            } catch (Exception e) {
                FileLog.e(e);
                error = "network";
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            final String finalTranslated = translated;
            final String finalDetected = detected;
            final String finalError = error;
            AndroidUtilities.runOnUIThread(() -> callback.onResult(finalTranslated, finalDetected, finalError));
        }).start();
    }

    public static void showTranslationDialog(Activity activity, org.telegram.ui.ActionBar.Theme.ResourcesProvider resourcesProvider, String original, String translated, String detectedLanguage) {
        AlertDialog dialog = new AlertDialog(activity, 0, resourcesProvider);
        dialog.setTitle("Перевод" + (detectedLanguage != null ? " (" + detectedLanguage.toUpperCase() + " → " + getTargetLanguage().toUpperCase() + ")" : ""));
        String body = (translated != null ? translated : "") + "\n\n— — —\n" + (original != null ? original : "");
        dialog.setMessage(body);
        dialog.setPositiveButton("OK", null);
        dialog.setNegativeButton("Копировать", (dlg, which) -> AndroidUtilities.addToClipboard(translated != null ? translated : ""));
        dialog.show();
    }
}

package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;

public class RatkoConfig {

    public static final String PREFS_NAME = "ratko_features";

    public static final String PREF_SHOW_USER_ID = "show_user_id";
    public static final String PREF_ANTI_DELETE = "anti_delete";
    public static final String PREF_NO_RESTRICTIONS = "no_restrictions";
    public static final String PREF_SECRET_SCREENSHOTS = "secret_screenshots";
    public static final String PREF_LOCAL_PREMIUM = "local_premium";

    private static volatile SharedPreferences prefs;

    private static SharedPreferences prefs() {
        if (prefs == null && ApplicationLoader.applicationContext != null) {
            synchronized (RatkoConfig.class) {
                if (prefs == null) {
                    prefs = ApplicationLoader.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                }
            }
        }
        return prefs;
    }

    public static boolean getBoolean(String key, boolean def) {
        SharedPreferences p = prefs();
        return p != null ? p.getBoolean(key, def) : def;
    }

    public static void setBoolean(String key, boolean value) {
        SharedPreferences p = prefs();
        if (p != null) {
            p.edit().putBoolean(key, value).apply();
        }
    }

    public static boolean isShowUserIdEnabled() {
        return getBoolean(PREF_SHOW_USER_ID, false);
    }

    public static boolean isAntiDeleteEnabled() {
        return getBoolean(PREF_ANTI_DELETE, false);
    }

    public static boolean isNoRestrictionsEnabled() {
        return getBoolean(PREF_NO_RESTRICTIONS, false);
    }

    public static boolean isSecretScreenshotsEnabled() {
        return getBoolean(PREF_SECRET_SCREENSHOTS, false);
    }

    public static boolean isLocalPremiumEnabled() {
        return getBoolean(PREF_LOCAL_PREMIUM, false);
    }
}

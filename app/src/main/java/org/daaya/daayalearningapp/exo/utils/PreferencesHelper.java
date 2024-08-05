package org.daaya.daayalearningapp.exo.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import org.daaya.daayalearningapp.exo.data.shared.PersistentPreference;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


import timber.log.Timber;

public class PreferencesHelper {

    static String USER_SERVERS_SEPARATOR = "\n";
    static String USER_SERVER_TIME_LAST_USED_SEPARATOR = " ";

    public PreferencesHelper() {}


    @Nullable
    private static Locale getPreferredLocale() {
        return getPreferredLocale(null);
    }

    @Nullable
    public static Locale getPreferredLocale(Context context) {
        String language = (context != null) ?
                PersistentPreference.Instance.PREFERRED_LOCALE.retrieve(context) :
                PersistentPreference.Instance.PREFERRED_LOCALE.retrieve();
        return !TextUtils.isEmpty(language) ? new Locale(language) : null;
    }

    public static void setPreferredLocale(Context context, Locale locale) {
        PersistentPreference.Instance.PREFERRED_LOCALE.save(context, locale.getLanguage());
    }

    public static void clearCredentials() {
        PersistentPreference.Instance.CREDENTIALS.save("");
    }



    public static Map<String, List<Triple<String, String, String>>> parseCredentials() {
        String currents = PersistentPreference.Instance.CREDENTIALS.retrieve();
        HashMap<String, List<Triple<String, String, String>>> set = new HashMap<>();
        if (!TextUtils.isEmpty(currents)) {
            String[] list = currents.split(";");
            if (list != null) {
                for (String ll : list) {
                    String[] tuple = ll.split(":");
                    if (tuple != null) {
                        List<Triple<String, String, String>> userSetPerSite = set.get(tuple[2]);
                        if (userSetPerSite == null) {
                            userSetPerSite = new ArrayList<>();
                        }
                        if (tuple.length > 3) {
                            Triple<String, String, String> triplet = new Triple<>(tuple[0], tuple[1], tuple[3]);
                            userSetPerSite.add(triplet);
                        } else {
                            Triple<String, String, String> triplet = new Triple<>(tuple[0], tuple[1], String.valueOf(userSetPerSite.size()));
                            userSetPerSite.add(triplet);
                        }
                        set.put(tuple[2], userSetPerSite);
                    }
                }
            }
        }
        return set;
    }

    public static void saveCredentials(Map<String, List<Triple<String, String, String>>> set) {
        StringBuilder stringBuilder = new StringBuilder();
        Set<Map.Entry<String, List<Triple<String, String, String>>>> list = set.entrySet();
        if (list != null) {
            for (Map.Entry<String, List<Triple<String, String, String>>> ll : list) {
                String servername = ll.getKey();
                for (Triple<String, String, String> triplet : ll.getValue()) {
                    if (triplet != null) {
                        String username = triplet.first;
                        String password = triplet.second;
                        String timestamp = triplet.third;
                        stringBuilder.append(username).append(":");
                        stringBuilder.append(password).append(":");
                        stringBuilder.append(servername).append(":");
                        stringBuilder.append(timestamp).append(";");
                    }
                }
            }
        }
        PersistentPreference.Instance.CREDENTIALS.save(stringBuilder.toString());
    }

    @Nullable
    public static String getCookieExpiry() {
        return PersistentPreference.Instance.COOKIE_EXPIRY.retrieve();
    }

    public static void setCookieExpiry(String cookieExpiry) {
        PersistentPreference.Instance.COOKIE_EXPIRY.save(cookieExpiry);
    }

    public static String checkCookie() {
        String cookie = PersistentPreference.Instance.COOKIE.retrieve();
        String cookie_expiry = PersistentPreference.Instance.COOKIE_EXPIRY.retrieve();
        DateTime expiryDate = null;
        try {
            //DateTimeFormatter dtf = DateTimeFormat.forPattern(ViewUtil.formatStringForSetCookieDate);
            //expiryDate = DateTime.parse(cookie_expiry, dtf);
            expiryDate = new DateTime(cookie_expiry);
        } catch (Exception e) {
            //do nothing. just dont crash for datetime parsing errors.
            Timber.e(e, "parsing of cookie Expiry failed");
        }
        int expiryGraceSeconds = getSignInGraceSecond();
        DateTime nowPlusGrace = new DateTime();
        nowPlusGrace = nowPlusGrace.plusSeconds(expiryGraceSeconds);
        if (TextUtils.isEmpty(cookie) || expiryDate == null || nowPlusGrace.isAfter(expiryDate)) {
            cookie = "";
        }
        return cookie;
    }

    public static void setCookie(String cookie) {
        PersistentPreference.Instance.COOKIE.save(cookie);
    }

    @Nullable
    public static String getDeviceId() throws ClassCastException {
        return PersistentPreference.Instance.DEVICE_ID.retrieve();
    }

    public static void setDeviceId(String deviceId) {
        PersistentPreference.Instance.DEVICE_ID.save(deviceId);
    }

    public static int getSignInGraceSecond() throws ClassCastException {
        return PersistentPreference.Instance.SIGNIN_GRACE_SECONDS.retrieve();
    }

    public static void setSignedInTime(DateTime signInTime) {
        PersistentPreference.Instance.SIGNEDIN_TIME.save(signInTime.toString());
    }

    public static DateTime getSignedInTime() {
        String date = PersistentPreference.Instance.SIGNEDIN_TIME.retrieve();
        return new DateTime(date);
    }

    public static void setSavePasswordFlag(boolean savePasswordFlag) {
        PersistentPreference.Instance.SAVE_PASSWORD.save(savePasswordFlag);
    }

    public static String getSavedRegionCode() {
        return PersistentPreference.Instance.SAVED_REGION_NAME.retrieve();
    }

    public static void setSavedRegionCode(String regionCode) {
        PersistentPreference.Instance.SAVED_REGION_NAME.save(regionCode);
    }

    public static void clearSavedRegionCode() {
        PersistentPreference.Instance.SAVED_REGION_NAME.saveDefault();
    }


    public static boolean getSavePasswordflag() {
        return PersistentPreference.Instance.SAVE_PASSWORD.retrieve();
    }

    public static void setSystemInfoStore(String systemInfoStore) {
        PersistentPreference.Instance.SYSTEMINFO_STORE.save(systemInfoStore);
    }

    public static String getSystemInfoStore() {
        return PersistentPreference.Instance.SYSTEMINFO_STORE.retrieve();
    }


    public void clear() {
        PersistentPreference.clear();
    }
}

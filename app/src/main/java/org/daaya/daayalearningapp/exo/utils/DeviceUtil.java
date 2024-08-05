/*--------------------------------------------------------------------------------------
 * Copyright (c) 2005-2015 Avigilon Corporation. All Rights Reserved.
 *
 * Unauthorized reproduction, use or disclosure, in whole or in part, of this file
 * and its contents, without express written consent of Avigilon Corp., is prohibited.
 *------------------------------------------------------------------------------------*/

package org.daaya.daayalearningapp.exo.utils;

import static android.os.Build.VERSION_CODES.N;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import org.daaya.daayalearningapp.exo.BuildConfig;
import org.daaya.daayalearningapp.exo.DaayaAndroidApplication;
import org.daaya.daayalearningapp.exo.R;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import timber.log.Timber;

public class DeviceUtil {
    private static final String HTTPS ="https://";
    private static final String HTTP ="http://";
    private static final String ALARM_RULE_STARTED_STRING = ":";
    private static final String ALARM_RULE_STARTED_STRING_SPACE = " ";
    private static final String SNAPSHOT_FOLDER_NAME = "AvigilonBlue";
    private static final int SNAPSHOT_COMPRESS_QUALITY = 90;


    public static int getScreenOrientation(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;

        int displayOrientation;
        if (displayWidth == displayHeight)
        {
            displayOrientation = Configuration.ORIENTATION_SQUARE;	// Just in case
        }
        else
        {
            if (displayWidth < displayHeight)
                displayOrientation = Configuration.ORIENTATION_PORTRAIT;
            else
                displayOrientation = Configuration.ORIENTATION_LANDSCAPE;
        }
        return displayOrientation;
    }

    public static boolean isLandscape(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x > size.y;
    }

	public static void adjustViewGroupPaddingForStatusBar(Activity activity, ViewGroup viewGroup) {
		int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		int statusSize = activity.getResources().getDimensionPixelSize(resourceId);
		viewGroup.setPadding(viewGroup.getPaddingLeft(), statusSize, viewGroup.getPaddingRight(), viewGroup.getPaddingBottom());
	}


	public static int getScreenOrientation(Activity context) {
		return context.getResources().getConfiguration().orientation;
	}


    public static float convertPixelToDensityPixel(Context context, int pixel) {
        return pixel / context.getResources().getDisplayMetrics().density;
    }


    private static boolean hasSoftwareKeys(Context context) {
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        display.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    public static int getPixelsFromDip(Context context, int dip) {
        return (int)(dip * context.getResources().getDisplayMetrics().density);
    }

    // Locks the device's rotation for a given activity
    public static void lockActivityOrientation(Activity activity)
    {
        if (activity == null)
            return;

        Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int configOrientation = activity.getResources().getConfiguration().orientation;
        int orientation = 0;

        // Takes in consideration the natural orientation for tablets and devices (Portrait vs Landscape)
        switch (configOrientation)
        {
            case Configuration.ORIENTATION_LANDSCAPE:
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90)
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                else
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270)
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                else
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
        }
        activity.setRequestedOrientation(orientation);
    }

    // Unlock the device's rotation for a given activity
    public static void unlockActivityOrientation(Activity activity)
    {
        if (activity == null)
            return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public static String getFormattedStringFromTimestamp(String timeStamp, DateTimeZone timeZone, String format) {
        DateTime dateTime = null;
        try {
            dateTime = new DateTime(timeStamp);
            if (timeZone != null) {
                dateTime = dateTime.withZone(timeZone);
            }
        } catch (IllegalArgumentException exception) {
            Timber.w(exception.getLocalizedMessage());
        }
        return (null != dateTime) ? dateTime.toString(format, Locale.getDefault()) : "Error parsing date time";
    }

    public static String curateHostString(String host){
        if (TextUtils.isEmpty(host)) return host;

        host = host.trim();
        if (host.charAt(host.length()-1) == '/') host = host.substring(0,host.length()-1);
        if (host.startsWith(HTTPS))              host = host.substring(8);
        if (host.startsWith(HTTP))               host = host.substring(7);
        return host;
    }

    public static String createErsUrl(String ersHost, int port, String roomId, String streamName, boolean secured) {
        Uri.Builder builder = new Uri.Builder();
        builder.encodedAuthority(curateHostString(ersHost) + ":" + port)
               .appendQueryParameter("roomId", roomId)
               .appendQueryParameter("streamName", streamName)
               .scheme(secured ? "wrtcs" : "wrtc");
        String url = builder.build().toString();
        return url;

    }

    /**
     * Utility method to create a SSLContext with a given TrustManager
     * @param trustManagers array of TrustManagers to facilitate checking of a certificate
     * @return an instance of SSLContext
     */
    public static SSLContext configureSSLContext(TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext;
        sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManagers, new SecureRandom());
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, new SecureRandom());
        Timber.e("sslContext=%s", sslContext);
        return sslContext;
    }

    /**
     * Utility method to create a HostnameVerifier that accepts all hostnames
     * @return an instance of HostnameVerifier
     */
    public static HostnameVerifier configureHostnameVerifier() {
        return (hostname, session) -> true;
    }

    public static SSLContext createOpenAccessSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustManagers =  new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        X509Certificate cert = x509Certificates[0];
                        try {
                            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
                            byte[] publicKey = messageDigest.digest(cert.getEncoded());
                            String certDetails = cert.toString();
                        } catch (NoSuchAlgorithmException | CertificateException exception) {
                            Timber.w(exception.getLocalizedMessage());
                        }
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        HostnameVerifier hostnameVerifier = DeviceUtil.configureHostnameVerifier();
        return configureSSLContext(trustManagers);

    }

    public static String printBytes(byte[] raw, int numBytes) {
        numBytes = Math.min(numBytes, raw.length);
        String retVal = "";

        for(int i = 0; i < numBytes; ++i) {
            retVal = retVal + String.format("%02x", raw[i]) + " ";
        }

        return retVal;
    }

    public static void printStackTrace() {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTrace : stackTraces) {
            Timber.d(stackTrace.toString());
        }
    }

    public static void restartApplication(Context context) {
        if (context != null) {
            Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            if (context instanceof Activity)
                ((Activity) context).finish();
            System.exit(0);
        }
    }

    public static void dismissKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void dialPhoneNumber(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static String getCurrentApkReleaseVersionName(Context context, boolean terse) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int debugBuild = (info.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE);
            if (debugBuild != 0 && !terse) {
                return info.versionName;
            } else {
                String versionName = info.versionName;
                versionName = versionName.replaceFirst("\\s*Release\\s*", "");
                versionName = versionName.split("-")[0];
                if (info.versionName.contains("dirty")){
                    versionName = versionName + " dirty";
                }
                return versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    public static int getCurrentApkReleaseVersionCode(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    public static String getAlarmRuleFromDescription(@NonNull String description) {
        boolean validString = description.length() != 0 && !description.trim().equals(ALARM_RULE_STARTED_STRING);
        if (validString) {
            int startingIndex = description.indexOf(ALARM_RULE_STARTED_STRING);
            if (-1 == startingIndex) {
                return description;
            } else {
                String alarmTitle = description.substring(startingIndex + ALARM_RULE_STARTED_STRING.length(), description.length());
                boolean hasWhiteSpaceInfront = alarmTitle.length() != 0 && alarmTitle.startsWith(ALARM_RULE_STARTED_STRING_SPACE);
                return (hasWhiteSpaceInfront) ? alarmTitle.substring(1, alarmTitle.length()) : alarmTitle;
            }
        } else {
            return "";
        }
    }

    public static String getAlarmCameraNameFromDescription(@NonNull String description) {
        boolean validString = description.length() != 0 && !description.trim().equals(ALARM_RULE_STARTED_STRING);
        if (validString) {
            int startingIndex = description.indexOf(ALARM_RULE_STARTED_STRING);
            return (-1 == startingIndex) ? description : description.substring(0, startingIndex);
        } else {
            return "";
        }
    }



    public static boolean isPermissionGranted(Context context, @NonNull String permission) {
        boolean isGranted;
        if(Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
            isGranted = (0 == permissionCheck);
        } else {
            isGranted = true;
        }

        return isGranted;
    }

    public static boolean isScreenLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }


    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static String getDeviceCodename() {
        return Build.PRODUCT;
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }


    public static void enableFullScreen(View contentView) {
        if (contentView != null)
            contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    public static String printBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        StringBuilder string = new StringBuilder("Bundle {");
        for (String key : bundle.keySet()) {
            string.append(" ").append(key).append(" => ").append(bundle.get(key)).append(";");
        }
        string.append(" }");
        return string.toString();
    }


    public static String returnDefaultIfEmpty(String input, String defaultStr) {
        if (TextUtils.isEmpty(defaultStr))
            defaultStr = "N/A";
        return TextUtils.isEmpty(input) ? defaultStr : input;
    }

    public static boolean isUUIDZero(String id) {
        return "00000000-0000-0000-0000-000000000000".equalsIgnoreCase(id);
    }

    public static void showLocaleList(Context context) {
        if (Build.VERSION.SDK_INT >= N) {
            LocaleList localeList = LocaleList.getDefault();
            for (int ii = 0; ii < localeList.size(); ii++) {
                Locale locale = localeList.get(ii);
                Timber.e("preferred locale-%d = %s", ii+1, locale.toString());
                Configuration configuration = new Configuration(context.getResources().getConfiguration());
                configuration.setLocale(locale);
                String translatedString = context.createConfigurationContext(configuration).getResources().getString(R.string.apply);
                Timber.e("translatedString for R.string.apply = %s", translatedString);
            }
            Locale currentLocale = context.getResources().getConfiguration().getLocales().get(0);
            Timber.e("current locale = %s", currentLocale.toString());
        } else {
            Locale currentLocale = context.getResources().getConfiguration().locale;
            Timber.e("current locale = %s", currentLocale.toString());
        }
        Locale defaultLocale = Locale.getDefault();
        Timber.e("default locale = %s", defaultLocale.toString());
    }

    public static Context setLocale(Context c) {
        return updateResources(c, DeviceUtil.resolveLocale(c));
    }

    public static Context setNewLocale(Context c, String language) {
        Locale locale = new Locale(language);
        PreferencesHelper.setPreferredLocale(c, locale);
        return updateResources(c, locale);
    }

    @Nullable
    public static String getLanguage(Context c) {
        Locale preferredLocale = PreferencesHelper.getPreferredLocale(c);
        if (preferredLocale != null)
            return preferredLocale.getLanguage();
        else
            return null;
    }


    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);
        changeLocaleForContext(context, locale);
        Context appContext = DaayaAndroidApplication.getContext();
        changeLocaleForContext(appContext, locale);
        return context;
    }

    private static void changeLocaleForContext(Context context, Locale locale) {
        if (context != null) {
            Resources res = context.getResources();
            Configuration config = new Configuration(res.getConfiguration());
            config.locale = locale;
            config.setLocale(locale);
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
    }

    public static Locale resolveLocale(@NonNull Context context) {
        Locale preferredLocale = PreferencesHelper.getPreferredLocale(context);
        return preferredLocale == null ? getSystemLocale(context.getResources()) : preferredLocale;
    }

    public static Locale getSystemLocale(Resources res) {
        if (Build.VERSION.SDK_INT >= N) {
            LocaleList localeList = LocaleList.getDefault();
            return localeList.getFirstMatch(BuildConfig.BUILD_SUPPORTED_LANGUAGES);
        } else {
            return res.getConfiguration().locale;
        }
    }

    private static Map<String, List<Locale>> languageNameToLocale = null;

    @Nullable
    public static Locale getMatch(String languageCode) {
        if (languageNameToLocale == null) {
            languageNameToLocale = new HashMap<>();
            Locale[] availableLocales = Locale.getAvailableLocales();
            for (Locale locale : availableLocales) {
                List<Locale> locales = languageNameToLocale.get(locale.getLanguage());
                if (locales == null) {
                    locales = new ArrayList<>();
                    languageNameToLocale.put(locale.getLanguage(), locales);
                }
                if (locale.getCountry().equalsIgnoreCase(locale.getLanguage()))
                    locales.add(0,locale);
                else
                    locales.add(locale);
            }
        }
        List<Locale> matchedLocales = languageNameToLocale.get(languageCode);
        if (matchedLocales == null || matchedLocales.isEmpty()) {
            return null;
        }
        return matchedLocales.get(0);
    }

    public static Locale getAcceptLanguageLocale() {
        Context context = DaayaAndroidApplication.getContext();
        Locale locale = resolveLocale(context);
        if (TextUtils.isEmpty(locale.getCountry())) {
            return getMatch(locale.getLanguage());
        } else {
            return locale;
        }
    }

    public static String getString (@NonNull Context context, @StringRes int resId, Locale locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration).getResources().getString(resId);
    }

    public static String resolveString(@NonNull Context context, @StringRes int resId) {
        return getString(context, resId, resolveLocale(context));
    }

    public static void throwOrLogError(String errorMessage) {
        if (BuildConfig.DEBUG)
            throw new RuntimeException(errorMessage);
        else
            Timber.e(errorMessage);
    }
}

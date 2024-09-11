package org.daaya.daayalearningapp.exo;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import org.daaya.daayalearningapp.exo.data.DefaultVideosRepository;
import org.daaya.daayalearningapp.exo.data.shared.PersistentPreference;
import org.daaya.daayalearningapp.exo.utils.DeviceUtil;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

@HiltAndroidApp
public class DaayaAndroidApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {
    public @Inject DefaultVideosRepository videosRepository;
    public static String baseUrl = "https://api.daaya.org/";

    private static boolean backgrounded = true;

    private static DaayaAndroidApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.DebugTree tree = new Timber.DebugTree();
            Timber.plant(tree);
        }
        //JodaTimeAndroid.init(this);

        mScreenOffBroadcastReceiver = new ScreenOffBroadcastReceiver();
        registerActivityLifecycleCallbacks(this);
        registerReceiver(mScreenOffBroadcastReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
        isScreenOffBroadcastReceiverRegistered = true;

        //Install TLS1.3 and other SecurityProviders via Conscryt
        //Security.insertProviderAt(new OpenSSLProvider("conscrypt"), 1);

        instance = this;
    }

    public static DaayaAndroidApplication getContext() {
        return instance;
    }

    @Override
    public void onTrimMemory(int level) {
        if (level <= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            backgrounded = true;
            handleApplicationWentIntoBackground();
        }
        super.onTrimMemory(level);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (backgrounded) {
            if (!isScreenOffBroadcastReceiverRegistered) {
                registerReceiver(mScreenOffBroadcastReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
            }
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        String pushToken = PersistentPreference.Instance.UA_CHANNEL_ID.retrieve();
        Timber.i("onActivityResumed(UA Channel Id=%s)", pushToken);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if (BuildConfig.DEBUG) {
            String pushToken = PersistentPreference.Instance.UA_CHANNEL_ID.retrieve();
            Timber.i("onActivityPaused(UA Channel Id=%s)", pushToken);
            Timber.i("onActivityPaused(activity=%s)", activity);
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            String pushToken = PersistentPreference.Instance.UA_CHANNEL_ID.retrieve();
            Timber.i("onActivityCreated(UA Channel Id=%s)", pushToken);
            Timber.i("onActivityCreated(activity=%s, savedInstancestate=%s)", activity, savedInstanceState);
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if (BuildConfig.DEBUG) {
            String pushToken = PersistentPreference.Instance.UA_CHANNEL_ID.retrieve();
            Timber.i("onActivityStopped(UA Channel Id=%s)", pushToken);
            Timber.i("onActivityStopped(activity=%s)", activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        if (BuildConfig.DEBUG) {
            Timber.i("onActivitySaveInstanceState(activity=%s, outState=%s)", activity, outState);
        }
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (BuildConfig.DEBUG) {
            Timber.i("onActivityDestroyed(activity=%s)", activity);
        }
    }

    public static boolean wasBackgrounded() {
        return backgrounded;
    }

    public static void flagForegrounded() {
        backgrounded = false;
    }



    private void handleApplicationWentIntoBackground() {
        if (isScreenOffBroadcastReceiverRegistered) {
            unregisterReceiver(mScreenOffBroadcastReceiver);
        }
        isScreenOffBroadcastReceiverRegistered = false;
    }

    private ScreenOffBroadcastReceiver mScreenOffBroadcastReceiver;
    private boolean isScreenOffBroadcastReceiverRegistered;


    class ScreenOffBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            backgrounded = true;
            handleApplicationWentIntoBackground();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        Context newBaseContext = DeviceUtil.setLocale(base);
        super.attachBaseContext(newBaseContext);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DeviceUtil.setLocale(this);
    }
}

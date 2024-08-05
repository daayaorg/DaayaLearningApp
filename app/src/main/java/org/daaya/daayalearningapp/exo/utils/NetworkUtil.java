package org.daaya.daayalearningapp.exo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import retrofit2.adapter.rxjava.HttpException;

public class NetworkUtil {

    /**
     * Returns true if the Throwable is an instance of RetrofitError with an
     * http Status code equals to the given one.
     */
    public static boolean isHttpStatusCode(Throwable throwable, int statusCode) {
        return throwable instanceof HttpException
                && ((HttpException) throwable).code() == statusCode;
    }

    public static boolean isNetworkConnected(Context context) {
        if (isAirplaneModeOn(context))
            return false;
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Gets the state of Airplane Mode.
     *
     * @param context
     * @return true if enabled.
     */
    private static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
}
package org.daaya.daayalearningapp.exo.network;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.daaya.daayalearningapp.exo.data.shared.PersistentPreference;
import org.daaya.daayalearningapp.exo.utils.DeviceUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * An OkHttp interceptor which updates the cookie information from response.
 */

public final class AppOkHttpInterceptor implements Interceptor {
    private static final String   COOKIE_SEPARATOR=";";
    private static final String   COOKIE_HEADER_PREFIX="Set-Cookie";
    private static final String[] COOKIE_PREFIXES= {".VIQAUTH", ".DEVAZUREAUTH" };
    private static final String   COOKIE_EXPIRY_PREFIX="expires=";
    private static final String   COOKIE_TIMESTAMP_FORMAT="EEE, dd-MMM-yyyy HH:mm:ss z";
    /**
     * Observe and extract Cookie information for HTTP response headers
     * and update the PersistentPreference, so it can be used for
     * the next REST API call
     */
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        try {

            Locale locale = DeviceUtil.getAcceptLanguageLocale();
            Request request = chain.request();
            if (locale != null) {
                String languageTag = locale.toLanguageTag();
                request = chain.request().newBuilder().addHeader("Accept-Language", languageTag).build();
            }
            Response response = chain.proceed(request);
            if (response.code() >= 200 && response.code() < 300) {
                Headers headers = response.headers();
                ArrayList<String> cookies = new ArrayList<>();
                for (int i = 0, count = headers.size(); i < count; i++) {
                    if (headers.name(i).equalsIgnoreCase(COOKIE_HEADER_PREFIX)) {
                        String[] values = headers.value(i).split(COOKIE_SEPARATOR);
                        if (!TextUtils.isEmpty(values[0])) {
                            cookies.add(values[0]);
                        }
                        for(String cookie_prefix: COOKIE_PREFIXES) {
                            if (values.length > 1 && values[0].startsWith(cookie_prefix) && values[1].contains(COOKIE_EXPIRY_PREFIX)) {
                                String expiryDate = values[1].substring(9);
                                DateTimeFormatter dtf = DateTimeFormat.forPattern(COOKIE_TIMESTAMP_FORMAT).withLocale(Locale.ENGLISH);
                                DateTime ex = DateTime.parse(expiryDate, dtf);
                                PersistentPreference.Instance.COOKIE_EXPIRY.save(ex.toString());
                            }
                        }
                    }
                }
                StringBuilder cookieAggregated = new StringBuilder();
                for(String cookie: cookies) {
                    cookieAggregated.append(cookie).append(";");
                }
                String cookie = cookieAggregated.toString();
                if(!TextUtils.isEmpty(cookie))
                    PersistentPreference.Instance.COOKIE.save(cookieAggregated.toString());
            }
            return response;
        } catch (UnknownHostException unknownHostException) {
            throw unknownHostException;
        } catch (ConnectException connectException) {
            throw connectException;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}

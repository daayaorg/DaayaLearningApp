package org.daaya.daayalearningapp.exo.data.shared;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.collection.SparseArrayCompat;

import org.daaya.daayalearningapp.exo.DaayaAndroidApplication;
import org.daaya.daayalearningapp.exo.R;
import org.daaya.daayalearningapp.exo.ui.video.VideoActivity;

import java.lang.reflect.Field;

import timber.log.Timber;


// A model class for SQL data types and their mapping to Java types
public class PersistentPreference<T> {
    private final int resIdForKey;
    private final T defaultValue;
    public static final String PREF_FILE_NAME = "avigilon_helios_pref_file";

    private PersistentPreference(int resIdForKey, T defaultValue) {
        this.resIdForKey = resIdForKey;
        this.defaultValue = defaultValue;
    }

    public static class Instance {
        public static PersistentPreference<String> CREDENTIALS = new PersistentPreference<>(R.string.key_credentials, "");
        public static PersistentPreference<String> COOKIE_EXPIRY = new PersistentPreference<>(R.string.key_cookie_expiry,  "");
        public static PersistentPreference<String> COOKIE = new PersistentPreference<>(R.string.key_cookie,  "");
        public static PersistentPreference<String> DEVICE_ID = new PersistentPreference<>(R.string.key_device_id,  "");
        public static PersistentPreference<Integer> SIGNIN_GRACE_SECONDS = new PersistentPreference<>(R.string.key_signin_grace_seconds,  5);
        public static PersistentPreference<String> SIGNEDIN_TIME = new PersistentPreference<>(R.string.key_signedin_time,  "");
        public static PersistentPreference<String> UA_CHANNEL_ID = new PersistentPreference<>(R.string.key_ua_channel_id,  "");
        public static PersistentPreference<Boolean> SAVE_PASSWORD = new PersistentPreference<>(R.string.key_save_password_flag, false);
        public static PersistentPreference<String> SYSTEMINFO_STORE = new PersistentPreference<>(R.string.key_systeminfo_store, "");
        public static PersistentPreference<String> SAVED_REGION_NAME = new PersistentPreference<>(R.string.key_region_name,"");
        public static PersistentPreference<String> PREFERRED_LOCALE = new PersistentPreference<>(R.string.key_preferred_locale,"");

        static {
            init();
        }
    }

    private static SparseArrayCompat<Class<?>> classHashMap;
    private static SparseArrayCompat<Object> defaultValues;

    static void init() {
        if (classHashMap == null) {
            classHashMap = new SparseArrayCompat<>();
            defaultValues = new SparseArrayCompat<>();
            Field[] fields =  Instance.class.getDeclaredFields();
            for (Field f : fields) {
                try {
                    PersistentPreference<?> p = (PersistentPreference<?>) f.get(new Object());
                    Class<?> cls = p.defaultValue.getClass();
                    classHashMap.put(p.resIdForKey, cls);
                    defaultValues.put(p.resIdForKey, p.defaultValue);
                } catch (IllegalAccessException e) {
                    Timber.d(e, "Exception in init");
                }
            }
        }
    }

    public static void clear() {
        SharedPreferences sharedPreferences = DaayaAndroidApplication.getContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    public T retrieve() {
        return retrieve(resIdForKey, null);
    }

    public T retrieve(Context context) {
        return retrieve(resIdForKey, context);
    }

    public static <T> T retrieve(PersistentPreference<?> pref) {
        return retrieve(pref.resIdForKey);
    }

    public static <T> T retrieve(int resIdForKey) {
        return retrieve(resIdForKey, null);
    }
    public void save(T t) {
        save(null, t);
    }
    public void save(Context context, T t) {
        save(resIdForKey, t, context);
    }

    public void saveDefault() {
        T t = defaultValue(resIdForKey);
        save(resIdForKey, t);
    }

    private static <T> void save(Context context, PersistentPreference<?> pref, T t) {
        save(pref.resIdForKey, t, context);
    }

    private static <T> void save(int resIdForKey, T t) {
        save (resIdForKey, t, null);
    }

    public static <T> T defaultValue(int resIdForKey) {
        init();
        return (T) defaultValues.get(resIdForKey);
    }


    public static <T> T retrieve(int resIdForKey, Context context) {
        init();
        if (context == null)
            context = DaayaAndroidApplication.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        String key = context.getString(resIdForKey);
        Class<?> cls = classHashMap.get(resIdForKey);
        if (cls == null) {
            return null;
        }
        if (cls == String.class) {
            String retVal =  sharedPreferences.getString(key, (String) defaultValues.get(resIdForKey));
            if (TextUtils.isEmpty(retVal)) {
                return null;
            }
            return (T) retVal;
        }

        if (cls == Integer.class) {
            Integer retVal =  sharedPreferences.getInt(key, (Integer) defaultValues.get(resIdForKey));
            return (T) retVal;
        }

        if (cls == Boolean.class) {
            Boolean retVal =  sharedPreferences.getBoolean(key, (Boolean) defaultValues.get(resIdForKey));
            return (T) retVal;
        }

        if (cls == Long.class) {
            Long retVal =  sharedPreferences.getLong(key, (Long) defaultValues.get(resIdForKey));
            return (T) retVal;
        }
        return null;
    }

    void  onItemClick(Context context, int position) {
        startActivity(context, new Intent(context, VideoActivity.class), null);
    }
    public static <T> void save(int resIdForKey, T t, Context context) {
        init();
        if (context == null) {
            context = DaayaAndroidApplication.getContext();
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Class<?> clas = classHashMap.get(resIdForKey);
        if (clas == null) {
            throw new RuntimeException("Resource not found for " + resIdForKey);
        }
        String key = context.getString(resIdForKey);
        if (clas == Boolean.class) {
            editor.putBoolean(key, (Boolean) t);
        }

        if (clas == String.class) {
            editor.putString(key, (String) t);
        }

        if (clas == Integer.class) {
            editor.putInt(key, (Integer) t);
        }

        if (clas == Long.class) {
            editor.putLong(key, (Long) t);
        }
        editor.apply();
    }
}

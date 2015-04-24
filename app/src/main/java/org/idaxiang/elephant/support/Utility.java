package org.idaxiang.elephant.support;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import org.idaxiang.elephant.model.AccountBean;
import org.idaxiang.elephant.support.lib.MyAsyncTask;
import org.idaxiang.elephant.support.utils.MD5;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Azzssss on 15-1-1.
 */
public class Utility {

    private static final String TAG = Utility.class.getSimpleName();

    public static <T> T findViewById(View v, int id) {
        return (T) v.findViewById(id);
    }

    public static <T> T findViewById(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    public static void bindOnClick(final Object obj, Object... viewsAndMethod) {
        final Class<?> clazz = obj.getClass();
        String method = viewsAndMethod[viewsAndMethod.length - 1].toString();
        try {
            final Method m = findMethod(clazz, method);
            m.setAccessible(true);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        m.invoke(obj);
                    } catch (InvocationTargetException e) {

                    } catch (IllegalAccessException e) {

                    }
                }
            };

            for (Object o : viewsAndMethod) {
                if (o instanceof View) {
                    ((View) o).setOnClickListener(listener);
                }
            }
        } catch (NoSuchMethodException e) {

        }
    }

    public static void bindOnLongClick(final Object obj, Object... viewsAndMethod) {
        final Class<?> clazz = obj.getClass();
        String method = viewsAndMethod[viewsAndMethod.length - 1].toString();
        try {
            final Method m = findMethod(clazz, method);
            m.setAccessible(true);
            View.OnLongClickListener listener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    try {
                        return Boolean.parseBoolean(m.invoke(obj).toString());
                    } catch (InvocationTargetException e) {

                    } catch (IllegalAccessException e) {

                    }

                    return false;
                }
            };

            for (Object o : viewsAndMethod) {
                if (o instanceof View) {
                    ((View) o).setOnLongClickListener(listener);
                }
            }
        } catch (NoSuchMethodException e) {

        }
    }

    public static Method findMethod(Class<?> clazz, String name) throws NoSuchMethodException {
        Class<?> cla = clazz;
        Method method = null;

        do {
            try {
                method = cla.getDeclaredMethod(name);
            } catch (NoSuchMethodException e) {
                method = null;
                cla = cla.getSuperclass();
            }
        } while (method == null && cla != Object.class);

        if (method == null) {
            throw new NoSuchMethodException();
        } else {
            return method;
        }
    }

    /**
     * Parse a URL query and fragment parameters into a key-value bundle.
     */
    public static Bundle parseUrl(String url) {
        // hack to prevent MalformedURLException
        url = url.replace("weiboconnect", "http");
        try {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (MalformedURLException e) {
            return new Bundle();
        }
    }

    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                try {
                    params.putString(URLDecoder.decode(v[0], "UTF-8"),
                            URLDecoder.decode(v[1], "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return params;
    }

    public static long calcTokenExpiresInDays(AccountBean account) {
        long days = TimeUnit.MILLISECONDS
                .toDays(account.getExpires_time() - System.currentTimeMillis());
        return days;
    }

    public static String encodeUrl(Map<String, String> param) {
        if (param == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        Set<String> keys = param.keySet();
        boolean first = true;

        for (String key : keys) {
            String value = param.get(key);
            //pain...EditMyProfileDao params' values can be empty
            if (!TextUtils.isEmpty(value) || key.equals("description") || key.equals("url")) {
                if (first) {
                    first = false;
                } else {
                    sb.append("&");
                }
                try {
                    sb.append(URLEncoder.encode(key, "UTF-8")).append("=")
                            .append(URLEncoder.encode(param.get(key), "UTF-8"));
                } catch (UnsupportedEncodingException e) {

                }
            }
        }

        return sb.toString();
    }

    public static void cancelTasks(MyAsyncTask... tasks) {
        for (MyAsyncTask task : tasks) {
            if (task != null) {
                task.cancel(true);
            }
        }
    }

    public static boolean isTaskStopped(MyAsyncTask task) {
        return task == null || task.getStatus() == MyAsyncTask.Status.FINISHED;
    }

    public static String getSign(Context context, String pkgName)
    {
        PackageInfo packageInfo;
        try
        {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName,
                    64);
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {

            return null;
        }
        for (int j = 0; j < packageInfo.signatures.length; j++) {
            byte[] str = packageInfo.signatures[j].toByteArray();
            if (str != null) {
                return MD5.hexdigest(str);
            }
        }
        return null;
    }

//    public static int getCurrentLanguage(Context context) {
//        int lang = Settings.getInstance(context).getInt(Settings.LANGUAGE, -1);
//        if (lang == -1) {
//            String language = Locale.getDefault().getLanguage();
//            String country = Locale.getDefault().getCountry();
//
//            if (DEBUG) {
//                Log.d(TAG, "Locale.getLanguage() = " + language);
//            }
//
//            if (language.equalsIgnoreCase("zh")) {
//                if (country.equalsIgnoreCase("CN")) {
//                    lang = 1;
//                } else {
//                    lang = 2;
//                }
//            } else {
//                lang = 0;
//            }
//        }
//
//        return lang;
//    }

}

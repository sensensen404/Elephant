package org.idaxiang.elephant.support.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import org.idaxiang.elephant.utils.AppLogger;

import static org.idaxiang.elephant.BuildConfig.DEBUG;

/**
 * Created by Azzssss on 15-1-18.
 */
public class Util {

    public static boolean isWIFI = false;

    public static String replaceSymbol(String content) {
        content = content.replaceAll("(“)([\u4e00-\u9fa5|\\s]+)", "「$2");
        content = content.replaceAll("([\u4e30-\u9fa5|\\s]+)(”)", "$1」");
        content = content.replaceAll("(‘)([\u4e00-\u9fa5|\\s]+)", "『$2");
        content = content.replaceAll("([\u4e00-\u9fa5|\\s]+)(’)", "$1』");
        content = content.replaceAll("……", "…");
        return content;
    }

    /**
     * 根据一个图片的url获取其在本机缓存中的存储url
     *
     * from:
     * https://avatars3.githubusercontent.com/u/3348483
     * to:
     * /storage/emulated/0/Android/data/com.cundong.izhihu/cache/f72a0397abad8edbd98a8d2a701464ee.jpg
     *
     * @param context
     * @param imageUrl
     * @return
     */
    public static String getCacheImgFilePath(Context context, String imageUrl) {

        return SDCardUtils.getExternalCacheDir(context)
                + MD5Util.encrypt(imageUrl) + ".bin";
    }

    public static boolean readNetworkState(Context context) {
        if (context == null) return false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
            if (DEBUG) {
                AppLogger.d("Network connected");
            }

            isWIFI = (cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI);

            return true;
        } else {
            if (DEBUG) {
                AppLogger.d("Network disconnected");
            }

            return false;
        }
    }
}

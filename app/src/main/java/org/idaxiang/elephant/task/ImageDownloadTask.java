package org.idaxiang.elephant.task;

import android.content.Context;
import android.text.TextUtils;

import org.idaxiang.elephant.support.http.HttpUtility;
import org.idaxiang.elephant.support.lib.MyAsyncTask;
import org.idaxiang.elephant.support.utils.FileUtils;
import org.idaxiang.elephant.support.utils.SDCardUtils;
import org.idaxiang.elephant.support.utils.StreamUtils;
import org.idaxiang.elephant.support.utils.Util;
import org.idaxiang.elephant.utils.AppLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Azzssss on 15-1-19.
 */
public class ImageDownloadTask extends MyAsyncTask<String, String, String> {

    private Context mContext;

    public ImageDownloadTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected String doInBackground(String... params) {

        String externalCacheDir = SDCardUtils.getExternalCacheDir(mContext);

        if ( params.length == 0 || TextUtils.isEmpty(externalCacheDir) )
            return null;

        File file = null;
        for (String param : params) {

            if (TextUtils.isEmpty(param)) {
                AppLogger.e("no download, the image url is empty");
                continue;
            }

            String filePath = Util.getCacheImgFilePath(mContext, param);
            file = new File(filePath);

            boolean needDownload = true;

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                long fileSize = FileUtils.getFileSize(filePath);

                if (fileSize == 0) {
                    // need re download
                } else {
                    needDownload = false;
                }
            }

            if (needDownload) {
                InputStream in = null;
                OutputStream out = null;

                // from web
                try {
                    in = HttpUtility.getStream(mContext, param, null);
                    out = new FileOutputStream(file);

                    StreamUtils.copy(in, out);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    StreamUtils.close(out);
                    StreamUtils.close(in);
                }
            } else {
                // no need download
            }

            publishProgress(param);
            AppLogger.e("download complete " + param);
        }

        return null;
    }
}

package org.idaxiang.elephant.api;

import android.util.Log;

import org.idaxiang.elephant.support.http.BaseParameters;
import org.idaxiang.elephant.support.http.HttpUtility;
import org.json.JSONArray;
import org.json.JSONObject;

import static org.idaxiang.elephant.BuildConfig.DEBUG;

/**
 * Created by Azzssss on 15-1-2.
 */
public class BaseElephantApi {
    private static final String TAG = BaseElephantApi.class.getSimpleName();

    // Http Methods
    protected static final String HTTP_GET = HttpUtility.GET;
    protected static final String HTTP_POST = HttpUtility.POST;

    protected static JSONObject request(String url, BaseParameters params, String method) throws Exception {
        return request(url, params, method, JSONObject.class);
    }

    protected static JSONArray requestArray(String url, BaseParameters params, String method) throws Exception {
        return request(url, params, method, JSONArray.class);
    }

    protected static <T> T request(String url, BaseParameters params, String method, Class<T> jsonClass) throws Exception {

        String jsonData = HttpUtility.doRequest(url, params, method);

        if (DEBUG) {
            Log.d(TAG, "jsonData = " + jsonData);
        }

        if (jsonData != null && (jsonData.contains("{") || jsonData.contains("["))) {
            return jsonClass.getConstructor(String.class).newInstance(jsonData);
        } else {
            return null;
        }
    }
}

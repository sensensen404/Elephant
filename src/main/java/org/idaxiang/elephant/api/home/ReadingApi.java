package org.idaxiang.elephant.api.home;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.idaxiang.elephant.api.BaseApi;
import org.idaxiang.elephant.model.ReadingModel;
import org.idaxiang.elephant.support.http.ElephantParameters;
import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

import static org.idaxiang.elephant.BuildConfig.DEBUG;

/**
 * Created by Azzssss on 14-12-31.
 */
public class ReadingApi extends BaseApi {

    private static final String TAG = ReadingApi.class.getSimpleName();

    public static List<ReadingModel> fetchReadingPage(String id) {
        ElephantParameters params = new ElephantParameters();
        params.put("args[0]", id);

        try {
//            http://backend.idaxiang.org/api/views/articles_view?args[0]=all&args[1]=all&created=&created_1=&limit=0
            JSONArray json = requestSimpleArray("http://backend.idaxiang.org/api/views/articles_node", params, HTTP_GET);

            Log.e(TAG, json.length() + " ");

            Gson gson = new Gson();
            Type type = new TypeToken<List<ReadingModel>>(){}.getType();
            List<ReadingModel> value = gson.fromJson(json.toString(), type);
            Log.d(TAG, "response size, " + value.size());
            return value;
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "Cannot fetch home timeline, " + e.getClass().getSimpleName());
                Log.d(TAG, Log.getStackTraceString(e));
            }
            return null;
        }
    }
}

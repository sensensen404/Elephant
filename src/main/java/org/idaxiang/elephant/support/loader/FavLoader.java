package org.idaxiang.elephant.support.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.idaxiang.elephant.model.FavModel;
import org.idaxiang.elephant.utils.AppLogger;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Azzssss on 15-1-2.
 */
public class FavLoader extends AsyncTaskLoader<List<FavModel>> {

    public FavLoader(Context context){
        super(context);
    }

    @Override
    public List<FavModel> loadInBackground() {
        List<FavModel> favModels = DataSupport.where("fav = ?", "1").find(FavModel.class);
        return favModels;
    }

    @Override
    protected void onStartLoading() {
        AppLogger.e("onStartLoading");
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        AppLogger.e("onStopLoading");
        cancelLoad();
    }
}

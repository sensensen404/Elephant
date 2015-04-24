package org.idaxiang.elephant.cache;

import org.idaxiang.elephant.api.home.HomeTimeLineApi;
import org.idaxiang.elephant.model.MsgModel;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azzssss on 14-12-29.
 */
public class TimeLineApiCache {

    public List<MsgModel> MsgListModel;

    public TimeLineApiCache(){
        MsgListModel = new ArrayList<>();
    }

    private boolean isGetNewData = false;

    public void loadFromCache(){
        MsgListModel = DataSupport.findAll(MsgModel.class);
    }

    public void loadFromWeb(){
        List<MsgModel> result = HomeTimeLineApi.fetchHomeTimeLine();
        if(result != null){
            isGetNewData = true;
            clearCache();
            clearList();
            MsgListModel.addAll(result);
            DataSupport.saveAll(result);
        }

    }

    public boolean clearCache(){
        int status = DataSupport.deleteAll(MsgModel.class);
        return status == 1 ? true : false;
    }

    public void clearList(){
        MsgListModel.clear();
    }

    public boolean isGetNewData() {
        return isGetNewData;
    }

    public void setGetNewData(boolean isGetNewData) {
        this.isGetNewData = isGetNewData;
    }
}

package org.idaxiang.elephant.support.adapter;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;

import org.idaxiang.elephant.R;
import org.idaxiang.elephant.bus.FavEvent;
import org.idaxiang.elephant.model.FavModel;
import org.idaxiang.elephant.model.MsgModel;
import org.idaxiang.elephant.ui.activity.ReadingActivity;
import org.idaxiang.elephant.utils.AppLogger;
import org.litepal.crud.DataSupport;

import java.util.List;

import de.greenrobot.event.EventBus;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    private List<MsgModel> mDataset;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tv_title;
        public TextView tv_time;
        public TextView tv_keep;
        public TextView tv_summary;

        public ViewHolder(View v) {
            super(v);
            tv_title = (TextView)v.findViewById(R.id.tv_title);
            tv_summary = (TextView)v.findViewById(R.id.tv_summary);
            tv_time = (TextView)v.findViewById(R.id.tv_time);
            tv_keep = (TextView)v.findViewById(R.id.tv_keep);
        }

    }

    public TimelineAdapter(List<MsgModel> myDataset, Context context) {

        mDataset = myDataset;
        mContext = context;
    }

    @Override
    public TimelineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_timeline, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ReadingActivity.class);
                intent.putExtra("id", mDataset.get(vh.getPosition()).getNid());
                intent.putExtra("title", mDataset.get(vh.getPosition()).getTitle());
                mContext.startActivity(intent);
            }
        });

        vh.tv_keep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgModel model = mDataset.get(vh.getPosition());
                new FavTask().execute(model,vh.tv_keep);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MsgModel msgModel = mDataset.get(position);
        holder.tv_title.setText(mDataset.get(position).getTitle());
        holder.tv_summary.setText(mDataset.get(position).getSummary());
        holder.tv_time.setText(mDataset.get(position).getCreated().split("T")[0]);

        int count = DataSupport.where("title = ? and fav = ?",msgModel.getTitle(),"1").count(FavModel.class);
        if(count > 0){
            holder.tv_keep.setText("取消收藏");
        } else {
            holder.tv_keep.setText("收藏");
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static final int FAVED = 0;
    public static final int FAV_SUCCESS = 1;
    public static final int FAV_ERROR = 2;

    private class FavTask extends AsyncTask<Object, Void, Integer> {
        private MsgModel msgModel;
        private TextView tv_keep;

        @Override
        protected Integer doInBackground(Object... params) {
            msgModel = (MsgModel)params[0];
            tv_keep = (TextView)params[1];

            List<FavModel> favs = DataSupport.where("nid = ? and fav = ?",msgModel.getNid(),"1").find(FavModel.class);
            if(favs != null && favs.size() > 0){
                FavModel fav = favs.get(0);
                ContentValues values = new ContentValues();
                values.put("fav", false);
                DataSupport.update(FavModel.class,values,fav.getId());
                return FAVED;
            } else {
                AVObject fav = new AVObject("Favorite");
                fav.put("nid", msgModel.getNid());
                fav.put("title", msgModel.getTitle());
                fav.put("summary", msgModel.getSummary());
                fav.put("username",msgModel.getUsername());
                fav.put("userid",msgModel.getUseruid());
                fav.put("taxonomy",msgModel.getTaxonomy());
                fav.put("changed",msgModel.getChanged());
                fav.put("created",msgModel.getCreated());
                try {
                    fav.save();
                    FavModel favModel = new FavModel(msgModel.getNid(),msgModel.getTitle(),msgModel.getCreated(),msgModel.getChanged(),msgModel.getSummary(),msgModel.getUseruid(),msgModel.getUsername(),msgModel.getTaxonomy(),true);
                    favModel.save();

                    return FAV_SUCCESS;
                } catch (AVException e) {
                    e.getMessage();
                    return FAV_ERROR;
                }
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer == FAVED){
                Toast.makeText(mContext,"取消收藏成功",Toast.LENGTH_SHORT).show();
                tv_keep.setText("收藏");
                EventBus.getDefault().post(new FavEvent());
            } else if(integer == FAV_SUCCESS){
                Toast.makeText(mContext,"收藏成功",Toast.LENGTH_SHORT).show();
                tv_keep.setText("取消收藏");
                EventBus.getDefault().post(new FavEvent());
            } else if(integer == FAV_ERROR){
                Toast.makeText(mContext,"收藏失败",Toast.LENGTH_SHORT).show();
            }
            TimelineAdapter.this.notifyDataSetChanged();
        }
    }
}
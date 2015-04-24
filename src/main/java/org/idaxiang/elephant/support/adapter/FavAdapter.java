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

import org.idaxiang.elephant.R;
import org.idaxiang.elephant.bus.TimelineEvent;
import org.idaxiang.elephant.model.FavModel;
import org.idaxiang.elephant.ui.activity.ReadingActivity;
import org.litepal.crud.DataSupport;

import java.util.List;

import de.greenrobot.event.EventBus;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {
    private List<FavModel> mDataset;
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

    public FavAdapter(List<FavModel> myDataset, Context context) {

        mDataset = myDataset;
        mContext = context;
    }

    @Override
    public FavAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_timeline, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ReadingActivity.class).putExtra("id", mDataset.get(vh.getPosition()).getNid()));
            }
        });

        vh.tv_keep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavModel model = mDataset.get(vh.getPosition());
                new FavTask().execute(model);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FavModel favModel = mDataset.get(position);
        holder.tv_title.setText(mDataset.get(position).getTitle());
        holder.tv_summary.setText(mDataset.get(position).getSummary());
        holder.tv_time.setText(mDataset.get(position).getCreated().split("T")[0]);
        if(favModel.isFav()){
            holder.tv_keep.setText("取消收藏");
        }
    }

    public void addAll(List<FavModel> list){
        this.mDataset.addAll(list);
        this.notifyDataSetChanged();
    }

    public void clear(){
        this.mDataset.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static final int FAVED = 0;
    public static final int FAV_SUCCESS = 1;
    public static final int FAV_ERROR = 2;

    private class FavTask extends AsyncTask<Object, Void, Integer> {
        private FavModel msgModel;

        @Override
        protected Integer doInBackground(Object... params) {
            msgModel = (FavModel)params[0];

            List<FavModel> favs = DataSupport.where("nid = ? and fav = ?",msgModel.getNid(), "1").find(FavModel.class);
            if(favs != null && favs.size() > 0){
                FavModel fav = favs.get(0);
                ContentValues values = new ContentValues();
                values.put("fav", false);
                DataSupport.update(FavModel.class, values, fav.getId());
                return FAVED;
            } else {
                return FAV_ERROR;
            }

        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer == FAVED){
                Toast.makeText(mContext,"取消收藏成功",Toast.LENGTH_SHORT).show();
                mDataset.remove(msgModel);
                EventBus.getDefault().post(new TimelineEvent());
            } else if(integer == FAV_ERROR){
                Toast.makeText(mContext,"取消收藏失败",Toast.LENGTH_SHORT).show();
            }
            FavAdapter.this.notifyDataSetChanged();
        }
    }
}
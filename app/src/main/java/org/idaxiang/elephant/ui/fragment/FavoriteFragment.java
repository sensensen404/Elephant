package org.idaxiang.elephant.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.idaxiang.elephant.R;
import org.idaxiang.elephant.bus.FavEvent;
import org.idaxiang.elephant.model.FavModel;
import org.idaxiang.elephant.support.adapter.FavAdapter;
import org.idaxiang.elephant.support.loader.FavLoader;
import org.idaxiang.elephant.utils.AppLogger;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class FavoriteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mList;
    private LinearLayoutManager mManager;
    private FavAdapter mAdapter;
    private List<FavModel> favModelList;

    private FavLoader mLoader;
    private static final int LOADER_ID = 1;

    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        EventBus.getDefault().register(this);
    }

//    @Override
//    public void onResume() {
//        favModelList.clear();
//        new Refresher().execute();
//        super.onResume();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        mList = (RecyclerView)root.findViewById(R.id.listview);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setLayoutManager(mManager);
        DividerItemDecoration divider = new DividerItemDecoration(20);
        mList.addItemDecoration(divider);

        favModelList = new ArrayList<>();
        mAdapter = new FavAdapter(favModelList,getActivity());
        mList.setAdapter(mAdapter);

        mLoader = new FavLoader(getActivity());
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, loaderCallbacks);

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(FavEvent event){
        mLoader.onContentChanged();
    }

    private class Refresher extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            List<FavModel> favModels = DataSupport.where("fav = ?","1").find(FavModel.class);
            if(favModels != null && favModels.size() > 0){
                favModelList.addAll(favModels);
            }
            AppLogger.i("favModels size " + favModels.size());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mAdapter.notifyDataSetChanged();
            super.onPostExecute(result);

        }

    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private int padding;

        public DividerItemDecoration(int padding) {
            this.padding = padding;
        }

        @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            outRect.left += padding;
//            outRect.right += padding;
            outRect.top += padding;
        }
    }

    private LoaderManager.LoaderCallbacks<List<FavModel>> loaderCallbacks = new LoaderManager.LoaderCallbacks<List<FavModel>>() {
        @Override
        public Loader<List<FavModel>> onCreateLoader(int id, Bundle args) {
            return mLoader;
        }

        @Override
        public void onLoadFinished(Loader<List<FavModel>> loader, List<FavModel> data) {
            mAdapter.clear();
            mAdapter.addAll(data);
        }

        @Override
        public void onLoaderReset(Loader<List<FavModel>> loader) {
            mAdapter.clear();
        }
    };

}

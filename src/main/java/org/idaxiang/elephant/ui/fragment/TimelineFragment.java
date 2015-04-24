package org.idaxiang.elephant.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.idaxiang.elephant.R;
import org.idaxiang.elephant.bus.TimelineEvent;
import org.idaxiang.elephant.cache.TimeLineApiCache;
import org.idaxiang.elephant.support.adapter.TimelineAdapter;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimelineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimelineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimelineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    protected RecyclerView mList;
    private LinearLayoutManager mManager;
    private TimelineAdapter mAdapter;
    // Pull To Refresh
    private SwipeRefreshLayout mSwipeRefresh;
    TimeLineApiCache cache;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeTimeLineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimelineFragment newInstance(String param1, String param2) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mList = (RecyclerView)root.findViewById(R.id.recycler_home);

        bindSwipeToRefresh((ViewGroup)root);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setLayoutManager(mManager);
        DividerItemDecoration divider = new DividerItemDecoration(20);
        mList.addItemDecoration(divider);


        cache = new TimeLineApiCache();
        cache.loadFromCache();
        mAdapter = new TimelineAdapter(cache.MsgListModel,getActivity());
        mList.setAdapter(mAdapter);


        if(cache.MsgListModel.size() == 0){
            new Refresher().execute(true);
        }

        EventBus.getDefault().register(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(TimelineEvent event){
        mAdapter.notifyDataSetChanged();
    }

    protected void bindSwipeToRefresh(ViewGroup v) {
        mSwipeRefresh = new SwipeRefreshLayout(getActivity());

        // Move child to SwipeRefreshLayout, and add SwipeRefreshLayout to root
        // view
        v.removeViewInLayout(mList);
        v.addView(mSwipeRefresh, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeRefresh.addView(mList, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mSwipeRefresh.setOnRefreshListener(this);
//        mSwipeRefresh.setColorScheme(R.color.ptr_green, R.color.ptr_orange,
//                R.color.ptr_red, R.color.ptr_blue);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        new Refresher().execute(true);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class Refresher extends AsyncTask<Boolean, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            cache.loadFromWeb();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            mSwipeRefresh.setRefreshing(false);
            if(cache.isGetNewData()){
                Toast.makeText(getActivity(),"刷新成功",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(),"刷新失败",Toast.LENGTH_SHORT).show();
            }
            cache.setGetNewData(false);
            mAdapter.notifyDataSetChanged();
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

}

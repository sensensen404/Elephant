package org.idaxiang.elephant;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.idaxiang.elephant.support.Settings;
import org.idaxiang.elephant.support.Utility;
import org.idaxiang.elephant.ui.activity.SettingActivity;
import org.idaxiang.elephant.ui.activity.ToolbarActivity;
import org.idaxiang.elephant.ui.fragment.FavoriteFragment;
import org.idaxiang.elephant.ui.fragment.TimelineFragment;
import org.idaxiang.elephant.ui.login.OAuthActivity;
import org.idaxiang.elephant.utils.AppLogger;

import java.lang.ref.WeakReference;

public class MainActivity extends ToolbarActivity implements TimelineFragment.OnFragmentInteractionListener {

    //声明相关变量
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    public static final int HOME = 0;
    public static final int FAV = 1;

    // Fragments
    private Fragment[] mFragments = new Fragment[2];
    private FragmentManager mManager;
    private LinearLayout ll_mark;
    private LinearLayout ll_fav;
    private LinearLayout ll_set;
    private LinearLayout[] buttons;

    private ImageView iv_avatar;
    private int mDrawerGravity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        setContentView(R.layout.activity_main);
        mLayout = R.layout.activity_main;
        super.onCreate(savedInstanceState);
        findViews(); //获取控件

//        toolbar.setTitle("Elephant");//设置Toolbar标题
//        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        boolean rightHanded = Settings.getInstance(this).getBoolean(Settings.RIGHT_HANDED, false);

        mDrawerGravity = rightHanded ? Gravity.RIGHT : Gravity.LEFT;

        // Set gravity
        View nav = findViewById(R.id.nav);
        DrawerLayout.LayoutParams p = (DrawerLayout.LayoutParams) nav.getLayoutParams();
        p.gravity = mDrawerGravity;
        nav.setLayoutParams(p);

        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, getToolbar(), R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mFragments[HOME] = TimelineFragment.newInstance("", "");
        mFragments[FAV] = FavoriteFragment.newInstance("","");

        mManager = getFragmentManager();

        FragmentTransaction ft = mManager.beginTransaction();
        for (Fragment f : mFragments) {
            ft.add(R.id.container, f);
            ft.hide(f);
        }
        ft.commit();

        switchTo(HOME);
        mark();

    }

    private void findViews() {
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_left);

        iv_avatar = Utility.findViewById(this,R.id.iv_avatar);
        ll_mark = (LinearLayout)findViewById(R.id.ll_bookmark);
        ll_fav= Utility.findViewById(this,R.id.ll_favarite);
        ll_set = Utility.findViewById(this,R.id.ll_setting);
        buttons = new LinearLayout[]{ll_mark,ll_fav,ll_set};

        Utility.bindOnClick(this, ll_mark, "mark");
        Utility.bindOnClick(this, ll_fav, "fav");
        Utility.bindOnClick(this, ll_set, "set");
        Utility.bindOnClick(this, iv_avatar, "login");

    }

    private void login(){
        AppLogger.i("login onclick");
        Intent intent = new Intent(MainActivity.this,
                OAuthActivity.class);
        startActivity(intent);
    }

    private void mark(){
        for(LinearLayout ll : buttons){
            ll.setSelected(false);
        }
        switchTo(HOME);
        ll_mark.setSelected(true);
    }

    private void fav(){
        for(LinearLayout ll : buttons){
            ll.setSelected(false);
        }
        switchTo(FAV);
        ll_fav.setSelected(true);
    }

    private void set(){
        for(LinearLayout ll : buttons){
            ll.setSelected(false);
        }
        ll_set.setSelected(true);
        switchToSetting();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.closeDrawer(mDrawerGravity);
            }
        },1000);
    }

    private void switchToSetting(){
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


    private void switchTo(int id) {
        FragmentTransaction ft = mManager.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);

        for (int i = 0; i < mFragments.length; i++) {
            Fragment f = mFragments[i];

            if (f != null) {
                if (i != id) {
                    ft.hide(f);
                } else {
                    ft.show(f);
                }
            }
        }

        ft.commit();

//        mCurrent = id;
//        mNext = id;

        mDrawerLayout.closeDrawer(mDrawerGravity);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private Handler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler{
        private final WeakReference<Activity> mActivity;
        public MyHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if(mActivity.get() == null) {
                return;
            }
        }
    }
}

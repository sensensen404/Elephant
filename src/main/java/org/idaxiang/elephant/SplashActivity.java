package org.idaxiang.elephant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import java.lang.ref.WeakReference;


public class SplashActivity extends Activity {

    View root_view;

    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public MyHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (mActivity.get() == null) {
                return;
            }
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent(mActivity.get(), MainActivity.class);
                    mActivity.get().startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        root_view = findViewById(R.id.action_bar_root);
        AlphaAnimation aa = new AlphaAnimation(0.7f, 1.0f);
        aa.setDuration(2000);
        root_view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mHandler.sendEmptyMessageDelayed(0, 3000);
    }

}

package org.idaxiang.elephant.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.idaxiang.elephant.R;
import org.idaxiang.elephant.support.lib.swipebacklayout.SwipeBackLayout;
import org.idaxiang.elephant.support.lib.swipebacklayout.app.SwipeBackActivity;
import org.idaxiang.elephant.ui.fragment.SettingsFragment;

public class SettingActivity extends SwipeBackActivity {

    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        mLayout = R.layout.activity_setting;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        getFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
    }

}

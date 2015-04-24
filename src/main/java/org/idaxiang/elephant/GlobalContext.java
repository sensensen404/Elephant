package org.idaxiang.elephant;

import com.avos.avoscloud.AVOSCloud;

import org.idaxiang.elephant.support.Constants;
import org.litepal.LitePalApplication;

/**
 * Created by Azzssss on 15-1-2.
 */
public class GlobalContext extends LitePalApplication {

    public static GlobalContext mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AVOSCloud.initialize(this, Constants.LEANCLOUD_APP_ID, Constants.LEANCLOUD_APP_KEY);
    }

    public static GlobalContext getInstance() {
        return mInstance;
    }
}

package com.xgw.mycustomexoplayer;

import android.app.Application;

import com.xgw.mybaselib.utils.Utils;
import com.xgw.myexovideoview.utils.MyExoVideoManager;

/**
 * Created by XieGuangwei on 2018/4/10.
 */

public class AppConfig extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        MyExoVideoManager
                .getInstance()
                .initApp(this)
                .initLog(BuildConfig.DEBUG);
    }
}

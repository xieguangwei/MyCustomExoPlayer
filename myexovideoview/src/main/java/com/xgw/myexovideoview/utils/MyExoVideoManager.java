package com.xgw.myexovideoview.utils;

import android.app.Application;
import com.socks.library.KLog;

/**
 * Created by XieGuangwei on 2018/4/10.
 * ExoPlayer管理类
 */

public class MyExoVideoManager {
    private static MyExoVideoManager instance;
    private Application app;
    public static MyExoVideoManager getInstance(){
        if (instance == null) {
            synchronized (MyExoVideoManager.class) {
                instance = new MyExoVideoManager();
            }
        }
        return instance;
    }
    public MyExoVideoManager initApp(Application app) {
        this.app = app;
        return this;
    }

    public MyExoVideoManager initLog(boolean showLog) {
        KLog.init(showLog);
        return this;
    }

    public Application getApp() {
        return app;
    }
}

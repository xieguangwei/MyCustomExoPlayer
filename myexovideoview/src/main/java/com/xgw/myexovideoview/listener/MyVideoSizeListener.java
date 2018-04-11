package com.xgw.myexovideoview.listener;

/**
 * Created by XieGuangwei on 2018/4/11.
 */

public interface MyVideoSizeListener {
    /**
     * 当播放器尺寸发生变化是调用
     * @param with 播放器宽
     * @param height 播放器高
     */
    void onVideoSizeChanged(int with,int height);
}

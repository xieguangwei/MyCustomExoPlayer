package com.xgw.myexovideoview.listener;

import com.google.android.exoplayer2.ExoPlaybackException;

/**
 * Created by XieGuangwei on 2018/4/10.
 */

public interface MyVideoEndListener {
    /**
     * 当播放器尺寸发生变化是调用
     * @param with 播放器宽
     * @param height 播放器高
     */
    void onVideoSizeChanged(int with,int height);

    /**
     * 当播放器开始或者停止加载视频资源时
     * @param isLoading 是否在加载
     */
    void onLoadingChanged(boolean isLoading);

    /**
     * 当还没有加载播放器时调用
     */
    void onIdle();

    /**
     * 正在缓冲回调
     */
    void onBuffering();

    /**
     * 播放器已准备好播放时调用
     */
    void onReady();

    /**
     * 播放一个视频完成时调用
     */
    void onEnded();

    /**
     * 播放视频出错时调用
     * @param error
     */
    void onError(ExoPlaybackException error);
}

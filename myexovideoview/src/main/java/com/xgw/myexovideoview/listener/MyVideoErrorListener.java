package com.xgw.myexovideoview.listener;

import com.google.android.exoplayer2.ExoPlaybackException;

/**
 * Created by XieGuangwei on 2018/4/10.
 */

public interface MyVideoErrorListener {
    void onError(ExoPlaybackException error);
}

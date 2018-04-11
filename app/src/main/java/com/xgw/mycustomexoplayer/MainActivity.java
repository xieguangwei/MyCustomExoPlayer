package com.xgw.mycustomexoplayer;

import android.widget.LinearLayout;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.socks.library.KLog;
import com.xgw.mybaselib.base.BaseActivity;
import com.xgw.mybaselib.utils.SizeUtils;
import com.xgw.myexovideoview.constant.ScreenStatus;
import com.xgw.myexovideoview.listener.MyVideoEndListener;
import com.xgw.myexovideoview.listener.MyVideoSizeListener;
import com.xgw.myexovideoview.listener.MyVideoStateListener;
import com.xgw.myexovideoview.listener.MyVideoErrorListener;
import com.xgw.myexovideoview.listener.MyVideoScreenListener;
import com.xgw.myexovideoview.utils.MyVideoControlManager;
import com.xgw.myexovideoview.view.MyExoVideoView;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements MyVideoStateListener, MyVideoErrorListener, MyVideoScreenListener, MyVideoEndListener, MyVideoSizeListener {
    @BindView(R.id.video_view)
    MyExoVideoView videoView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        videoView.initPlayer("http://play.g3proxy.lecloud.com/vod/v2/MjQ5LzM3LzIwL2xldHYtdXRzLzE0L3Zlcl8wMF8yMi0xMTA3NjQxMzkwLWF2Yy00MTk4MTAtYWFjLTQ4MDAwLTUyNjExMC0zMTU1NTY1Mi00ZmJjYzFkNzA1NWMyNDc4MDc5OTYxODg1N2RjNzEwMi0xNDk4NTU3OTYxNzQ4Lm1wNA==?b=479&mmsid=65565355&tm=1499247143&key=98c7e781f1145aba07cb0d6ec06f6c12&platid=3&splatid=345&playid=0&tss=no&vtype=13&cvid=2026135183914&payff=0&pip=08cc52f8b09acd3eff8bf31688ddeced&format=0&sign=mb&dname=mobile&expect=1&tag=mobile&xformat=super");
        videoView.setMyVideoStateListener(this);
        videoView.setController(this, new MyVideoControlManager());
        videoView.setMyVideoErrorListener(this);
        videoView.setMyVideoScreenListener(this);
        videoView.setMyVideoEndListener(this);
        videoView.setMyVideoSizeListener(this);
        videoView.shouldShowPlayModeIv(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.onPause();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onVideoSizeChanged(int with, int height) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        KLog.e("onLoadingChanged---isLoading--" + isLoading);
    }

    @Override
    public void onIdle() {
        KLog.e("onIdle");
    }

    @Override
    public void onBuffering() {
        KLog.e("onBuffering");
    }

    @Override
    public void onReady() {
        KLog.e("onReady");
    }

    @Override
    public void onEnded() {

        KLog.e("onEnded");
    }

    @Override
    public void onError(ExoPlaybackException error) {
        KLog.e("onError---" + error.getMessage());
    }

    @Override
    public void startFullScreen() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) videoView.getLayoutParams();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        videoView.setLayoutParams(params);
        videoView.setScreenParams(ScreenStatus.SCREEN_STATUS_FULL);
    }

    @Override
    public void exitFullScreen() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) videoView.getLayoutParams();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = SizeUtils.dp2px(300);
        videoView.setLayoutParams(params);
        videoView.setScreenParams(ScreenStatus.SCREEN_STATUS_NORMAL);
    }
}

package com.xgw.myexovideoview.view;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.xgw.myexovideoview.R;
import com.xgw.myexovideoview.constant.PlayMode;
import com.xgw.myexovideoview.constant.ScreenStatus;
import com.xgw.myexovideoview.listener.MyVideoEndListener;
import com.xgw.myexovideoview.listener.MyVideoSizeListener;
import com.xgw.myexovideoview.listener.MyVideoStateListener;
import com.xgw.myexovideoview.listener.MyVideoErrorListener;
import com.xgw.myexovideoview.listener.MyVideoScreenListener;
import com.xgw.myexovideoview.listener.SimpleEventListener;
import com.xgw.myexovideoview.utils.MyVideoControlManager;
import com.xgw.myexovideoview.utils.PlayModeUtils;
import com.xgw.myexovideoview.videogesture.ShowControlLayout;
import com.xgw.myexovideoview.videogesture.VideoGestureLayout;

/**
 * Created by XieGuangwei on 2018/3/30.
 * ExoVideoPlayer（google官方，android4.x）
 */

public class MyExoVideoView extends RelativeLayout implements View.OnClickListener {
    private MyTextureView textureView;//此处用textureview自定义界面，不使用simpleexoplayerview
    private MyCoverImageView coverIv;//封面
    private RelativeLayout controlRl;//控制布局
    private RelativeLayout reloadRl;//重新加载

    private RelativeLayout loadingRl;//加载框

    private VideoGestureLayout vgl;//手势控制布局（控制音量、亮度、进度）
    private ShowControlLayout scl;//显示手势控制的信息布局

    private TextView durationTv;//显示总时长
    private TextView playStartTv;//显示当前进度市场
    private SeekBar mSeekBar;//进度条

    private Button btnPlay;//播放按钮

    private ImageView fullScreenIv;//全屏按钮
    private ImageView playModeIv;//播放模式按钮


    private LinearLayout playProgressLl;//底部进度布局


    private SimpleExoPlayer player;//在textureview上运行
    private DataSource.Factory mediaDataSourceFactory;
    private TrackSelector trackSelector;
    private DefaultBandwidthMeter bandwidthMeter;

    private String playUrl;//播放url
    private MyVideoStateListener mStateListener;//播放状态监听
    private MyVideoErrorListener mErrorListener;//播放出错监听
    private MyVideoEndListener mEndListener;//播放完成监听
    private MyVideoSizeListener mSizeListener;//播放器大小变化监听

    private boolean isFinishPlay;//是否完成播放
    private boolean isPlayError;//是否播放出错

    private static final int UPDATE_TIME_AND_PROGRESS = 1;//更新时间和进度的消息

    private long currentPosition;//当前播放进度


    //当前屏幕状态，默认为正常
    private ScreenStatus screenStatus = ScreenStatus.SCREEN_STATUS_NORMAL;

    private MyVideoScreenListener mScreenListener;//全屏、退出全屏监听接口


    /**
     * handler每隔500ms刷新一次当前播放进度
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case UPDATE_TIME_AND_PROGRESS://更新进度和时间
                    //获取当前时间
                    long currentTime = player.getCurrentPosition();
                    currentPosition = player.getCurrentPosition();
                    //获取总时间
                    long totalTime = player.getDuration();

                    //设置总时间、当前时间显示，并格式化
                    updateTimeFormat(durationTv, (int) totalTime);
                    updateTimeFormat(playStartTv, (int) currentTime);

                    //设置进度条
                    mSeekBar.setMax((int) totalTime);
                    mSeekBar.setProgress((int) currentTime);

                    //每隔500ms通知自己刷新一次进度
                    mHandler.sendEmptyMessageDelayed(UPDATE_TIME_AND_PROGRESS, 500);//500MS通知自己刷新一次
                    break;
            }
        }
    };


    public MyExoVideoView(Context context) {
        super(context);
        init(context);
    }

    public MyExoVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyExoVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 时间格式化
     *
     * @param textView    时间控件
     * @param millisecond 总时间 毫秒
     */
    private void updateTimeFormat(TextView textView, int millisecond) {
        //将毫秒转换为秒
        int second = millisecond / 1000;
        //计算小时
        int hh = second / 3600;
        //计算分钟
        int mm = second % 3600 / 60;
        //计算秒
        int ss = second % 60;
        //判断时间单位的位数
        String str = null;
        if (hh != 0) {//表示时间单位为三位
            str = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            str = String.format("%02d:%02d", mm, ss);
        }
        //将时间赋值给控件
        textView.setText(str);
    }

    public void setController(Activity activity, MyVideoControlManager myVideoControlManager) {
        myVideoControlManager.bindView(activity, this, vgl, scl);
    }

    private void init(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.my_exo_video_layout, this, true);
        textureView = (MyTextureView) contentView.findViewById(R.id.texture_view);
        coverIv = (MyCoverImageView) contentView.findViewById(R.id.cover_iv);
        controlRl = (RelativeLayout) contentView.findViewById(R.id.control_rl);
        reloadRl = (RelativeLayout) contentView.findViewById(R.id.reload_rl);
        reloadRl.setOnClickListener(this);
        loadingRl = (RelativeLayout) contentView.findViewById(R.id.loading_rl);
        vgl = (VideoGestureLayout) contentView.findViewById(R.id.vgl);
        scl = (ShowControlLayout) contentView.findViewById(R.id.scl);
        durationTv = (TextView) contentView.findViewById(R.id.duration_tv);
        playStartTv = (TextView) contentView.findViewById(R.id.play_start_tv);
        mSeekBar = (SeekBar) contentView.findViewById(R.id.seekbar);

        btnPlay = (Button) contentView.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);

        fullScreenIv = (ImageView) contentView.findViewById(R.id.full_screen_iv);
        fullScreenIv.setOnClickListener(this);

        playModeIv = (ImageView) contentView.findViewById(R.id.play_model_iv);
        playModeIv.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //拖动视频是禁止刷新
                mHandler.removeMessages(UPDATE_TIME_AND_PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止拖动，获取总进度
                int totalTime = mSeekBar.getProgress();
                //跳转到当前位置
                seek(totalTime);
                //重新刷新
                mHandler.sendEmptyMessage(UPDATE_TIME_AND_PROGRESS);
            }
        });

        playProgressLl = (LinearLayout) contentView.findViewById(R.id.play_progress);

        //初始化EXOplayer相关
        //create a TrackSelector 选择MediaSource
        bandwidthMeter = new DefaultBandwidthMeter();
        //produces DataSource instances through which media data is loaded
        mediaDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, null), bandwidthMeter);

        setPlayModelImage();
    }

    /**
     * 设置播放列表
     *
     * @param url
     */
    public void initPlayer(String url) {
        this.playUrl = url;
        Glide.with(getContext())
                .load(url)
                .into(coverIv);
    }

    /**
     * 播放指定位置视频
     *
     * @param playUrl
     */
    public void playSpecifiedVideo(String playUrl, long currentPosition) {
        if (TextUtils.isEmpty(playUrl)) {
            onErrorHandle();
            return;
        }
        this.playUrl = playUrl;
        Glide.with(getContext())
                .load(playUrl)
                .into(coverIv);

        if (mediaDataSourceFactory == null || player == null) {
            releasePlayer();
            //从MediaSource中选出media提供给可用的Render S来渲染,在创建播放器时被注入
            TrackSelection.Factory videoTrackSelectionFactor = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactor);
            //Create the player
            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

            player.addListener(eventListener);
            player.addVideoListener(videoListener);
            player.setVideoTextureView(textureView);
        }

        //Produces Extractor instances for parsing the media data 萃取剂
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(playUrl),
                mediaDataSourceFactory, extractorsFactory, null, null);
        //设置资源
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.seekTo(currentPosition);

        isFinishPlay = false;
        isPlayError = false;
    }

    /**
     * 初始化播放器
     */
    private void initializePlayer() {
        requestFocus();
        if (TextUtils.isEmpty(playUrl)) {
            onErrorHandle();
            return;
        }
        releasePlayer();
        //从MediaSource中选出media提供给可用的Render S来渲染,在创建播放器时被注入
        TrackSelection.Factory videoTrackSelectionFactor = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactor);
        //Create the player
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);


        playSpecifiedVideo(playUrl, currentPosition);

        player.addListener(eventListener);
        player.addVideoListener(videoListener);
        player.setVideoTextureView(textureView);
    }

    /**
     * 释放播放器
     */
    private void releasePlayer() {
        if (mHandler != null) {
            mHandler.removeMessages(UPDATE_TIME_AND_PROGRESS);
        }
        if (player != null) {
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    /**
     * fragment/activity onResume调用
     */
    public void onResume() {
        initializePlayer();
        setPlayModelImage();
    }

    /**
     * fragment/activity onPause调用
     */
    public void onPause() {
        releasePlayer();
    }

    /**
     * 播放器大小监听
     */
    private SimpleExoPlayer.VideoListener videoListener = new SimpleExoPlayer.VideoListener() {
        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            textureView.adaptVideoSize(width, height);
            coverIv.adaptVideoSize(width, height);
            if (mSizeListener != null) {
                mSizeListener.onVideoSizeChanged(width, height);
            }
        }

        @Override
        public void onRenderedFirstFrame() {

        }
    };

    /**
     * 播放时间监听
     */
    private Player.EventListener eventListener = new SimpleEventListener() {
        @Override
        public void onLoadingChanged(boolean isLoading) {
            //此处设置第二条进度条
            mSeekBar.setSecondaryProgress((int) (player.getBufferedPosition()));
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_IDLE:
                    //播放器还没准备好
                    if (mStateListener != null) {
                        mStateListener.onIdle();
                    }
                    break;
                case Player.STATE_BUFFERING:
                    //缓冲，显示菊花
                    if (mStateListener != null) {
                        mStateListener.onBuffering();
                    }
                    showLoadingRl();
                    dismissControlRl();
                    break;
                case Player.STATE_READY:
                    //准备好播放，隐藏菊花
                    if (mStateListener != null) {
                        mStateListener.onReady();
                    }
                    onPlayingHandle();
                    break;
                case Player.STATE_ENDED:
                    //当前视频播放完成
                    showControlRl();
                    if (mEndListener != null) {
                        mEndListener.onEnded();
                    }
                    onCompleteHandle();
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            if (mErrorListener != null) {
                mErrorListener.onError(error);
            }
            onErrorHandle();
        }
    };

    /**
     * 显示重新加载布局
     */
    private void showReloadRl() {
        reloadRl.setVisibility(VISIBLE);
    }

    /**
     * 隐藏重新加载布局
     */
    private void dismissReloadRl() {
        reloadRl.setVisibility(GONE);
    }

    /**
     * 显示控制
     */
    private void showControlRl() {
        controlRl.setVisibility(VISIBLE);
        playProgressLl.setVisibility(VISIBLE);
    }

    /**
     * 隐藏控制
     */
    private void dismissControlRl() {
        controlRl.setVisibility(GONE);
        playProgressLl.setVisibility(GONE);
    }

    /**
     * 显示正在加载
     */
    private void showLoadingRl() {
        loadingRl.setVisibility(VISIBLE);
    }

    /**
     * 隐藏正在加载
     */
    private void dismissLoadingRl() {
        loadingRl.setVisibility(GONE);
    }

    /**
     * 设置播放完成监听
     *
     * @param listener
     */
    public void setMyVideoStateListener(MyVideoStateListener listener) {
        this.mStateListener = listener;
    }

    /**
     * 设置播放完成监听
     *
     * @param listener
     */
    public void setMyVideoEndListener(MyVideoEndListener listener) {
        this.mEndListener = listener;
    }

    /**
     * 设置播放器大小变化监听
     *
     * @param listener
     */
    public void setMyVideoSizeListener(MyVideoSizeListener listener) {
        this.mSizeListener = listener;
    }

    /**
     * 设置播放出错监听
     *
     * @param listener
     */
    public void setMyVideoErrorListener(MyVideoErrorListener listener) {
        this.mErrorListener = listener;
    }

    /**
     * 正在播放的处理
     */
    private void onPlayingHandle() {
        if (player != null) {
            if (player.getPlayWhenReady()) {
                dismissControlRl();
            } else {
                showControlRl();
            }
        }
        dismissLoadingRl();
        dismissReloadRl();
        dismissCoverIv();
        mHandler.sendEmptyMessage(UPDATE_TIME_AND_PROGRESS);
    }

    /**
     * 播放暂停的处理
     */
    private void onPauseHandle() {
        showControlRl();
    }

    /**
     * 播放出错的处理
     */
    private void onErrorHandle() {
        dismissLoadingRl();
        showReloadRl();
        reloadRl.setVisibility(VISIBLE);
        isPlayError = true;
    }

    /**
     * 播放完成的处理
     */
    private void onCompleteHandle() {
        showCoverIv();
        dismissLoadingRl();
        dismissReloadRl();
        showControlRl();
        isFinishPlay = true;
        currentPosition = 0;
    }

    /**
     * 显示封面
     */
    private void showCoverIv() {
        coverIv.setVisibility(VISIBLE);
    }

    /**
     * 隐藏封面
     */
    private void dismissCoverIv() {
        coverIv.setVisibility(GONE);
    }

    /**
     * 播放还是进入列表
     *
     * @return
     */
    public boolean playOrEnterList() {
        if (player != null) {
            if (player.getPlayWhenReady()) {
                return true;
            } else {
                if (isPlayError || isFinishPlay) {
                    playSpecifiedVideo(playUrl, 0);
                } else {
                    player.setPlayWhenReady(true);
                }
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 播放或暂停
     */
    public void playOrPause() {
        if (player != null) {
            if (isFinishPlay || isPlayError) {
                playSpecifiedVideo(playUrl, 0);
            } else {
                if (player.getPlayWhenReady()) {
                    onPauseHandle();
                    player.setPlayWhenReady(false);
                } else {
                    onPlayingHandle();
                    player.setPlayWhenReady(true);
                }
            }
        }
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return player != null && player.getPlayWhenReady();
    }

    /**
     * 获取当前播放器
     *
     * @return
     */
    public SimpleExoPlayer getPlayer() {
        return player;
    }

    /**
     * 跳转到指定位置播放
     *
     * @param progress
     */
    public void seek(long progress) {
        if (player != null) {
            if (progress <= player.getDuration()) {
                player.seekTo(progress);
            } else {
                player.seekTo(player.getDuration());
            }
        }
    }

    /**
     * 获取动前进度
     * @return
     */
    public long getCurrentProgress() {
        return player == null ? 0 : player.getCurrentPosition();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == btnPlay.getId()) {
            playOrPause();
        } else if (id == reloadRl.getId()) {
            playSpecifiedVideo(playUrl, currentPosition);
        } else if (id == fullScreenIv.getId()) {
            if (mScreenListener != null) {
                if (screenStatus == ScreenStatus.SCREEN_STATUS_FULL) {
                    mScreenListener.exitFullScreen();
                } else {
                    mScreenListener.startFullScreen();
                }
            }
        } else if (id == playModeIv.getId()) {
            setPlayMode();
        }
    }

    private void setPlayMode() {
        String toast = null;
        //顺序播放、列表循环、单曲循环
        if (PlayModeUtils.getPlayMode() == PlayMode.PLAY_MODE_LIST_ORDER) {
            PlayModeUtils.setPlayMode(PlayMode.PLAY_MODE_LIST_CYCLE);
            toast = "列表循环";
        } else if (PlayModeUtils.getPlayMode() == PlayMode.PLAY_MODE_LIST_CYCLE) {
            PlayModeUtils.setPlayMode(PlayMode.PLAY_MODE_SINGLE_CYCLE);
            toast = "单曲循环";
        } else if (PlayModeUtils.getPlayMode() == PlayMode.PLAY_MODE_SINGLE_CYCLE) {
            PlayModeUtils.setPlayMode(PlayMode.PLAY_MODE_LIST_ORDER);
            toast = "顺序播放";
        }
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
        setPlayModelImage();
    }

    /**
     * 设置playmodel对应的按钮
     */
    private void setPlayModelImage() {
        if (playModeIv == null) {
            return;
        }
        int resourceId;
        switch (PlayModeUtils.getPlayMode()) {
            case PlayMode.PLAY_MODE_LIST_ORDER:
                resourceId = R.drawable.ic_play_model_order;
                break;
            case PlayMode.PLAY_MODE_LIST_CYCLE:
                resourceId = R.drawable.ic_play_model_list_recycle;
                break;
            case PlayMode.PLAY_MODE_SINGLE_CYCLE:
                resourceId = R.drawable.ic_play_model_single_recycle;
                break;
            default:
                resourceId = R.drawable.ic_play_model_order;
                break;
        }
        playModeIv.setImageResource(resourceId);
    }

    /**
     * 屏幕状态监听
     *
     * @param screenListener
     */
    public void setMyVideoScreenListener(MyVideoScreenListener screenListener) {
        this.mScreenListener = screenListener;
    }


    /**
     * 设置全屏、退出全屏后的参数、状态
     *
     * @param screenStatus
     */
    public void setScreenParams(ScreenStatus screenStatus) {
        if (fullScreenIv != null) {
            this.screenStatus = screenStatus;
            if (screenStatus == ScreenStatus.SCREEN_STATUS_FULL) {
                fullScreenIv.setImageResource(R.drawable.video_exit);
            } else if (screenStatus == ScreenStatus.SCREEN_STATUS_NORMAL) {
                fullScreenIv.setImageResource(R.drawable.video_full_screen);
            }
        }
        if (player != null) {
            if (isFinishPlay || isPlayError) {
                playSpecifiedVideo(playUrl, 0);
            } else {
                if (!player.getPlayWhenReady()) {
                    onPlayingHandle();
                    player.setPlayWhenReady(true);
                }
            }
        }
    }

    public void shouldShowFullScreenIv(boolean shouldShowFullScreen) {
        if (fullScreenIv == null) {
            return;
        }
        fullScreenIv.setVisibility(shouldShowFullScreen ? VISIBLE : GONE);
    }

    public void shouldShowPlayModeIv(boolean shouldShowPlayMode) {
        if (playModeIv == null) {
            return;
        }
        playModeIv.setVisibility(shouldShowPlayMode ? VISIBLE : GONE);
    }
}

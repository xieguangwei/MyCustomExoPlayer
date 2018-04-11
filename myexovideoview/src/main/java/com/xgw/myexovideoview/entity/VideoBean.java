package com.xgw.myexovideoview.entity;

import java.io.Serializable;

/**
 * Created by XieGuangwei on 2018/3/16.
 */

public class VideoBean implements Serializable{
    private static final long serialVersionUID = 4265481348890105457L;
    private String url;
    private String name;
    private boolean isPlaying;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.example.sayyad.exotest.VideoListActivity.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by SAYYAD on 05-Apr-18.
 */

public class VideoModel implements Serializable{
    private String title, url;
    private Integer duration;
    private Bitmap thumbnail;

    public VideoModel(String title, Integer duration, String url,Bitmap thumbnail) {
        this.title = title;
        this.url = url;
        this.duration = duration;
        this.thumbnail = thumbnail;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}

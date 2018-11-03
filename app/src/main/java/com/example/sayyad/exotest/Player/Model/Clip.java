package com.example.sayyad.exotest.Player.Model;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import java.io.Serializable;

/**
 * Created by SAYYAD on 14-Feb-18.
 */

public class Clip implements Serializable,Comparable<Clip>{
    private String lable;
    private long startTime;
    private long stopTime;

    public Clip() {
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public Clip(String lable, long startTime, long stopTime) {
        this.lable = lable;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public Clip(long startTime, long stopTime) {
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int compareTo(@NonNull Clip clip) {
        int compreValue = Long.compare(this.startTime,clip.startTime);
        if (compreValue==0){
            compreValue = Long.compare(this.stopTime,clip.getStopTime());
        }
        return compreValue;
    }
}
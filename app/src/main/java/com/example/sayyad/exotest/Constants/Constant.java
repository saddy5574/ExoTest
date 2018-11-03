package com.example.sayyad.exotest.Constants;


import android.Manifest;

public interface Constant {
    String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    String WRITE_SETTINGS = Manifest.permission.WRITE_SETTINGS;
    String ENABLE_SKIPPING = "enable_skipping";
    String SKIP_CATEGORIES = "skip_categories";
    String PLAY_BACK_POSITION = "playBackPosition";
    String CURRENT_WINDOW = "currentWindow";
    String PLAY_WHEN_READY = "playWhenReady";
    String ENABLE_JUST_PLAY = "enable_justPlay";
    String JUST_PLAY_CATEGORIES = "justPlay_categories";
    String ENABLE_VOLUME = "enable_volume";
    String VOLUME_PERCENTAGE = "volume_percentages";
    String VOLUME_CATEGORIES= "volume_categories";
    String ENABLE_MUTE = "enable_mute";



    int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    int READ_EXTERNAL_STORAGE_REQUEST_CODE = 2;
    int WRITE_SETTING_REQUEST_CODE = 5;
    int LOAD_VIDEOS_FROM_EXTERNAL_STORAGE = 3;
    int LOAD_VIDEOS_FROM_INTERNAL_STORAGE = 4;




}

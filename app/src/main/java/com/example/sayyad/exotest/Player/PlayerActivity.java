package com.example.sayyad.exotest.Player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sayyad.exotest.Constants.Constant;
import com.example.sayyad.exotest.LoadVideos.LoadVideos;
import com.example.sayyad.exotest.Player.Model.Categories;
import com.example.sayyad.exotest.Player.Model.Clip;
import com.example.sayyad.exotest.Player.Model.ClipList;
import com.example.sayyad.exotest.R;
import com.example.sayyad.exotest.Setting.Setting;
import com.example.sayyad.exotest.Util.FileUtil;
import com.example.sayyad.exotest.Util.PermissionUtil;
import com.example.sayyad.exotest.VideoListActivity.Model.VideoModel;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayerActivity extends Activity implements Constant {

    //region Used Variable

    public static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = "PlayerActivity";
    @BindView(R.id.exo_video_view)
    PlayerView playerView;
    @BindView(R.id.button_back)
    ImageButton buttonBack;
    @BindView(R.id.textView_title)
    TextView textViewTitle;
    @BindView(R.id.button_add_label)
    ImageButton buttonAddLabel;
    @BindView(R.id.button_add_mute)
    ImageButton buttonAddMute;
    @BindView(R.id.button_setting)
    ImageButton buttonSetting;
    @BindView(R.id.button_hide_label_bar)
    ImageButton buttonHideLabelBar;
    @BindView(R.id.textView_start_time)
    TextView textViewStartTime;
    @BindView(R.id.textView_stop_time)
    TextView textViewStopTime;
    @BindView(R.id.button_save)
    ImageButton buttonSave;
    @BindView(R.id.skip_controls_container)
    LinearLayout skipControlsContainer;
    @BindView(R.id.exo_prev)
    ImageButton exoPrev;
    @BindView(R.id.next)
    ImageButton exoNext;
    @BindView(R.id.spinner_label)
    Spinner spinnerLabel;
    @BindView(R.id.title_container)
    LinearLayout titleContainer;
    @BindView(R.id.button_hide_mute_bar)
    ImageButton buttonHideMuteBar;
    @BindView(R.id.textView_start_time_for_mute)
    TextView textViewStartTimeForMute;
    @BindView(R.id.textView_stop_time_for_mute)
    TextView textViewStopTimeForMute;
    @BindView(R.id.button_save_mute)
    ImageButton buttonSaveMute;
    @BindView(R.id.mute_controls_container)
    LinearLayout muteControlsContainer;


    private SimpleExoPlayer player;
    private ComponentListener componentListener;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;

    private List<VideoModel> videosList;
    private int videoNo;
    private String label;
    private long mStartTime = -1;
    private long mStopTime = -1;


    //For Add Labels
    private ArrayList<String> labels;
    ArrayAdapter<String> spinnerAdapter;

    SkipAsyncTask skipAsyncTask;
    JustPlayAsyncTask justPlayAsyncTask;
    private VolumeAsyncTask volumeAsyncTask;
    private MuteAsyncTask muteAsyncTask;


    private boolean isSkipRunning = false;
    private boolean isJustPlayRunning = false;
    private boolean isVoumeRunning = false;
    private boolean isMuteRunning = false;


    long muteStartTime = -1;
    long muteStopTime = -1;

    //endregion-
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras().getBundle("BUNDLE");
        videoNo = bundle.getInt("POSITION");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        videosList = LoadVideos.getVideosList();

        ButterKnife.bind(this);

        PermissionUtil.checkPermission(this, WRITE_EXTERNAL_STORAGE);

        componentListener = new ComponentListener();

        settingSpinner();

    }

    class SkipAsyncTask extends AsyncTask<ClipList, Long, Long> {

        @Override
        protected Long doInBackground(ClipList... clips) {
            ClipList clipList = clips[0];
            List<Clip> list = clipList.getClips();
            Collections.sort(list);

            while (isSkipRunning) {
                for (Clip clip : list) {
                    if (player != null) {
                        if (player.getCurrentPosition() >= clip.getStartTime() && player.getCurrentPosition() <= clip.getStopTime()) {
                            publishProgress(clip.getStopTime());
                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isCancelled()) {
                    isSkipRunning = false;
                    break;
                }
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);

            if (player != null) {
                player.seekTo(values[0]);
                player.setPlayWhenReady(playWhenReady);
            }

        }
    }

    class JustPlayAsyncTask extends AsyncTask<ClipList, Long, Long> {

        @Override
        protected Long doInBackground(ClipList... clips) {
            ClipList clipList = clips[0];
            List<Clip> list = clipList.getClips();
            Collections.sort(list);
            ClipList clipList1 = new ClipList();
            clipList1.setClips(list);
            clipList1.reversTime(player.getDuration());
            list = clipList1.getClips();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String s = gson.toJson(list);
            Log.i(TAG, "doInBackground: " + s);

            while (isJustPlayRunning) {
                for (Clip clip : list) {
                    if (player != null) {
                        if (player.getCurrentPosition() >= clip.getStartTime() && player.getCurrentPosition() <= clip.getStopTime()) {
                            publishProgress(clip.getStopTime());
                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isCancelled()) {
                    isJustPlayRunning = false;
                    break;
                }
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);

            if (player != null) {
                player.seekTo(values[0]);
                player.setPlayWhenReady(playWhenReady);
            }

        }
    }

    class VolumeAsyncTask extends AsyncTask<ClipList, Void, Void> {

        @Override
        protected Void doInBackground(ClipList... clipLists) {
            ClipList clipList = clipLists[0];
            List<Clip> list = clipList.getClips();
            Collections.sort(list);

            String volumePercentageString = getPref().getString(VOLUME_PERCENTAGE, null);

            float volumePercentage = Float.valueOf(volumePercentageString);

            float playerVolume = 0;
            if (player!=null) {
                playerVolume = player.getVolume();
            }

            while (isVoumeRunning) {
                Log.i(TAG, "doInBackground: ");

                for (Clip clip : list) {
                    if (player != null) {
                        if (player.getCurrentPosition() >= clip.getStartTime() && player.getCurrentPosition() <= clip.getStopTime()) {
                            if (player.getVolume() == volumePercentage) {
                                continue;
                            } else {
                                player.setVolume(volumePercentage);
                                Log.i(TAG, "doInBackground: player.setVolume(volumePercentage);");
                            }
                        }
                        if (player.getCurrentPosition() > clip.getStopTime()) {
                            if (player.getVolume() == volumePercentage) {
                                player.setVolume(playerVolume);
                                Log.i(TAG, "doInBackground: player.setVolume(playerVolume);");
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isCancelled()) {
                    isVoumeRunning = false;
                    break;
                }
            }

            return null;
        }
    }

    class MuteAsyncTask extends AsyncTask<ClipList, Void, Void> {
        @Override
        protected Void doInBackground(ClipList... clipLists) {
            ClipList clipList = clipLists[0];
            List<Clip> list = clipList.getClips();
            Collections.sort(list);

            float volumePercentage = 0.0f;

            float playerVolume = 0;
            if (player!=null) {
                playerVolume = player.getVolume();
            }

            while (isMuteRunning) {
                Log.i(TAG, "doInBackground: ");

                for (Clip clip : list) {
                    if (player != null) {
                        if (player.getCurrentPosition() >= clip.getStartTime() && player.getCurrentPosition() <= clip.getStopTime()) {
                            if (player.getVolume() == volumePercentage) {
                                continue;
                            } else {
                                player.setVolume(volumePercentage);
                                Log.i(TAG, "doInBackground: player.setVolume(volumePercentage);");
                            }
                        }
                        if (player.getCurrentPosition() > clip.getStopTime()) {
                            if (player.getVolume() == volumePercentage) {
                                player.setVolume(playerVolume);
                                Log.i(TAG, "doInBackground: player.setVolume(playerVolume);");
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isCancelled()) {
                    isVoumeRunning = false;
                    break;
                }
            }

            return null;
        }
    }

    private SharedPreferences getPref() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public Set<String> getDefaultCategories() {
        return new HashSet<String>(Arrays.asList(getResources().getStringArray(R.array.clips_categories)));
    }

    private ClipList getClips(String key) {

        String url = videosList.get(videoNo).getUrl().toString();

        String path = url.substring(0, url.lastIndexOf('/'));
        String fileName = videosList.get(videoNo).getTitle() + ".json";

        ClipList selectedClips = new ClipList();

        SharedPreferences sharedPreferences = getPref();
        Set<String> set = sharedPreferences.getStringSet(key, getDefaultCategories());

        Categories categories = FileUtil.read(path, fileName);

        if (categories == null || set.isEmpty())
            return selectedClips;

        for (String category : set) {
            if (categories.getCategories().containsKey(category)) {
                ClipList clipList = categories.getCategories().get(category);
                for (int j = 0; j < clipList.getClips().size(); j++) {
                    selectedClips.getClips().add(clipList.getClips().get(j));
                }
            }
        }
        return selectedClips;
    }

    private ClipList getMuteClips() {
        String url = videosList.get(videoNo).getUrl().toString();

        String path = url.substring(0, url.lastIndexOf('/'));
        String fileName = videosList.get(videoNo).getTitle() + "mute.json";

        ClipList selectedClips = new ClipList();

        Categories categories = FileUtil.read(path, fileName);

        if (categories == null)
            return selectedClips;

        if (categories.getCategories().containsKey("MUTE")) {
            ClipList clipList = categories.getCategories().get("MUTE");
            for (int j = 0; j < clipList.getClips().size(); j++) {
                selectedClips.getClips().add(clipList.getClips().get(j));
            }
        }
        return selectedClips;
    }

    //region Initialize and SettingUp Spinner
    private void settingSpinner() {

        List<String> list = Arrays.asList(getResources().getStringArray(R.array.clips_categories));
        labels = new ArrayList<>(list);

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
        spinnerLabel.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLabel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                label = labels.get(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    //endregion

    //region onViewClicked Method

    @OnClick({R.id.button_hide_mute_bar, R.id.textView_start_time_for_mute, R.id.textView_stop_time_for_mute, R.id.button_save_mute})
    public void onMuteViewClicked(View view) {

        switch (view.getId()) {
            case R.id.button_hide_mute_bar:
                muteControlsContainer.setVisibility(View.GONE);
                titleContainer.setVisibility(View.VISIBLE);
                hideSystemUi();
                break;
            case R.id.textView_start_time_for_mute:
                muteStartTime = player.getCurrentPosition();
                textViewStartTimeForMute.setText(String.valueOf(muteStartTime));
                break;
            case R.id.textView_stop_time_for_mute:
                muteStopTime = player.getCurrentPosition();
                textViewStopTimeForMute.setText(String.valueOf(muteStopTime));
                break;
            case R.id.button_save_mute:
                saveMuteClip();
                break;
        }
    }
    private void saveMuteClip() {

        Categories categories;

        String url = videosList.get(videoNo).getUrl().toString();
        String path = url.substring(0, url.lastIndexOf('/'));
        String fileName = videosList.get(videoNo).getTitle() + "mute.json";

        if (PermissionUtil.isExternalStorageReadable()) {
            if (muteStartTime != -1 && muteStopTime != -1) {

                Clip clip = new Clip(muteStartTime, muteStopTime);

                categories = FileUtil.read(path, fileName);

                if (categories != null) {
                    if (categories.getCategories().containsKey("MUTE")) {
                        ClipList oldClipList = (ClipList) categories.getCategories().get("MUTE");
                        oldClipList.getClips().add(clip);
                        categories.getCategories().put("MUTE", oldClipList);
                    } else {
                        ClipList newClipList = new ClipList();
                        newClipList.getClips().add(clip);
                        categories.getCategories().put("MUTE", newClipList);
                    }
                } else if (categories == null) {
                    ClipList clipList = new ClipList();
                    clipList.getClips().add(clip);

                    categories = new Categories();
                    categories.getCategories().put("MUTE", clipList);
                }

                FileUtil.write(path, fileName, categories);

                Toast.makeText(this, "Record is Saved", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "Record is NOT Saved", Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick({R.id.button_back, R.id.button_add_label, R.id.button_setting,
            R.id.button_hide_label_bar, R.id.textView_start_time,
            R.id.textView_stop_time, R.id.button_save, R.id.exo_prev, R.id.next,
            R.id.button_add_mute})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            //region For next and prev
            case R.id.exo_prev:
                if (--videoNo >= 0) {
                    player.prepare(buildMediaSource(Uri.parse(videosList.get(videoNo).getUrl())));
                } else {
                    // play last song
                    player.prepare(buildMediaSource(Uri.parse(videosList.get(videoNo = videosList.size() - 1).getUrl())));
                }
                player.setPlayWhenReady(playWhenReady);

                break;
            case R.id.next:
                // check if next song is there or not
                if (++videoNo < (videosList.size() - 1)) {
                    player.prepare(buildMediaSource(Uri.parse(videosList.get(videoNo).getUrl())));
                } else {
                    player.prepare(buildMediaSource(Uri.parse(videosList.get(videoNo = 0).getUrl())));
                }
                player.setPlayWhenReady(playWhenReady);

                break;
            //endregion

            //region For player top
            case R.id.button_back:
                finish();
                break;
            case R.id.button_add_label:
                titleContainer.setVisibility(View.GONE);
                skipControlsContainer.setVisibility(View.VISIBLE);
                hideSystemUi();
                break;
            case R.id.button_add_mute:
                titleContainer.setVisibility(View.GONE);
                muteControlsContainer.setVisibility(View.VISIBLE);
                hideSystemUi();
                break;
            case R.id.button_setting:
                startActivity(new Intent(PlayerActivity.this, Setting.class));
                break;
            //endregion

            //region For Skip Control Container
            case R.id.button_hide_label_bar:
                skipControlsContainer.setVisibility(View.GONE);
                titleContainer.setVisibility(View.VISIBLE);
                hideSystemUi();
                break;
            case R.id.textView_start_time:
                mStartTime = player.getCurrentPosition();
                textViewStartTime.setText(String.valueOf(mStartTime));
                break;
            case R.id.textView_stop_time:
                mStopTime = player.getCurrentPosition();
                textViewStopTime.setText(String.valueOf(mStopTime));
                break;
            case R.id.button_save:
                saveClip();
                break;
            //endregion
        }
    }

    private void saveClip() {

        Categories categories;

        String url = videosList.get(videoNo).getUrl().toString();
        String path = url.substring(0, url.lastIndexOf('/'));
        String fileName = videosList.get(videoNo).getTitle() + ".json";

        if (PermissionUtil.isExternalStorageReadable()) {
            if (label != null && mStartTime != -1 && mStopTime != -1) {

                Clip clip = new Clip(label, mStartTime, mStopTime);

                categories = FileUtil.read(path, fileName);

                if (categories != null) {
                    if (categories.getCategories().containsKey(label)) {
                        ClipList oldClipList = (ClipList) categories.getCategories().get(label);
                        oldClipList.getClips().add(clip);
                        categories.getCategories().put(label, oldClipList);
                    } else {
                        ClipList newClipList = new ClipList();
                        newClipList.getClips().add(clip);
                        categories.getCategories().put(label, newClipList);
                    }
                } else if (categories == null) {
                    ClipList clipList = new ClipList();
                    clipList.getClips().add(clip);

                    categories = new Categories();
                    categories.getCategories().put(label, clipList);
                }

                FileUtil.write(path, fileName, categories);

                Toast.makeText(this, "Record is Saved", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "Record is NOT Saved", Toast.LENGTH_SHORT).show();
        }
    }

    //endregion

    //region Life Cycle Methods


    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            if (player == null) {
                initializePlayer();
            }
            player.seekTo(currentWindow, playbackPosition);
            player.setPlayWhenReady(playWhenReady);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isSkipRunning = false;
        isJustPlayRunning = false;
        isVoumeRunning = false;
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUi();
        }
    }

    //endregion

    //region InitializePlayer , ReleasePlayer , BuildMediaSource , hideSystemUi Methods

    private void initializePlayer() {
        if (player == null) {

            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);

            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());

            player.addListener(componentListener);

            playerView.setPlayer(player);

            player.seekTo(currentWindow, playbackPosition);
        }

        player.prepare(buildMediaSource(Uri.parse(videosList.get(videoNo).getUrl())));

        player.setPlayWhenReady(playWhenReady);
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(componentListener);
            player.release();
            player = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultDataSourceFactory(this, "ua"),
                new DefaultExtractorsFactory(), null, null);
    }


    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @OnClick(R.id.skip_controls_container)
    public void onViewClicked() {
    }

    //endregion

    //region ComponentListener Class and its all overrides Methods

    private class ComponentListener implements Player.EventListener {

        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            Log.i(TAG, "onTimelineChanged: ");
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Log.i(TAG, "onTracksChanged: ");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Log.i(TAG, "onLoadingChanged: ");
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case ExoPlayer.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING";
                    break;
                case ExoPlayer.STATE_ENDED:
                    if (skipAsyncTask != null) {
                        skipAsyncTask.cancel(true);
                    }
                    if (justPlayAsyncTask != null) {
                        justPlayAsyncTask.cancel(true);
                    }
                    if (volumeAsyncTask != null) {
                        volumeAsyncTask.cancel(true);
                    }
                    stateString = "ExoPlayer.STATE_ENDED";
                    break;
                case ExoPlayer.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE";
                    break;
                case ExoPlayer.STATE_READY:
                    //Setting Title
                    textViewTitle.setText(videosList.get(videoNo).getTitle().toString());

                    if (getPref().getBoolean(ENABLE_SKIPPING, false)) {
                        ClipList clipList = getClips(SKIP_CATEGORIES);
                        if (!clipList.getClips().isEmpty()) {
                            if (!isSkipRunning) {
                                isSkipRunning = true;
                                skipAsyncTask = new SkipAsyncTask();
                                skipAsyncTask.execute(clipList);
                            }
                        }
                    }

                    if (getPref().getBoolean(ENABLE_JUST_PLAY, false)) {
                        ClipList clipList = getClips(JUST_PLAY_CATEGORIES);
                        if (!clipList.getClips().isEmpty()) {
                            if (!isJustPlayRunning) {
                                isJustPlayRunning = true;
                                justPlayAsyncTask = new JustPlayAsyncTask();
                                justPlayAsyncTask.execute(clipList);
                            }
                        }
                    }

                    if (getPref().getBoolean(ENABLE_VOLUME, false)) {
                        ClipList clipList = getClips(VOLUME_CATEGORIES);
                        Log.i(TAG, "onPlayerStateChanged: ENABLE_VOLUME");
                        if (!clipList.getClips().isEmpty()) {
                            if (!isVoumeRunning) {
                                isVoumeRunning = true;
                                volumeAsyncTask = new VolumeAsyncTask();
                                volumeAsyncTask.execute(clipList);
                            }
                        }
                    }

                    if (getPref().getBoolean(ENABLE_MUTE, false)) {
                        ClipList clipList = getMuteClips();
                        Log.i(TAG, "onPlayerStateChangeddddd: ENABLE_MUTE");
                        if (!clipList.getClips().isEmpty()) {
                            if (!isMuteRunning) {
                                isMuteRunning = true;
                                muteAsyncTask = new MuteAsyncTask();
                                muteAsyncTask.execute(clipList);
                            }
                        }
                    }

                    stateString = "ExoPlayer.STATE_READY";
                    break;
                default:
                    stateString = "Unknown State";
                    break;
            }
            Log.d(TAG, "onPlayerStateChanged: " + stateString + " PlayWhenReady : " + playWhenReady);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            Log.i(TAG, "onPositionDiscontinuity: ");
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            Log.i(TAG, "onPlaybackParametersChanged: ");
        }

        @Override
        public void onSeekProcessed() {
            Log.i(TAG, "onSeekProcessed: ");
        }

    }
    //endregion

    //region Permission CallBack Methods : onRequestPermissionsResult
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    PermissionUtil.checkPermission(this, WRITE_EXTERNAL_STORAGE);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //endregion


}

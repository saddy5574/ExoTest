package com.example.sayyad.exotest.VideoListActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.example.sayyad.exotest.Constants.Constant;
import com.example.sayyad.exotest.Player.PlayerActivity;
import com.example.sayyad.exotest.R;
import com.example.sayyad.exotest.Setting.Setting;
import com.example.sayyad.exotest.Util.PermissionUtil;
import com.example.sayyad.exotest.VideoListActivity.Adapter.VideoModelAdapter;
import com.example.sayyad.exotest.LoadVideos.LoadVideos;
import com.example.sayyad.exotest.VideoListActivity.Model.VideoModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VideoListActivity extends Activity implements Constant, VideoModelAdapter.OnItemClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private RecyclerView recyclerView;
    private ArrayList<VideoModel> videosList;

    public static VideoModelAdapter videoModelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        videosList = new ArrayList<VideoModel>();

        PermissionUtil.checkPermission(this,READ_EXTERNAL_STORAGE);
        if (PermissionUtil.isExternalStorageReadable()){
            LoadVideos loadVideos = new LoadVideos();
            videosList = loadVideos.load(this,Constant.LOAD_VIDEOS_FROM_EXTERNAL_STORAGE).getVideosList();
        }

        videoModelAdapter = new VideoModelAdapter(getApplication(),videosList);
        videoModelAdapter.setOnItemClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(videoModelAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.munu_list_activity,menu);
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                startActivity(new Intent(VideoListActivity.this, Setting.class));
        }
        return super.onOptionsItemSelected(item);
    }

    //region Permission CallBack Methods : onRequestPermissionsResult
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case READ_EXTERNAL_STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    return;
                }else{
                    PermissionUtil.checkPermission(this,Constant.READ_EXTERNAL_STORAGE);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }
    //endregion

    //region OnVideoClick CallBack Method : onItemClick
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(VideoListActivity.this,PlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION",position);
        intent.putExtra("BUNDLE",bundle);
        startActivity(intent);
    }
    //endregion

}

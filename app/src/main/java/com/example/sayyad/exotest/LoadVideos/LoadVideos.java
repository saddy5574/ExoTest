package com.example.sayyad.exotest.LoadVideos;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.example.sayyad.exotest.Constants.Constant;
import com.example.sayyad.exotest.Util.PermissionUtil;
import com.example.sayyad.exotest.VideoListActivity.Model.VideoModel;

import java.util.ArrayList;

public class LoadVideos implements Constant{

    private  Context mContext;
    private static ArrayList<VideoModel> videosList;

    public LoadVideos() {
        videosList = new ArrayList<>();
    }

    public LoadVideos load(Context context, int LOAD_FROM){
        mContext = context;
        if (LOAD_FROM == LOAD_VIDEOS_FROM_EXTERNAL_STORAGE){
            loadVideosFromSDCard();
        }
        return this;
    }

    public static ArrayList<VideoModel> getVideosList() {
        return videosList;
    }

    private  void loadVideosFromSDCard() {
        if (!PermissionUtil.isExternalStorageReadable())
            return;

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Uri urit = MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                        String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        String url = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Video.Thumbnails.MINI_KIND);
                        VideoModel video = new VideoModel(title, Integer.valueOf(duration), url,bitmap);

                        videosList.add(video);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }

    //region CreateThumbs
       /* private class CreateThumbs extends AsyncTask<String, String, ArrayList<VideoModel>> {

            ArrayList<VideoModel> list;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                list = new ArrayList<>();
            }

            @Override
            protected ArrayList<VideoModel> doInBackground(String... strings) {
                list = getVideosList();
                if (!list.isEmpty()){
                    for (int i = 0; i < list.size(); i++) {
                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(list.get(i).getUrl(),
                                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                        list.get(i).setThumbnail(bitmap);
                    }
                }
                return list;
            }

            @Override
            protected void onPostExecute(ArrayList<VideoModel> videoModels) {
                super.onPostExecute(videoModels);
                videosList = videoModels;
            }
        } */
    //endregion
}

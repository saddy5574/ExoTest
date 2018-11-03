package com.example.sayyad.exotest.Util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.example.sayyad.exotest.Constants.Constant;

public class PermissionUtil implements Constant{


    public static void checkPermission(Context context,String permission){
      if (permission.equals(READ_EXTERNAL_STORAGE)){
          readExternalStorage(context,permission);
      }
      if (permission.equals(WRITE_EXTERNAL_STORAGE)){
          writeExternalStrage(context,permission);
      }
      if (permission.equals(WRITE_SETTINGS)){
          writeSetting(context,permission);
      }
    }

    private static void writeSetting(Context context, String permission) {
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context,new String[]{permission},
                        WRITE_SETTING_REQUEST_CODE);
                return;
            }
        }
    }

    private static void writeExternalStrage(Context context,String permission) {
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context,new String[]{permission},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                return;
            }
        }
    }

    private static void readExternalStorage(Context context,String permission){

        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) context,new String[]{permission},
                        READ_EXTERNAL_STORAGE_REQUEST_CODE);
                return;
            }
        }
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}

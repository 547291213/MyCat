package com.example.xkfeng.mycat.Util;

import android.os.Environment;

public class FileHelper {

    public static boolean isSdCardExist(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ;
    }
}

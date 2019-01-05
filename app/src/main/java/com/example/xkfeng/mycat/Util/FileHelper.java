package com.example.xkfeng.mycat.Util;

import android.os.Environment;

import java.text.NumberFormat;

public class FileHelper {

    public static boolean isSdCardExist(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ;
    }

    public static String getFileSize(Number fileSize){
        NumberFormat ddf1 =NumberFormat.getInstance() ;
        ddf1.setMaximumIntegerDigits(2);
        double size =fileSize.doubleValue() ;
        String sizeDisplay = null;
        if (size> 1048576.0) {
            double result = size / 1048576.0;
            sizeDisplay = ddf1.format(result) + " MB";
        } else if (size > 1024) {
            double result = size/ 1024;
            sizeDisplay = ddf1.format(result) + " KB";

        } else {
            sizeDisplay = ddf1.format(size) + " B";
        }

        return sizeDisplay ;
    }
}

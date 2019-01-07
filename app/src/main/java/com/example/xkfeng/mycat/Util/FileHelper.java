package com.example.xkfeng.mycat.Util;

import android.graphics.BitmapFactory;
import android.os.Environment;

import java.text.NumberFormat;

public class FileHelper {

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getFileSize(Number fileSize) {
        NumberFormat ddf1 = NumberFormat.getInstance();
        ddf1.setMaximumIntegerDigits(2);
        double size = fileSize.doubleValue();
        String sizeDisplay = null;
        if (size > 1048576.0) {
            double result = size / 1048576.0;
            sizeDisplay = ddf1.format(result) + " MB";
        } else if (size > 1024) {
            double result = size / 1024;
            sizeDisplay = ddf1.format(result) + " KB";

        } else {
            sizeDisplay = ddf1.format(size) + " B";
        }

        return sizeDisplay;
    }

    /**
     * 如果以图片720 ，1280为准界限
     * @param path
     * @return true 范围在720，1280之内 ， false范围在720，1280之外
     */
    private static final int MAX_WIDTH = 720 ;
    private static final int MAX_HEIGHT = 1280 ;
    public static boolean verifyPicFileSize(String path){
        BitmapFactory.Options options = new BitmapFactory.Options() ;
        options.inJustDecodeBounds = true ;
        BitmapFactory.decodeFile(path , options) ;
        if (options.outWidth <= MAX_WIDTH && options.outHeight <= MAX_HEIGHT){
            return true ;
        }
        return false ;
    }
}

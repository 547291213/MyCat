package com.example.xkfeng.mycat.Util;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.example.xkfeng.mycat.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;

public class FileHelper {

    private static FileHelper mInstance = new FileHelper();

    public static FileHelper getInstance() {
        return mInstance;
    }

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


    public void copyFile(final String fileName, final String filePath, final Activity context,
                         final CopyFileCallback callback) {
        if (isSdCardExist()) {
            final Dialog dialog = DialogHelper.createLoadingDialog(context,
                    "正在加载");
            dialog.show();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileInputStream fis = new FileInputStream(new File(filePath));
                        File destDir = new File(StaticValueHelper.FILE_DIR);
                        if (!destDir.exists()) {
                            destDir.mkdirs();
                        }
                        final File tempFile = new File(StaticValueHelper.FILE_DIR + fileName);
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        byte[] bt = new byte[1024];
                        int c;
                        while((c = fis.read(bt)) > 0) {
                            fos.write(bt,0,c);
                        }
                        //关闭输入、输出流
                        fis.close();
                        fos.close();

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.copyCallback(Uri.fromFile(tempFile));
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            });
            thread.start();
        }else {
            ITosast.showShort(context , context.getResources().getString(R.string.sdcard_not_prepare_toast)).show();
        }
    }


    public interface CopyFileCallback {
        public void copyCallback(Uri uri);
    }
}

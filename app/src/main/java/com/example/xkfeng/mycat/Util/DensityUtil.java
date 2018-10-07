package com.example.xkfeng.mycat.Util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;

/**
 * Created by initializing on 2018/10/5.
 */

public class DensityUtil {

    /**
     *
     * @param context 内容上下文
     * @param dpValue dp值
     * @return   对应屏幕分辨率的px值
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String getResourcesUri(Context context ,@DrawableRes int id) {
        Resources resources = context.getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(id) + "/" + resources.getResourceTypeName(id) + "/" + resources.getResourceEntryName(id);
        return uriPath;
    }


}

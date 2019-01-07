package com.example.xkfeng.mycat.Util;

import android.content.Context;

public class ProviderUtil {
    public static String getFileProviderName(Context context){
        return context.getPackageName()+".provider";
    }
}

package com.example.xkfeng.mycat.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xkfeng.mycat.R;

public class ITosast {

    /**
     * 自定义 Toast.LENGTH_SHORT
     * @param context
     * @param string   toast显示字符串
     */
    public static  void showShort(Context context , String string) {
        ITosast.show(context , string , Toast.LENGTH_SHORT);
    }

    /**
     * 自定义 Toast.LENGTH_LONG
     * @param context
     * @param string toast显示字符串
     */
    public static void showLong(Context context ,String string){
        ITosast.show(context ,string , Toast.LENGTH_LONG);
    }

    /**
     * 自定义Toast，自定义显示时间
     * @param context
     * @param string  toast 显示字符串
     * @param time  toast显示时间
     */
    public static void show(Context context ,String string , int time ){
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout ,null ,false) ;
        ((TextView)view.findViewById(R.id.tv_toastText)).setText(string);
        Toast toast = new Toast(context) ;
        toast.setDuration(time);
        toast.setView(view);
        toast.show();
    }

}

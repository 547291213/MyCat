package com.example.xkfeng.mycat.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.R;

/**
 * 单例设计模式
 */
public class ITosast {

    private static ITosast instance;
    private static View view;
    private static Toast toast;


    private ITosast() {
    }

    public static ITosast getInstance() {
        if (instance == null) {
            synchronized (ITosast.class) {
                if (instance == null) {
                    instance = new ITosast();
                }
            }
        }
        return instance;
    }

    /**
     * 自定义 Toast.LENGTH_SHORT
     *
     * @param context
     * @param string  toast显示字符串
     */
    public static ITosast showShort(Context context, String string) {

        return ITosast.show(context, string, Toast.LENGTH_SHORT);


    }

    /**
     * 自定义 Toast.LENGTH_LONG
     *
     * @param context
     * @param string  toast显示字符串
     */
    public static ITosast showLong(Context context, String string) {

        return ITosast.show(context, string, Toast.LENGTH_LONG);

    }

    /**
     * 自定义Toast，自定义显示时间
     *
     * @param context
     * @param string  toast 显示字符串
     * @param time    toast显示时间
     */
    public static ITosast show(Context context, String string, int time) {
        view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null, false);
        ((TextView) view.findViewById(R.id.tv_toastText)).setText(string);
        toast = new Toast(context);
        toast.setDuration(time);
        toast.setView(view);
        return ITosast.getInstance();
    }

    public void show() {
        if (toast != null) {
            toast.show();
        }

    }

    /**
     * set Toast text-size
     *
     * @param size 字体大小
     * @return IToast object
     */
    public ITosast setTextSize(int size) {

        if (view != null) {
            ((TextView) view.findViewById(R.id.tv_toastText)).setTextSize(size);
        }

        try {
            isNull();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * set Toast text color
     *
     * @param color
     * @return IToast object
     */
    public ITosast setTextColor(int color) {

        if (view != null) {
            ((TextView) view.findViewById(R.id.tv_toastText)).setTextColor(color);
        }

        try {
            isNull();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * 为Toast加载图片
     * @param resource  图片资源
     * @param context  上下文
     * @return  ITosast object
     */
    public ITosast loadImage(int resource , Context context){
        if (view != null){
            Glide.with(context).load(resource).into(((ImageView) view.findViewById(R.id.iv_toastImage))) ;
        }
        try {
            isNull();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance ;
    }

    /**
     * 判断当前Instance是否为空
     * @throws Exception
     */
    private void isNull() throws Exception {
        if (instance == null)
        {
            throw new Exception("ITosast is null object")  ;
        }
    }

}

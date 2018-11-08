package com.example.xkfeng.mycat.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.xkfeng.mycat.Activity.BaseActivity;

/**
 * 单例设计模式
 * 1 帮助快捷获取自动登陆需要的相关属性
 * username , password and so on ...
 * <p>
 * 2 帮助快捷获取用户的一些设置属性
 * nakeName ， roaming and so on ...
 */
public class UserAutoLoginHelper {

    private static UserAutoLoginHelper userAutoLoginHelper;
    private static SharedPreferences sharedPreferences;

    private UserAutoLoginHelper(Context context) {

        sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
    }

    /**
     * 获取单例对象
     */
    public static UserAutoLoginHelper getUserAutoLoginHelper(Context context) {
        if (userAutoLoginHelper == null) {
            synchronized (UserAutoLoginHelper.class) {
                if (userAutoLoginHelper == null) {
                    userAutoLoginHelper = new UserAutoLoginHelper(context);
                }
            }
        }
        return userAutoLoginHelper;
    }

    /**
     * 用户名
     */
    public void setUserName(String userName) {
        sharedPreferences.edit().putString("userName", userName).commit();
    }

    public String getUserName() {
        return sharedPreferences.getString("userName", "");
    }

    /**
     * 用户密码
     */
    public void setUserPassword(String userPassword) {
        sharedPreferences.edit().putString("userPassword", userPassword).commit();

    }

    public String getUserPassword() {
        return sharedPreferences.getString("userPassword", "");
    }

    /**
     * 昵称
     */
    public void setNakeName(String guestId) {
        sharedPreferences.edit().putString("userNakeName", guestId).commit();
    }

    public String getNakeName() {
        return sharedPreferences.getString("userNakeName", "");
    }

    /**
     * 漫游开启状态
     */
    public void setRoaming(boolean flag) {
        sharedPreferences.edit().putBoolean("roaming", flag).commit();
    }

    public boolean getRoaming() {
        return sharedPreferences.getBoolean("roaming", false);
    }

    /**
     * 推送开启状态
     */
    public void setPush(boolean flag) {
        sharedPreferences.edit().putBoolean("push", flag).commit();
    }

    public boolean getPush() {
        return sharedPreferences.getBoolean("push", false);
    }


    /**
     * 声音推送开启状态
     */
    public void setMusic(boolean flag) {
        sharedPreferences.edit().putBoolean("push_music", flag).commit();
    }
    public boolean getMusic() {
        return sharedPreferences.getBoolean("push_music", false);
    }



    /**
     * 震动开启状态
     */
    public void setVib(boolean flag) {
        sharedPreferences.edit().putBoolean("push_vib", flag).commit();
    }

    public boolean getVib() {
        return sharedPreferences.getBoolean("push_vib", false);
    }

    /**
     * App key
     */
    public void setAppKey(String appKey) {
        sharedPreferences.edit().putString("appkey", appKey).commit();
    }

    public String getAppKey() {
        return sharedPreferences.getString("appkey", "");
    }


}

package com.example.xkfeng.mycat.Model;

import cn.jpush.im.android.api.model.UserInfo;

public class FriendInfo {
    private UserInfo userInfo ;
    private String firstLetter ;


    public FriendInfo(){}

    public FriendInfo(UserInfo userInfo , String firstLetter){
        this.userInfo = userInfo ;
        this.firstLetter = firstLetter ;
    }
    public String getFirstLetter() {
        return firstLetter;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}

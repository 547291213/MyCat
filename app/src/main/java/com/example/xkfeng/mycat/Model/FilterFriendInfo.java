package com.example.xkfeng.mycat.Model;

import cn.jpush.im.android.api.model.UserInfo;

public class FilterFriendInfo {
    private UserInfo userInfo ;
    private String fileterName ;


    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getFileterName() {
        return fileterName;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public void setFileterName(String fileterName) {
        this.fileterName = fileterName;
    }
}

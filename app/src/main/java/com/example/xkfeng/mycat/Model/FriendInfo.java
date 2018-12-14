package com.example.xkfeng.mycat.Model;

import cn.jpush.im.android.api.model.UserInfo;

public class FriendInfo {
    /**
     * 用户信息类
     */
    private UserInfo userInfo ;
    /**
     * 用户首字母
     */
    private String firstLetter ;
    /**
     * 用户标题，所有字母，
     * 使用原因：
     *    只用首字母进行排序，列表项每次排序的结果不一致
     */
    private String allLetter ;
    /**
     * 用户标题，
     * 避免重复的代码
     */
    private String titleName ;


    public FriendInfo(){}

    public FriendInfo(UserInfo userInfo , String firstLetter ,String allLetter ,String titleName){
        this.userInfo = userInfo ;
        this.firstLetter = firstLetter ;
        this.allLetter = allLetter ;
        this.titleName = titleName ;

    }
    public String getFirstLetter() {
        return firstLetter;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getAllLetter() {
        return allLetter;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }


    public void setAllLetter(String allLetter) {
        this.allLetter = allLetter;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }
}

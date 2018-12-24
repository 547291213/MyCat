package com.example.xkfeng.mycat.Model;

import com.example.xkfeng.mycat.Activity.GroupListActivity;

import java.util.List;
import cn.jpush.im.android.api.model.UserInfo;

public class SearchContact {

    private String nameFilter ;
    private List<FilterFriendInfo> friendInfoList ;
    private List<FilterGroupInfo> groupInfoList ;

    public SearchContact(){}

    public SearchContact(String nameFilter , List<FilterFriendInfo> friendInfoList , List<FilterGroupInfo> groupInfoList){
        this.nameFilter = nameFilter ;
        this.friendInfoList = friendInfoList ;
        this.groupInfoList = groupInfoList ;

    }


    public List<FilterGroupInfo> getGroupInfoList() {
        return groupInfoList;
    }

    public List<FilterFriendInfo> getFriendInfoList() {
        return friendInfoList;
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public void setFriendInfoList(List<FilterFriendInfo> friendInfoList) {
        this.friendInfoList = friendInfoList;
    }

    public void setGroupInfoList(List<FilterGroupInfo> groupInfoList) {
        this.groupInfoList = groupInfoList;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }
}


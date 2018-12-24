package com.example.xkfeng.mycat.Model;

import cn.jpush.im.android.api.model.GroupInfo;

public class FilterGroupInfo {

    private GroupInfo groupInfo ;
    private String filterName ;


    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }
}

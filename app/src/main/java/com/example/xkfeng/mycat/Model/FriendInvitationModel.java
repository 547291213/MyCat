package com.example.xkfeng.mycat.Model;

public class FriendInvitationModel {

    /**
     * 当前用户
     */
    private String mUserName ;

    /**
     * 像当前用用户发出添加好友申请的用户
     */
    private String mFromUser ;

    /**
     * 审核状态
     * 3 尚未处理
     * 2 接受
     * 1 拒绝
     */
    private int state ;

    private String reason ;

    /**
     * 用于唯一标识码
     * fromUser + sednTime ;
     */
    private long fromUserTime ;


    public FriendInvitationModel(){}

    public FriendInvitationModel(String mUserName ,String mFromUser ,int state){
        this.mUserName = mUserName ;
        this.mFromUser = mFromUser ;
        this.state = state ;
    }

    public int getState() {
        return state;
    }

    public String getmFromUser() {
        return mFromUser;
    }

    public String getmUserName() {
        return mUserName;
    }

    public String getReason() {
        return reason;
    }


    public long getFromUserTime() {
        return fromUserTime;
    }

    public void setmFromUser(String mFromUser) {
        this.mFromUser = mFromUser;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setFromUserTime(long fromUserTime) {
        this.fromUserTime = fromUserTime;
    }
}


package com.example.xkfeng.mycat.Model;

import java.io.Serializable;

import cn.jpush.im.android.api.model.Conversation;

public class JPushMessageInfo implements Serializable {

    //登录状态
    private boolean login;
    //在线状态
    private boolean online;
    private int type;
    private String img;
    private String msgID;
    private String title;
    private String content;
    private String time;
    private String unReadCount ;
    private String userName;
    private Boolean isFriends;
    private Conversation conversation;
    private int MsgType;


    public void setUnReadCount(String unReadCount) {
        this.unReadCount = unReadCount;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFriends(Boolean friends) {
        isFriends = friends;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public void setMsgType(int msgType) {
        MsgType = msgType;
    }

    public boolean isLogin() {
        return login;
    }

    public boolean isOnline() {
        return online;
    }



    public String getUnReadCount() {
        return unReadCount;
    }

    public int getType() {
        return type;
    }

    public String getImg() {
        return img;
    }

    public String getMsgID() {
        return msgID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getUserName() {
        return userName;
    }

    public Boolean getFriends() {
        return isFriends;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public int getMsgType() {
        return MsgType;
    }
}

package com.example.xkfeng.mycat.Model;

public class DraftMsg {
    private String msg ;
    private String userName ;

    public DraftMsg() {}

    public DraftMsg(String msg , String userName){
        this.msg = msg ;
        this.userName = userName ;
    }
    public String getMsg() {
        return msg;
    }

    public String getUserName() {
        return userName;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

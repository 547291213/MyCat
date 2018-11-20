package com.example.xkfeng.mycat.Model;

public class MsgEvent {
    private String msg;

    private LocationBean t;


    public MsgEvent(String msg ,LocationBean t){
        this.msg = msg ;
        this.t = t ;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public LocationBean getT() {
        return t;
    }

    public void setT(LocationBean t) {
        this.t = t;
    }
}

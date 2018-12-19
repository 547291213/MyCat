package com.example.xkfeng.mycat.Model;

import com.example.xkfeng.mycat.RecyclerDefine.QuickAdapter;

public class HasMsgListOpen {


    private QuickAdapter.VH vh ;

    private String msg ;

    public QuickAdapter.VH getVh() {
        return vh;
    }

    public String getMsg() {
        return msg;
    }

    public void setVh(QuickAdapter.VH vh) {
        this.vh = vh;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

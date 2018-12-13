package com.example.xkfeng.mycat.Model;

import android.graphics.Bitmap;

public class ForwardingFriendInfo {

    private String userName ;
    private Bitmap headerBitmap ;

    public Bitmap getHeaderBitmap() {
        return headerBitmap;
    }

    public String getUserName() {
        return userName;
    }

    public void setHeaderBitmap(Bitmap headerBitmap) {
        this.headerBitmap = headerBitmap;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

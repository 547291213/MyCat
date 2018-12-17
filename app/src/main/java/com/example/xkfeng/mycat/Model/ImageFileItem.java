package com.example.xkfeng.mycat.Model;

import java.text.NumberFormat;

import cn.jpush.im.android.api.model.Message;

public class ImageFileItem {

    private String mFilePath;
    private String mFileName;
    private String mSize;
    private String mDate;
    private int section;
    private int msgId;
    private String mUserName;
    private Message mMessage;

    public ImageFileItem(){}

    public ImageFileItem(String mFilePath , String mFileName , String mSize , String date , int msgId){
        this.mFileName = mFileName ;
        this.mFilePath = mFilePath ;
        this.mDate = date ;
        this.mSize = mSize ;
        this.msgId = msgId ;


    }


    public String getFileSize() {
        NumberFormat ddf1 = NumberFormat.getNumberInstance();
        //保留小数点后两位
        ddf1.setMaximumFractionDigits(2);
        long size = Long.valueOf(mSize);
        String sizeDisplay;
        if (size > 1048576.0) {
            double result = size / 1048576.0;
            sizeDisplay = ddf1.format(result) + "M";
        } else if (size > 1024) {
            double result = size / 1024;
            sizeDisplay = ddf1.format(result) + "K";

        } else {
            sizeDisplay = ddf1.format(size) + "B";
        }
        return sizeDisplay;
    }

    public String getmFilePath() {
        return mFilePath;
    }

    public String getmFileName() {
        return mFileName;
    }

    public String getmSize() {
        return mSize;
    }

    public String getmDate() {
        return mDate;
    }

    public int getSection() {
        return section;
    }

    public int getMsgId() {
        return msgId;
    }

    public String getmUserName() {
        return mUserName;
    }

    public Message getmMessage() {
        return mMessage;
    }

    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public void setmSize(String mSize) {
        this.mSize = mSize;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public void setmMessage(Message mMessage) {
        this.mMessage = mMessage;
    }
}

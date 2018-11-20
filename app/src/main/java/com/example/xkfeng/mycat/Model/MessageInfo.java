package com.example.xkfeng.mycat.Model;

public class MessageInfo {
    private String imageFile ;
    private String title ;
    private String content ;
    private String time ;
    private String messageNotRead ;

    public MessageInfo(){}

    public MessageInfo(String imageFile ,String title ,String content ,String time ,String messageNotRead){
        this.imageFile = imageFile ;
        this.title = title ;
        this.content = content ;
        this.time = time ;
        this.messageNotRead = messageNotRead ;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
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

    public void setMessageNotRead(String messageNotRead) {
        this.messageNotRead = messageNotRead;
    }

    public String getImageFile() {
        return imageFile;
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

    public String getMessageNotRead() {
        return messageNotRead;
    }
}

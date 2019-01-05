package com.example.xkfeng.mycat.Interface;

import com.example.xkfeng.mycat.Model.FileType;

public interface UpdateSelectedStateListener {

    public void onSelected(String path, long fileSize, FileType type);
    public void onUnselected(String path, long fileSize, FileType type);
}

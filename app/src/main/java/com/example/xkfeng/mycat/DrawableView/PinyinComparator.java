package com.example.xkfeng.mycat.DrawableView;

import com.example.xkfeng.mycat.Model.FriendInfo;

import java.util.Comparator;

public class PinyinComparator implements Comparator<FriendInfo> {

    @Override
    public int compare(FriendInfo o1, FriendInfo o2) {
        //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
//        if (o1.getFirstLetter().equals("@") || o2.getFirstLetter().equals("#")) {
//            return -1;
//        } else if (o1.getFirstLetter().equals("#") || o2.getFirstLetter().equals("@")) {
//            return 1;
//        } else {
//            return o1.getFirstLetter().compareTo(o2.getFirstLetter());
//        }
        if (o1.getAllLetter().startsWith("@") || o2.getAllLetter().startsWith("#")) {
            return -1;

        } else if (o1.getAllLetter().startsWith("#") || o2.getAllLetter().startsWith("@")) {
            return 1;
        } else {
            return o1.getAllLetter().compareTo(o2.getAllLetter());
        }
    }

}

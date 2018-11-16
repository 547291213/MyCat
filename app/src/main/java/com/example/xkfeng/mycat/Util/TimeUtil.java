package com.example.xkfeng.mycat.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    //时间转化毫秒
    public static long date2ms(String dateForamt,String time) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat(dateForamt).parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }

    //毫秒转化成日期
    public static String ms2date(String dateForamt,long ms){
        Date date = new Date(ms);
        SimpleDateFormat format = new SimpleDateFormat(dateForamt);
        return format.format(date);
    }

    /**时间戳转日期*/
    public static String unix2Date(String dateForamt, long ms) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateForamt);
        String sd = sdf.format(new Date(ms*1000));
        return sd;
    }
}
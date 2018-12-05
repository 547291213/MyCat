package com.example.xkfeng.mycat.Util;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.example.xkfeng.mycat.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.xkfeng.mycat.Util.TimeUtil.Time_Difference.*;

/**
 * time utils
 * 时间与毫秒的切换，毫秒与日期切换，时间转换为仿QQ消息时间格式的实现
 */
public class TimeUtil {

    enum Time_Difference {
        zero, one, two, three, four, five, six, seven
    }

    private static final int MONTH_DAY_THIRTYONE = 31;
    private static final int MONTH_DAY_THIRTY = 30;
    private static final int MONTH_DAY_TWENTYNINE = 29;
    private static final int MONTH_DAY_TWENTYEIGHT = 28;
    private static final int AT_MOON = 12;
    private Context mContext;
    private long msgTime;


    //时间转化毫秒
    public static long date2ms(String dateForamt, String time) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat(dateForamt).parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }

    //毫秒转化成日期
    public static String ms2date(String dateForamt, long ms) {
        Date date = new Date(ms);
        SimpleDateFormat format = new SimpleDateFormat(dateForamt);
        return format.format(date);
    }

    /**
     * 时间戳转日期
     */
    public static String unix2Date(String dateForamt, long ms) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateForamt);
        String sd = sdf.format(new Date(ms * 1000));
        return sd;
    }

    public TimeUtil() {
    }

    public TimeUtil(Context context, long msgTime) {
        mContext = context;
        this.msgTime = msgTime;
    }

    /**
     * 会话内时间显示规则：
     * 当天消息显示上午/下午+具体时间, 举例子：下午 18:09 ， 上午 8：30
     * 昨天和前天，举例: 昨天 18:09
     * 近7天（排除今天，昨天，前天）举例：周日 18:09
     * 今年其他时间，举例：4-22 18:09
     * 今年之前的时间，举例：2015-4-22 18:09
     * 前置条件外部实现：时间显示的间隔：当两次发送或收取消息间隔大于5分钟，则显示新的时间
     */
    public String getDetailTime() {
        if(mContext == null || msgTime == 0){
            return "";
        }
        String result = null;
        Date date = new Date(msgTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String oldTime = sdf.format(date);
        String oldY = oldTime.substring(0, 4);
        int oldM = Integer.parseInt(oldTime.substring(5, 7));
        int oldD = Integer.parseInt(oldTime.substring(8, 10));
        /**
         * 小时和分钟是需要具体显示的数据
         * 转为int，存在显示BUG，比如分钟为05，就会直接显示5，效果很差
         */
        String oldHour = oldTime.substring(11, 13);
        String oldMinutes = oldTime.substring(14, 16);

        String newTime = TimeUtil.ms2date("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
        String newY = newTime.substring(0, 4);
        int newM = Integer.parseInt(newTime.substring(5, 7));
        int newD = Integer.parseInt(newTime.substring(8, 10));

        if (!oldY.equals(newY)) {
            result = oldY + "" + oldM + "" + oldD;
            return result;
        } else {
            if (oldM == newM) {
                return prindTimeForSwitch(newD - oldD, date, oldM, oldD, oldHour, oldMinutes);
            } else {
                //not this month
                //find out what might be adjacent
                if ((newM - oldM == 1) && (oldM == 12 && newM == 1)) {
                    if (oldM == 1 || oldM == 3 || oldM == 5 || oldM == 7 || oldM == 8 || oldM == 10 || oldM == 12) {
                        return prindTimeForSwitch(newD + MONTH_DAY_THIRTYONE - oldD, date, oldM, oldD, oldHour, oldMinutes);
                    } else if (oldM == 4 || oldM == 6 || oldM == 9 || oldM == 11) {
                        return prindTimeForSwitch(newD + MONTH_DAY_THIRTY - oldD, date, oldM, oldD, oldHour, oldMinutes);
                    } else if (oldM == 2) {
                        boolean isLeapYear = yearIsLeap(Integer.parseInt(oldY));
                        if (isLeapYear) {
                            return prindTimeForSwitch(newD + MONTH_DAY_TWENTYNINE - oldD, date, oldM, oldD, oldHour, oldMinutes);
                        } else {
                            return prindTimeForSwitch(newD + MONTH_DAY_TWENTYEIGHT - oldD, date, oldM, oldD, oldHour, oldMinutes);
                        }
                    }
                } else {
                    //month non-adjacent
                    return oldM + "-" + oldD + " " + oldHour + ":" + oldMinutes;
                }
            }
        }
        return result ;
    }



    private String prindTimeForSwitch(int diffTimeValue, Date date, int oldM, int oldD,
                                      String oldHour, String oldMinutes) {
        switch (diffTimeValue) {
            case 0 :
                if (Integer.parseInt(oldHour) > AT_MOON){
                    return mContext.getResources().getString(R.string.afternoon) + " " + oldHour + ":" + oldM ;
                }else {
                    return mContext.getResources().getString(R.string.morning) + " " + oldHour + ":" + oldM ;

                }
            case 1:
                return "昨天 " + oldHour + ":" + oldMinutes;
            case 2:
                return "前天" + oldHour + ":" + oldMinutes;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return getChineseWeekDay(date) + "" + oldHour + ":" + oldMinutes;
            default:
                return oldM + "-" + oldD + " " + oldHour + ":" + oldMinutes;

        }

    }

    private String getChineseWeekDay(@NonNull Date date) {
        switch (date.getDay()) {
            case 1:
                return "周一 ";
            case 2:
                return "周二 ";
            case 3:
                return "周三 ";
            case 4:
                return "周四 ";
            case 5:
                return "周五";
            case 6:
                return "周六 ";
            default:
                return "周日 ";
        }
    }

    /**
     * @param year
     * @return true is leap year , false is non-leap year
     */
    private boolean yearIsLeap(int year) {
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            return true;
        }
        return false;
    }
}
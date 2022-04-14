package com.seraph.ppschedule.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String timeStamp2Time(long time) {
        return new SimpleDateFormat("HH:mm", Locale.CHINA).format(new Date(time));
    }

    /**
     * 时间戳转换成日期格式字符串
     *
     * @param time   时间戳
     * @param format
     * @return
     */
    public static String timeStamp2Date(long time, String format) {
        if (time == 0) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param date   字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long date2TimeStamp(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String date2Time(Date date) {
        return new SimpleDateFormat("HH:mm", Locale.CHINA).format(date);
    }

    public static String date2DateString(Date date) {
        return new SimpleDateFormat("yyyy/MM/dd").format(date);
    }

}

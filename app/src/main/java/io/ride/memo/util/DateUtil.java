package io.ride.memo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ride on 17-12-15.
 * 时间转字符串, 字符串转时间工具类
 */

public class DateUtil {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String formatTime(Date date) {
        return format.format(date);
    }

    public static Date str2TDate(String str) throws ParseException {
        return format.parse(str);
    }
}

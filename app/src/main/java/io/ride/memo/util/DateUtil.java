package io.ride.memo.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ride on 17-12-15.
 * 时间转字符串, 字符串转时间工具类
 */

public class DateUtil {

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * 将date类型转换为字符串类型
     *
     * @param date 被转换的日期类型
     * @return 转换失败
     */
    public static String formatTime(Date date) {
        return format.format(date);
    }

    /**
     * 将字符串转换成date
     *
     * @param str 转换的字符串
     * @return 转换后的字符串
     * @throws ParseException 转换失败
     */
    public static Date str2TDate(String str) throws ParseException {
        return format.parse(str);
    }
}

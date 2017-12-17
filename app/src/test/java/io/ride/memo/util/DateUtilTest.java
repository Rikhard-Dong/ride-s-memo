package io.ride.memo.util;

import org.junit.Test;

import java.util.Date;

/**
 * Created by ride on 17-12-15.
 * test
 */
public class DateUtilTest {
    @Test
    public void str2TDate() throws Exception {
        System.out.println(DateUtil.str2TDate("2017-12-1 10:10"));
    }

    @Test
    public void formatTime() throws Exception {
        System.out.println(DateUtil.formatTime(new Date()));
    }

}
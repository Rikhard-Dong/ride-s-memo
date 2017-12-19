package io.ride.memo;

import android.os.SystemClock;

import org.junit.Test;

import java.util.Date;

import io.ride.memo.util.DateUtil;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        System.out.println(new Date());
        System.out.println(DateUtil.formatTime(new Date()));
    }
}
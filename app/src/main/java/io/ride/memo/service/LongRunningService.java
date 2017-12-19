package io.ride.memo.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;

import io.ride.memo.dao.MemoDao;
import io.ride.memo.model.Memo;
import io.ride.memo.receiver.AlarmReceiver;

/**
 * Created by ride on 17-12-16.
 * <p>
 * 服务, 打开到期的时间
 */

public class LongRunningService extends Service {

    MemoDao memoDao;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        memoDao = new MemoDao(this);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        try {
            // 获取到需要提醒的数据
            Memo memo = memoDao.queryByRecentWarmMemo();
            Log.i("ride-memo", "get memo is " + memo);
            if (memo != null) {
                Long targetTime = memo.getWarmTime().getTime();
                Intent i = new Intent(this, AlarmReceiver.class);
                i.putExtra("id", memo.getId());
                PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
                manager.set(AlarmManager.RTC_WAKEUP, targetTime, pi);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return super.onStartCommand(intent, flags, startId);
    }
}

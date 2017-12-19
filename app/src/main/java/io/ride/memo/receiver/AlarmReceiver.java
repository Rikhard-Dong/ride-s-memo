package io.ride.memo.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;

import io.ride.memo.R;
import io.ride.memo.activity.MemoActivity;
import io.ride.memo.dao.MemoDao;
import io.ride.memo.model.Memo;
import io.ride.memo.service.LongRunningService;

/**
 * Created by ride on 17-12-16.
 * 广播
 */

public class AlarmReceiver extends BroadcastReceiver {
    private int id = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        MemoDao memoDao = new MemoDao(context);
        int id = intent.getIntExtra("id", -1);
        Log.i("ride-memo", "get id is " + id);
        try {
            if (id != -1) {
                Memo memo = memoDao.queryById(id);
                /*
                 * memo为null的情况: 当设置完提醒的memo被删除后, memo则为空
                 */
                if (memo != null) {
                    notification(memo, context);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent intent1 = new Intent(context, LongRunningService.class);
        context.startService(intent1);
    }

    private void notification(Memo memo, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.memo);
        builder.setContentTitle("备忘录-提醒");
        String content = memo.getContent().length() > 32 ? memo.getContent().substring(0, 31) : memo.getContent();
        builder.setContentText(content);
        builder.setAutoCancel(true);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setVibrate(new long[]{0, 1000, 1000, 1000});


        Intent intent = new Intent(context, MemoActivity.class);
        intent.putExtra("id", memo.getId());
        intent.putExtra("code", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id++, builder.build());
    }
}

package io.ride.memo.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import io.ride.memo.R;
import io.ride.memo.dao.GroupDao;
import io.ride.memo.dao.MemoDao;
import io.ride.memo.model.Memo;
import io.ride.memo.util.DateUtil;

/**
 * Created by ride on 17-12-15.
 * 编辑界面
 */

public class MemoActivity extends Activity {

    private String time;
    private Memo memo;

    private EditText contentText;
    private TextView warmTimeText;
    private MemoDao memoDao;
    private GroupDao groupDao;
    // code 0 插入 1 更新
    private int code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_activity);

        SwitchButton selectTimeButton = findViewById(R.id.warm);
        ImageButton backButton = findViewById(R.id.bt_back);
        ImageButton commitButton = findViewById(R.id.commit);
        warmTimeText = findViewById(R.id.warm_time);
        contentText = findViewById(R.id.memo_content);

        try {
            memoDao = new MemoDao(this);
            groupDao = new GroupDao(this);

            Intent intent = getIntent();
            int id = intent.getIntExtra("id", -1);
            code = intent.getIntExtra("code", 0);
            if (id == -1) {
                memo = new Memo();
                memo.setCraeteTime(new Date());
                memo.setWarm(false);
                int groupId = intent.getIntExtra("groupId", 1);
                memo.setGroupId(groupId);
            } else {
                memo = memoDao.queryById(id);
                contentText.setText(memo.getContent());
                selectTimeButton.setChecked(memo.isWarm());
                if (memo.isWarm()) {
                    warmTimeText.setText(DateUtil.formatTime(memo.getWarmTime()));
                    if (memo.getWarmTime().getTime() < new Date().getTime()) {
                        // 如果提醒时间已过期, 添加删除线
                        warmTimeText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                }
            }
            Log.i("ride-memo", "group id is " + intent.getIntExtra("groupId", 1));
            Log.i("ride-memo", "group id is " + memo.getGroupId());


            selectTimeButton.setOnCheckedChangeListener(selectTimeButtonListener);
            backButton.setOnClickListener(backButtonListener);
            commitButton.setOnClickListener(commitButtonListener);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * switch button listener
     */
    CompoundButton.OnCheckedChangeListener selectTimeButtonListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Toast.makeText(MemoActivity.this, isChecked + "", Toast.LENGTH_SHORT).show();
            memo.setWarm(isChecked);
            Log.i("ride-memo", "warm is " + isChecked);
            if (isChecked) {
                showDialogPick();
            } else {
                memo.setWarmTime(null);
                warmTimeText.setText("");
            }
        }
    };

    /**
     * 返回按钮(左上角)
     */
    View.OnClickListener backButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MemoActivity.this, "本次修改不保存", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    /**
     * 完成按钮
     */
    View.OnClickListener commitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MemoActivity.this, "click commit button ", Toast.LENGTH_SHORT).show();
            save();
            finish();
        }
    };


    /**
     * 时间选择器
     * 第一次显示日期选择器, 点击确定后显示时间选择器
     */
    private void showDialogPick() {
        //获取Calendar对象，用于获取当前时间
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //实例化TimePickerDialog对象
        final TimePickerDialog timePickerDialog = new TimePickerDialog(MemoActivity.this, new TimePickerDialog.OnTimeSetListener() {
            //选择完时间后会调用该回调函数
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //设置TextView显示最终选择的时间
                try {
                    time += " " + hourOfDay + ":" + minute;
                    Toast.makeText(MemoActivity.this, "click time is " + time, Toast.LENGTH_SHORT).show();
                    memo.setWarmTime(DateUtil.str2TDate(time));
                    Log.d("ride-memo-activity", "warm time is " + time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, hour, minute, true);
        //实例化DatePickerDialog对象
        DatePickerDialog datePickerDialog = new DatePickerDialog(MemoActivity.this, new DatePickerDialog.OnDateSetListener() {
            //选择完日期后会调用该回调函数
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                time = "";
                //因为monthOfYear会比实际月份少一月所以这边要加1
                time += year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                //选择完日期后弹出选择时间对话框
                timePickerDialog.show();
            }
        }, year, month, day);
        //弹出选择日期对话框
        datePickerDialog.show();
    }

    /**
     * 返回住activity自动保存
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            save();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 保存内容
     */
    private void save() {
        if (!(contentText.getText() == null || contentText.getText().toString() == ""
                || contentText.getText().toString().trim() == "")) {
            String content = String.valueOf(contentText.getText());
            memo.setContent(content);
            long result;
            if (code == 0) {
                result = memoDao.insert(memo);
            } else {
                result = memoDao.update(memo);
            }
            if (result == 0) {
                Log.i("ride-memo", "数据插入失败!");
            } else {
                Log.i("ride-memo", "数据插入成功!");
            }
        } else {
            Toast.makeText(this, "没有内容, 不保存!", Toast.LENGTH_SHORT).show();
            Log.i("ride-memo", "未填入数据");
        }
    }
}

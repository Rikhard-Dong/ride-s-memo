package io.ride.memo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

import io.ride.memo.model.Group;
import io.ride.memo.model.Memo;
import io.ride.memo.util.DateUtil;

/**
 * Created by ride on 17-12-15.
 * 数据库助手
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    static final String DB_NAME = "db_memo.db";  //  数据库名
    static final int VERSION = 1;                // 数据库版本

    // 创建group表
    private static final String CREATE_TABLE_GROUP = "create table " + Group.KEY_TABLE + "("
            + Group.KEY_ID + " integer primary key autoincrement, "
            + Group.KEY_NAME + " text not null"
            + ")";
    // 删除group表
    private static final String DROP_TABLE_GROUP = "drop table " + Group.KEY_TABLE;

    // 插入默认分组
    private static final String INSERT_DEFAULT_GROUP = "insert into  " + Group.KEY_TABLE
            + " values('1', 'default')";

    // 创建memo表
    private static final String CREATE_TABLE_MEMO = "create table " + Memo.KEY_TABLE + "("
            + Memo.KEY_ID + " integer primary key autoincrement, "
            + Memo.KEY_CONTENT + " text not null, "
            + Memo.KEY_IS_WARM + " text not null default 0, "
            + Memo.KEY_WARM_TIME + " text, "
            + Memo.KEY_CREATE_TIME + " text, "                  // 文本类型, sqlite3没有时间类型
            + Memo.KEY_GROUP_ID + " integer"                    // 所属分组id
            + ")";

    // 删除memo表
    private static final String DROP_TABLE_MEMO = "drop table " + Memo.KEY_TABLE;

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GROUP);
        db.execSQL(INSERT_DEFAULT_GROUP);
        db.execSQL(CREATE_TABLE_MEMO);

        String nowTimeStr = DateUtil.formatTime(new Date());

        ContentValues values = new ContentValues();
        values.put(Memo.KEY_CONTENT, "欢迎使用本程序\n您可以记录您遇到的事情以及需要记住的事情\n\nauthor:ride");
        values.put(Memo.KEY_CREATE_TIME, nowTimeStr);
        values.put(Memo.KEY_IS_WARM, false);
        values.put(Memo.KEY_GROUP_ID, 1);

        db.insert(Memo.KEY_TABLE, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_GROUP);
        db.execSQL(DROP_TABLE_MEMO);
        onCreate(db);
    }
}

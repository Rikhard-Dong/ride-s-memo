package io.ride.memo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.ride.memo.model.Memo;
import io.ride.memo.util.DateUtil;

/**
 * Created by ride on 17-12-15.
 * dao层, 操作memo表
 */

public class MemoDao {
    private SQLiteDatabase db;

    /**
     * 创建对象的同时并打开数据库
     *
     * @param context 上下文
     */
    public MemoDao(Context context) {
        DBOpenHelper helper = new DBOpenHelper(context, DBOpenHelper.DB_NAME, null, DBOpenHelper.VERSION);
        db = helper.getWritableDatabase();
    }

    /**
     * 关闭数据库
     */
    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    /**
     * 插入memo
     *
     * @param memo 插入对象
     * @return 数据库更新条数
     */
    public long insert(Memo memo) {
        ContentValues values = new ContentValues();
        String curTimeStr = DateUtil.formatTime(memo.getCreateTime());

        // 设置插入的阐述
        values.put(Memo.KEY_CONTENT, memo.getContent());
        values.put(Memo.KEY_CREATE_TIME, curTimeStr);
        values.put(Memo.KEY_IS_WARM, memo.isWarm() ? 1 : 0);
        if (memo.isWarm()) {
            String warmTimeStr = DateUtil.formatTime(memo.getWarmTime());
            values.put(Memo.KEY_WARM_TIME, warmTimeStr);
        }
        values.put(Memo.KEY_GROUP_ID, memo.getGroupId());

        return db.insert(Memo.KEY_TABLE, null, values);
    }

    /**
     * 根据ID更新数据
     *
     * @param memo 更新对象
     * @return 数据库更新条数
     */
    public long update(Memo memo) {
        ContentValues values = new ContentValues();
        String curTimeStr = DateUtil.formatTime(memo.getCreateTime());

        // 设置插入的阐述
        values.put(Memo.KEY_CONTENT, memo.getContent());
        values.put(Memo.KEY_CREATE_TIME, curTimeStr);
        values.put(Memo.KEY_IS_WARM, memo.isWarm() ? 1 : 0);
        if (memo.isWarm()) {
            String warmTimeStr = DateUtil.formatTime(memo.getWarmTime());
            values.put(Memo.KEY_WARM_TIME, warmTimeStr);
        }
        values.put(Memo.KEY_GROUP_ID, memo.getGroupId());
        return db.update(Memo.KEY_TABLE, values, Memo.KEY_ID + " = ?",
                new String[]{String.valueOf(memo.getId())});
    }

    /**
     * 根据Id删除memo
     *
     * @param id 删除ID
     * @return 数据库更新条数
     */
    public long deleteById(int id) {
        return db.delete(Memo.KEY_TABLE, Memo.KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * 根据分组ID删除
     *
     * @param groupId 删除该分组所有memo
     * @return 数据库更新条数
     */
    public long deleteByGroupId(int groupId) {
        return db.delete(Memo.KEY_TABLE, Memo.KEY_GROUP_ID + " = ?",
                new String[]{String.valueOf(groupId)});
    }

    /**
     * 查询所有
     *
     * @return 查询所有内容
     * @throws ParseException 日期转换异常
     */
    public List<Memo> queryAll() throws ParseException {
        Cursor cursor = db.query(Memo.KEY_TABLE, null, null, null,
                null, null, Memo.KEY_CREATE_TIME);
        return convert2Memo(cursor);
    }

    /**
     * 根据Id查询
     *
     * @param id 查询ID
     * @return 查询到的单个结果
     * @throws ParseException
     */
    public Memo queryById(int id) throws ParseException {
        Cursor cursor = db.query(Memo.KEY_TABLE, null, Memo.KEY_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        List<Memo> results = convert2Memo(cursor);
        if (results == null || results.size() == 0) {
            return null;
        }
        return results.get(0);
    }

    /**
     * 查询分组下所有memo
     *
     * @param groupId
     * @return
     * @throws ParseException
     */
    public List<Memo> queryByGroupId(int groupId) throws ParseException {
        Cursor cursor = db.query(Memo.KEY_TABLE, null, Memo.KEY_GROUP_ID + " = ?",
                new String[]{String.valueOf(groupId)}, null, null,
                Memo.KEY_CREATE_TIME + " desc");
        return convert2Memo(cursor);
    }

    /**
     * 得到所有的删除
     *
     * @return
     * @throws ParseException
     */
    public List<Memo> queryByWarm() throws ParseException {
        Cursor cursor = db.query(Memo.KEY_TABLE, null,
                Memo.KEY_GROUP_ID + " = ?",
                new String[]{"1"}, null, null,
                Memo.KEY_CREATE_TIME + " desc");
        List<Memo> memos = convert2Memo(cursor);
        if (memos == null || memos.size() == 0) {
            return null;
        }
        // 剔除已经过期的
        for (Memo memo : memos) {
            if (memo.getWarmTime().getTime() < new Date().getTime()) {
                memos.remove(memo);
            }
        }
        return memos;
    }

    /**
     * 得到最近要提醒的
     *
     * @return
     * @throws ParseException
     */
    public Memo queryByRecentWarmMemo() throws ParseException {
        String curDateStr = DateUtil.formatTime(new Date());
        String sql = "select * from " + Memo.KEY_TABLE + " where "
                + Memo.KEY_IS_WARM + " = '1' and "
                + Memo.KEY_WARM_TIME + " > '" + curDateStr + "' order by "
                + Memo.KEY_WARM_TIME + " asc";

        Cursor cursor = db.rawQuery(sql, null);
        List<Memo> memos = convert2Memo(cursor);
        return (memos == null || memos.size() == 0) ? null : memos.get(0);
    }

    /**
     * 将查询结构转换成需要的数据格式
     *
     * @param cursor 游标
     * @return 转换结果
     */
    private List<Memo> convert2Memo(Cursor cursor) throws ParseException {
        if (cursor.getCount() == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<Memo> memos = new ArrayList<>();
        do {
            int id = cursor.getInt(cursor.getColumnIndex(Memo.KEY_ID));
            int groupId = cursor.getInt(cursor.getColumnIndex(Memo.KEY_GROUP_ID));
            boolean isWarm = cursor.getInt(cursor.getColumnIndex(Memo.KEY_IS_WARM)) == 1;
            String content = cursor.getString(cursor.getColumnIndex(Memo.KEY_CONTENT));
            String createTimeStr = cursor.getString(cursor.getColumnIndex(Memo.KEY_CREATE_TIME));
            Date createTime = DateUtil.str2TDate(createTimeStr);
            String warmTimeStr;
            Date warmTime = null;
            if (isWarm) {
                warmTimeStr = cursor.getString(cursor.getColumnIndex(Memo.KEY_WARM_TIME));
                warmTime = DateUtil.str2TDate(warmTimeStr);
            }

            Memo memo = new Memo(id, content, createTime, isWarm, warmTime, groupId);
            memos.add(memo);
        } while (cursor.moveToNext());

        return memos;
    }


}

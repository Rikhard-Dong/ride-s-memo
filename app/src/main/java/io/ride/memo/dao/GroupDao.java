package io.ride.memo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.ride.memo.model.Group;
import io.ride.memo.util.DBOpenHelper;

/**
 * Created by ride on 17-12-15.
 * <p>
 * group dao层操作
 */

public class GroupDao {
    private Context context;
    private DBOpenHelper helper;
    private SQLiteDatabase db;

    private MemoDao memoDao;

    public GroupDao(Context context) {
        this.context = context;
        helper = new DBOpenHelper(context, DBOpenHelper.DB_NAME, null, DBOpenHelper.VERSION);
        db = helper.getWritableDatabase();
        memoDao = new MemoDao(context);
    }

    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    public long insert(Group group) {
        ContentValues values = new ContentValues();
        values.put(Group.KEY_NAME, group.getName());

        return db.insert(Group.KEY_TABLE, null, values);
    }

    public long delete(int id) {
        // TODO 这里需要做事务操作
        // 1 是默认分组, 不能删除
        if (id == 1) {
            return 0;
        }
        memoDao.deleteByGroup(id);
        return db.delete(Group.KEY_TABLE, Group.KEY_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public List<Group> queryAll() {
        Cursor cursor = db.query(Group.KEY_TABLE, null, null, null,
                null, null, Group.KEY_ID);

        return convert2Group(cursor);
    }

    public Group queryById(int id) {
        Cursor cursor = db.query(Group.KEY_TABLE, null, Group.KEY_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        List<Group> groups = convert2Group(cursor);
        if (groups == null || groups.size() == 0) {
            return null;
        }
        return groups.get(0);
    }

    public List<Group> convert2Group(Cursor cursor) {
        if (cursor.getCount() == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<Group> groups = new ArrayList<>();
        do {
            int id = cursor.getInt(cursor.getColumnIndex(Group.KEY_ID));
            String name = cursor.getString(cursor.getColumnIndex(Group.KEY_NAME));
            Group group = new Group(id, name);
            groups.add(group);
        } while (cursor.moveToNext());
        return groups;
    }
}

package io.ride.memo.model;

import java.util.Date;

/**
 * Created by ride on 17-12-15.
 * memo java bean
 */

public class Memo {
    // 定义一些常量, 操作数据库使用
    public static final String KEY_TABLE = "t_memo";                // 表名
    public static final String KEY_ID = "_id";                      // id
    public static final String KEY_CREATE_TIME = "crate_time";      // 创建时间
    public static final String KEY_CONTENT = "content";             // 备忘录内容
    public static final String KEY_IS_WARM = "is_warm";             // 是否设置提醒
    public static final String KEY_WARM_TIME = "warm_time";         // 提醒时间
    public static final String KEY_GROUP_ID = "group_id";           // 分组ID

    private int id;
    private String content;
    private Date createTime;
    private boolean isWarm;
    private Date warmTime;
    private int groupId;

    public Memo() {
    }

    public Memo(String content) {
        this.createTime = new Date();
        this.content = content;
    }

    public Memo(String content, boolean isWarm, Date warmTime) {
        this.content = content;
        this.isWarm = isWarm;
        this.warmTime = warmTime;
    }

    public Memo(int id, String content, Date createTime, boolean isWarm, Date warmTime, int groupId) {
        this.id = id;
        this.content = content;
        this.createTime = createTime;
        this.isWarm = isWarm;
        this.warmTime = warmTime;
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isWarm() {
        return isWarm;
    }

    public void setWarm(boolean warm) {
        isWarm = warm;
    }

    public Date getWarmTime() {
        return warmTime;
    }

    public void setWarmTime(Date warmTime) {
        this.warmTime = warmTime;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "Memo{" +
                "id=" + id +
                ", content=" + content +
                ", createTime=" + createTime +
                ", isWarm=" + isWarm +
                ", warmTime=" + warmTime +
                ", groupId=" + groupId +
                '}';
    }
}
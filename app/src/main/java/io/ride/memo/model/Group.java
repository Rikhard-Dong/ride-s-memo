package io.ride.memo.model;

/**
 * Created by ride on 17-12-15.
 * <p>
 * group java bean
 */

public class Group {

    // 操作数据库的常量
    public static final String KEY_TABLE = "t_group";      // 表名
    public static final String KEY_ID = "_id";             // id
    public static final String KEY_NAME = "name";          // 分组名

    private int id;

    private String name;

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public Group(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

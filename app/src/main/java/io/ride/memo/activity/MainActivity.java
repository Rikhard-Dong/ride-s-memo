package io.ride.memo.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.ride.memo.R;
import io.ride.memo.adapter.GroupGridViewAdapter;
import io.ride.memo.adapter.MemoGridViewAdapter;
import io.ride.memo.dao.GroupDao;
import io.ride.memo.dao.MemoDao;
import io.ride.memo.model.Group;
import io.ride.memo.model.Memo;
import io.ride.memo.service.LongRunningService;

public class MainActivity extends Activity {

    private GridView memoGridView;
    private GridView groupGridView;
    private MemoGridViewAdapter memoGridViewAdapter;
    private GroupGridViewAdapter groupGridViewAdapter;

    private List<Memo> memos;
    private ArrayList<Group> groups;

    private int groupId = 1;

    private long lastBackTime = 0;
    private long currentBackTime = 0;

    private GroupDao groupDao;
    private MemoDao memoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        groupDao = new GroupDao(this);
        memoDao = new MemoDao(this);

        addMenu();

        memoGridView = findViewById(R.id.gridview);
        memoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Memo memo = memos.get(position);
                Intent intent = new Intent(MainActivity.this, MemoActivity.class);
                intent.putExtra("id", memo.getId());
                intent.putExtra("code", 1);
                startActivity(intent);
            }
        });

        groupGridView = findViewById(R.id.group_view);
        groupGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = groups.get(position);
                groupId = group.getId();
                groupGridViewAdapter.setGridData(groups, groupId);

                try {
                    memos = memoDao.queryByGroupId(groupId);
                    memoGridViewAdapter.setMemos(memos);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        groupGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Group group = groups.get(position);
                String message = "确定要删除\"" + group.getName() + "\"分组么\n该分组下所有备忘录都将被删除!";
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("删除分组")
                        .setMessage(message)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                groupId = 1;
                                if (group.getId() == 1) {
                                    Toast.makeText(MainActivity.this, "默认分组不能被删除!",
                                            Toast.LENGTH_SHORT).show();
                                }
                                groupDao.delete(group.getId());
                                groups = (ArrayList<Group>) groupDao.queryAll();
                                groupGridViewAdapter = new GroupGridViewAdapter(MainActivity.this,
                                        R.layout.group_grid_item, groups, groupId);
                                groupGridView.setAdapter(groupGridViewAdapter);
                            }
                        })
                        .setNegativeButton("取消", null);
                builder.show();

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "从别的Activity返回", Toast.LENGTH_SHORT).show();
        try {
            memos = memoDao.queryByGroupId(groupId);
            memoGridViewAdapter = new MemoGridViewAdapter(this, memos);
            memoGridView.setAdapter(memoGridViewAdapter);

            groups = (ArrayList<Group>) groupDao.queryAll();
            Log.i("ride-memo", groups.toString());
            groupGridViewAdapter = new GroupGridViewAdapter(this, R.layout.group_grid_item, groups, groupId);
            groupGridView.setAdapter(groupGridViewAdapter);

            // 启动service
            Intent intent = new Intent(MainActivity.this, LongRunningService.class);
            startService(intent);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现再按一次退出程序
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            currentBackTime = System.currentTimeMillis();
            if (currentBackTime - lastBackTime > 2000) {
                Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                lastBackTime = currentBackTime;
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 添加卫星菜单
     */
    private void addMenu() {
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.add));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);

        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);

        // 添加备忘录
        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.add_memo));
        // 添加分组
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.group));


        // Build the menu with default options: light theme, 90 degrees, 72dp
        // radius.
        // Set 4 default SubActionButtons
        // FloatingActionMenu通过attachTo(rightLowerButton)附着到FloatingActionButton
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .attachTo(rightLowerButton)
                .build();

        // 设置打开和关闭的按钮旋转动画
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // 增加按钮中的+号图标顺时针旋转45度
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // 增加按钮中的+号图标逆时针旋转45度
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });

        // 监听新建memo
        rlIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ride-memo", "点击新建memo按钮");

                Intent intent = new Intent(MainActivity.this, MemoActivity.class);
                intent.putExtra("groupId", groupId);
                // code 0 插入 1 更新
                intent.putExtra("code", 0);
                startActivity(intent);

            }
        });

        // 监听新建分组
        rlIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ride-memo", "点击新建分组按钮");
                final EditText editText = new EditText(MainActivity.this);
                editText.setPadding(10, 2, 10, 2);


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("新建分组")
                        .setMessage("创建新的分组存放您的备忘录")
                        .setView(editText)
                        .setPositiveButton("确定", null)
                        .setNegativeButton("取消", null);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String inputStr = editText.getText().toString();
                                Log.i("ride-memo", "inputStr: " + inputStr);
                                if (!inputStr.trim().equals("")) {
                                    Toast.makeText(MainActivity.this, "true:" + inputStr, Toast.LENGTH_SHORT).show();
                                    Group group = new Group(inputStr);
                                    groupDao.insert(group);

                                    groups = (ArrayList<Group>) groupDao.queryAll();
                                    groupGridViewAdapter = new GroupGridViewAdapter(MainActivity.this,
                                            R.layout.group_grid_item, groups, groupId);
                                    groupGridView.setAdapter(groupGridViewAdapter);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(MainActivity.this, "请输入分组名", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}

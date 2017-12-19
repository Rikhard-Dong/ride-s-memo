package io.ride.memo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.Date;
import java.util.List;

import io.ride.memo.R;
import io.ride.memo.dao.MemoDao;
import io.ride.memo.model.Memo;
import io.ride.memo.util.DateUtil;

/**
 * Created by ride on 17-12-15.
 * adapter
 */

public class MemoGridViewAdapter extends BaseSwipeAdapter {
        private Context context;
        private List<Memo> memos;
        private MemoDao memoDao;

        public MemoGridViewAdapter(Context context, List<Memo> list) {
            this.context = context;
            memoDao = new MemoDao(this.context);
            // TODO 从数据库获取数据
            this.memos = list;
        }

    public void setMemos(List<Memo> memos) {
        this.memos = memos;
        notifyDataSetChanged();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.memo_grid_item, null);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 480);
        view.setLayoutParams(params);
        return view;
    }

    @Override
    public void fillValues(final int position, View convertView) {
        final Memo memo = memos.get(position);
        TextView createTimeText = convertView.findViewById(R.id.create_date);
        TextView contentText = convertView.findViewById(R.id.grid_content);
        ImageView clockImage = convertView.findViewById(R.id.clock);
        contentText.setText(memo.getContent());
        createTimeText.setText(DateUtil.formatTime(memo.getCreateTime()));
        if (memo.isWarm()) {
            if (memo.getWarmTime().getTime() < new Date().getTime()) {
                clockImage.setImageResource(R.drawable.clock_1);
            } else {
                clockImage.setImageResource(R.drawable.clock_2);
            }
            clockImage.setVisibility(View.VISIBLE);
        } else {
            clockImage.setVisibility(View.INVISIBLE);
        }

        // 设置隐藏ID
        TextView memoIdText = convertView.findViewById(R.id.memo_id);
        memoIdText.setText(String.valueOf(memo.getId()));

        // 删除按钮
        ImageView button = convertView.findViewById(R.id.memo_delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoDao.deleteById(memo.getId());
                memos.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        return memos == null ? 0 : memos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

package io.ride.memo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.ride.memo.R;
import io.ride.memo.model.Group;

/**
 * Created by ride on 17-12-16.
 * group adapter
 */

public class GroupGridViewAdapter extends ArrayAdapter<Group> {
    private List<Group> groups;
    private int layoutInflaterId;
    private Context context;
    private int curGroupId;

    public GroupGridViewAdapter(@NonNull Context context, int resource, ArrayList<Group> groups, int curGroupId) {
        super(context, resource, groups);
        //        this.groups = groups;
        Log.i("ride-memo", " groups is " + groups);
        this.groups = groups;
        layoutInflaterId = resource;
        this.context = context;
        this.curGroupId = curGroupId;
    }

    public void setGridData(ArrayList<Group> groups, int groupId) {
        this.groups = groups;
        this.curGroupId = groupId;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            convertView = layoutInflater.inflate(layoutInflaterId, parent, false);
            viewHolder.groupIdText = convertView.findViewById(R.id.group_id);
            viewHolder.groupNameText = convertView.findViewById(R.id.group_name);
            viewHolder.layout = convertView.findViewById(R.id.group_layout);
            viewHolder.currentUseImage = convertView.findViewById(R.id.current_group);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 420);
        convertView.setLayoutParams(params);
        Group group = groups.get(position);
        Log.i("ride-memo", "group is --->" + group);

        viewHolder.groupIdText.setText(String.valueOf(group.getId()));
        if (curGroupId == group.getId()) {
            viewHolder.layout.setBackgroundResource(R.drawable.shape2);
            viewHolder.currentUseImage.setVisibility(View.VISIBLE);
        } else {
            viewHolder.layout.setBackgroundResource(R.drawable.shape);
            viewHolder.currentUseImage.setVisibility(View.INVISIBLE);
        }

        viewHolder.groupNameText.setText(group.getName());
        return convertView;
    }

    class ViewHolder {
        RelativeLayout layout;
        ImageView currentUseImage;
        TextView groupIdText;
        TextView groupNameText;
    }
}

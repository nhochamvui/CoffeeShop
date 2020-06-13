package com.example.coffeeshop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomUserListAdapter extends BaseAdapter {
    private List<User> userList;
    private LayoutInflater layoutInflater;
    private Context context;

    public CustomUserListAdapter(List<User> userList,Context context) {
        this.userList = userList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            Log.e("convertView = null", "we are here");
            convertView = layoutInflater.inflate(R.layout.user_container_layout, null);
            holder = new ViewHolder();
            holder.textViewID = (TextView) convertView.findViewById(R.id.textViewID);
            holder.textViewUserName = (TextView) convertView.findViewById(R.id.textViewDrinkName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.e("getView", "we are here");
        User user = this.userList.get(position);
        holder.textViewID.setText("Index: "+position);
        holder.textViewUserName.setText(user.getUsername());
        return convertView;
    }
    static class ViewHolder {
        TextView textViewUserName;
        TextView textViewID;
    }
    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    public void setLayoutInflater(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

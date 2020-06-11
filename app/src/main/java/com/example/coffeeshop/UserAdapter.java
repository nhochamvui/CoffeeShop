package com.example.coffeeshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<User> userArrayList;
    OnClickListener onClickListener;

    public UserAdapter(Context mContext, ArrayList<User> userArrayList, OnClickListener onClickListener) {
        this.mContext = mContext;
        this.userArrayList = userArrayList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View userView = inflater.inflate(R.layout.user_container_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(userView, onClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        User user = userArrayList.get(position);
        holder.textViewUserName.setText(user.getUsername());
        holder.textViewID.setText(user.getId());
        Glide.with(holder.imageViewAvatar.getContext())
                .load("https://i.stack.imgur.com/GShqJ.jpg?s=32")
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(holder.imageViewAvatar);
    }
    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        private TextView textViewID;
        private TextView textViewUserName;
        private ImageView imageViewAvatar;
        OnClickListener onClickListener;
        public ViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            textViewID = itemView.findViewById(R.id.textViewID);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            this.onClickListener = onClickListener;
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onClickListener.OnItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
    public interface OnClickListener{
        void OnItemClick(int position);
    }
    public void addAll(ArrayList<User> arrayList)
    {
        this.userArrayList = arrayList;
    }
}

package com.example.coffeeshop;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<User> userArrayList;
    OnClickListener onClickListener;
    private Activity act;
    public UserAdapter(Context mContext, ArrayList<User> userArrayList, OnClickListener onClickListener, Activity act) {
        this.mContext = mContext;
        this.userArrayList = userArrayList;
        this.onClickListener = onClickListener;
        this.act = act;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View userView = inflater.inflate(R.layout.user_container_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(userView, onClickListener);
        return viewHolder;
    }
    // gan chuc nang cho component
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.textViewAuthor.setText("");
        User user = userArrayList.get(position);
        holder.textViewUserName.setText(user.getUsername());
        holder.textViewRole.setText(user.getRole());
        if (user.getAdd() == true)
            holder.textViewAuthor.append("Add\n");
        if (user.getModify() == true)
            holder.textViewAuthor.append("Modify\n");
        if (user.getRemove() == true)
            holder.textViewAuthor.append("Remove\n");
        holder.textViewAuthor.append("View");
//        holder.textViewAdd.append(user.getAdd().toString());
//        holder.textViewModify.append(user.getModify().toString());
//        holder.textViewRemove.append(user.getRemove().toString());
        Glide.with(holder.imageViewAvatar.getContext())
                .load(user.getAvatar())
//                .circleCrop()
                .error(R.drawable.ic_round_broken_image_24)
                .placeholder(R.drawable.ic_baseline_image_24)
//                .transform(new RoundedCorners(10))
                .into(holder.imageViewAvatar);
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }
    public User getItem(int position)
    {
        return userArrayList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        private TextView textViewUserName;
        private ImageView imageViewAvatar;
        private CardView imageViewUpdate;
        private CardView imageViewDelete;
        private TextView textViewRole;
        private TextView textViewAuthor;
//        private TextView textViewRemove;
//        private TextView textViewModify;

        OnClickListener onClickListener;
        // khai bao cac component
        public ViewHolder(@NonNull View itemView, final OnClickListener onClickListener) {
            super(itemView);
//            textViewID = itemView.findViewById(R.id.textViewID);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            imageViewUpdate = itemView.findViewById(R.id.imageViewSettingUser);
            imageViewDelete = itemView.findViewById(R.id.imageViewDeleteUser);
            textViewRole = itemView.findViewById(R.id.textViewRole);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
//            textViewRemove = itemView.findViewById(R.id.textViewRemove);
//            textViewModify = itemView.findViewById(R.id.textViewModify);
            imageViewUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.OnSettingClick(getAdapterPosition());
                }
            });
            imageViewDelete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    onClickListener.OnDeleteClick(getAdapterPosition());
                }
            });
            this.onClickListener = onClickListener;
            itemView.setOnClickListener(this);
//            imageViewDelete.setOnClickListener(this);
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
        void OnSettingClick(int position);
        void OnDeleteClick(int position);
    }
    public void addAll(ArrayList<User> arrayList)
    {
        this.userArrayList = arrayList;
    }
}

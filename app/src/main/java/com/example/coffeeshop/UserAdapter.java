package com.example.coffeeshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import org.w3c.dom.Text;

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
    // gan chuc nang cho component
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        User user = userArrayList.get(position);
        holder.textViewUserName.setText(user.getUsername());
//        holder.textViewID.setText(" Index: "+position);
        holder.textViewRole.setText(user.getRole());
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
//        private TextView textViewID;
        private TextView textViewUserName;
        private ImageView imageViewAvatar;
        private CardView imageViewUpdate;
        private CardView imageViewDelete;
        private TextView textViewRole;
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

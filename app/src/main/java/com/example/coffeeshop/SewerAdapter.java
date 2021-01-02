package com.example.coffeeshop;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SewerAdapter extends RecyclerView.Adapter<SewerAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<Sewer> sewerArrayList;
    SewerOnClickListener onClickListener;

    public SewerAdapter(Context mContext, ArrayList<Sewer> sewerArrayList, SewerOnClickListener onClickListener) {
        this.mContext = mContext;
        this.sewerArrayList = sewerArrayList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public SewerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View userView = inflater.inflate(R.layout.sewer_container_layout, parent, false);
        SewerAdapter.ViewHolder viewHolder = new SewerAdapter.ViewHolder(userView, onClickListener);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final SewerAdapter.ViewHolder holder, final int position) {
        final Sewer sewer = sewerArrayList.get(position);
        holder.textViewSewerName.setText(sewer.getName());
        holder.textViewSewerCategory.setText("Loại: " +sewer.getCategory());
        holder.textViewSewerLocation.setText("Địa điểm: " +sewer.getLocation());
    }

    @Override
    public int getItemCount() {
        return sewerArrayList.size();
    }
    public Sewer getItem(int position)
    {
        return sewerArrayList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView textViewSewerName, textViewSewerLocation, textViewSewerCategory;
        private ImageView imageViewSetting;
        private CardView cardViewSewer;
        private ConstraintLayout constraintLayoutSewerContainer;
        //        private ImageView imageViewDelete;
        SewerAdapter.SewerOnClickListener onClickListener;
        public ViewHolder(@NonNull View itemView, final SewerAdapter.SewerOnClickListener onClickListener) {
            super(itemView);
            textViewSewerName = itemView.findViewById(R.id.textViewSewerName);
            textViewSewerLocation = itemView.findViewById(R.id.textViewSewerLocation);
            imageViewSetting = itemView.findViewById(R.id.imageViewSettingSewer);
            textViewSewerCategory = itemView.findViewById(R.id.textViewSewerCategory);
            cardViewSewer = itemView.findViewById(R.id.cardViewSewer);
            constraintLayoutSewerContainer = itemView.findViewById(R.id.constraintLayoutSewerContainer);
            imageViewSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.OnSettingClick(getAdapterPosition());
                }
            });
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
    public interface SewerOnClickListener{
        void OnItemClick(int position);
        void OnSettingClick(int position);
        void OnDeleteClick(int position);
    }
    public void addAll(ArrayList<Sewer> arrayList)
    {
        this.sewerArrayList = arrayList;
    }
}

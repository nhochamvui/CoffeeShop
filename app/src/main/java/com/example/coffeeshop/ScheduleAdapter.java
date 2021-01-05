package com.example.coffeeshop;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<Schedule> scheduleArrayList;
    ScheduleOnClickListener onClickListener;

    public ScheduleAdapter(Context mContext, ArrayList<Schedule> scheduleArrayList, ScheduleOnClickListener onClickListener) {
        this.mContext = mContext;
        this.scheduleArrayList = scheduleArrayList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View userView = inflater.inflate(R.layout.schedule_container_layout, parent, false);
        ScheduleAdapter.ViewHolder viewHolder = new ScheduleAdapter.ViewHolder(userView, onClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ScheduleAdapter.ViewHolder holder, final int position) {
        final Schedule schedule = scheduleArrayList.get(position);
        holder.textViewScheduleSewerName.setText(schedule.getSewer().getName());
        holder.textViewScheduleSewerLocation.setText("Location: " +schedule.getSewer().getLocation().get("district")+", "+schedule.getSewer().getLocation().get("city"));
        holder.textViewScheduleAction.setText("Action: " +schedule.getAction());
        holder.editTextScheduleTime.setText(schedule.getTime() + " " + schedule.getDate());
    }

    @Override
    public int getItemCount() {
        return scheduleArrayList.size();
    }
    public Schedule getItem(int position)
    {
        return scheduleArrayList.get(position);
    }
    public void setItems(ArrayList<Schedule> schedules){
        this.scheduleArrayList = schedules;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView textViewScheduleSewerName, textViewScheduleSewerLocation, textViewScheduleAction;
        private TextInputEditText editTextScheduleTime;
        private ImageView imageViewSettingSchedule;
        ScheduleAdapter.ScheduleOnClickListener onClickListener;
        public ViewHolder(@NonNull View itemView, final ScheduleAdapter.ScheduleOnClickListener onClickListener) {
            super(itemView);
            textViewScheduleSewerName = itemView.findViewById(R.id.textViewScheduleSewerName);
            textViewScheduleSewerLocation = itemView.findViewById(R.id.textViewScheduleSewerLocation);
            textViewScheduleAction = itemView.findViewById(R.id.textViewScheduleAction);
            editTextScheduleTime = itemView.findViewById(R.id.editTextScheduleTime);
            imageViewSettingSchedule = itemView.findViewById(R.id.imageViewSettingSchedule);
            imageViewSettingSchedule.setOnClickListener(new View.OnClickListener() {
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
    public interface ScheduleOnClickListener{
        void OnItemClick(int position);
        void OnSettingClick(int position);
        void OnDeleteClick(int position);
    }
}

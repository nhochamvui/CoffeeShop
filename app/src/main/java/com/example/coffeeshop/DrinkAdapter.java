package com.example.coffeeshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<Drink> drinkArrayList;
    DrinkOnClickListener onClickListener;

    public DrinkAdapter(Context mContext, ArrayList<Drink> drinkArrayList, DrinkOnClickListener onClickListener) {
        this.mContext = mContext;
        this.drinkArrayList = drinkArrayList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public DrinkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View userView = inflater.inflate(R.layout.drink_container_layout, parent, false);
        DrinkAdapter.ViewHolder viewHolder = new DrinkAdapter.ViewHolder(userView, onClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkAdapter.ViewHolder holder, final int position) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);

        Drink drink = drinkArrayList.get(position);
        holder.textViewDrinkName.setText(drink.getName());
        holder.textViewDrinkPrice.setText(numberFormat.format(Integer.parseInt(drink.getPrice())));
        Glide.with(holder.imageViewDrinkImg.getContext())
                .load(drink.getImg())
                .centerCrop()
                .error(R.drawable.ic_round_broken_image_24)
                .placeholder(R.drawable.ic_baseline_image_24)
                .transform(new RoundedCorners(10))
                .into(holder.imageViewDrinkImg);
    }

    @Override
    public int getItemCount() {
        return drinkArrayList.size();
    }
    public Drink getItem(int position)
    {
        return drinkArrayList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView textViewDrinkName;
        private TextView textViewDrinkPrice;
        private ImageView imageViewDrinkImg;
        private ImageView imageViewSetting;
        private ImageView imageViewDelete;
        DrinkAdapter.DrinkOnClickListener onClickListener;
        public ViewHolder(@NonNull View itemView, final DrinkAdapter.DrinkOnClickListener onClickListener) {
            super(itemView);
            textViewDrinkName = itemView.findViewById(R.id.textViewDrinkName);
            textViewDrinkPrice = itemView.findViewById(R.id.textViewDrinkPrice);
            imageViewDrinkImg = itemView.findViewById(R.id.imageViewDrinkImg);
            imageViewSetting = itemView.findViewById(R.id.imageViewSettingDrink);
            imageViewDelete = itemView.findViewById(R.id.imageViewDeleteDrink);
            imageViewSetting.setOnClickListener(new View.OnClickListener() {
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
    public interface DrinkOnClickListener{
        void OnItemClick(int position);
        void OnSettingClick(int position);
        void OnDeleteClick(int position);
    }
    public void addAll(ArrayList<Drink> arrayList)
    {
        this.drinkArrayList = arrayList;
    }
}

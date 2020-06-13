package com.example.coffeeshop;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DrinkManagementFragment extends Fragment implements DrinkAdapter.DrinkOnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseReference mDatabase;
    private ValueEventListener valueEventListenerFetchUser;
    private ArrayList<Drink> drinkArrayList;
    private DrinkAdapter drinkAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButtonAddDrink;

    public DrinkManagementFragment() {
        // Required empty public constructor
    }
    public static DrinkManagementFragment newInstance(String param1, String param2) {
        DrinkManagementFragment fragment = new DrinkManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_drink_management, container, false);
        initialComponent(v);

        //set up layout for RecyclerView
        drinkAdapter= new DrinkAdapter(DrinkManagementFragment.this.getContext(), drinkArrayList, DrinkManagementFragment.this);
        recyclerView.setLayoutManager(new GridLayoutManager(DrinkManagementFragment.this.getContext(),2));
        recyclerView.setAdapter(drinkAdapter);
        return v;
    }
    public void initialComponent(View v)
    {
        recyclerView = v.findViewById(R.id.recyclerViewDrinkManagement);
        drinkArrayList = new ArrayList<Drink>();
        floatingActionButtonAddDrink = v.findViewById(R.id.floatingActionButtonAddDrink);
        floatingActionButtonAddDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_drink_add);
                dialog.show();
            }
        });
    }
    private void fetchDataIntoRecyclerView() {
         mDatabase = FirebaseDatabase.getInstance().getReference().child("items");
         valueEventListenerFetchUser = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("on changing drink","detected changed!");
                drinkArrayList.clear();
                Drink drink = new Drink("", "", "","","","");
                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    if(id.child("category").getValue().equals("Drink")) {
                        drink = id.getValue(Drink.class);
                        drinkArrayList.add(drink);
                    }
                }
                drinkAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchDataIntoRecyclerView();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatabase.removeEventListener(valueEventListenerFetchUser);
    }

    @Override
    public void OnItemClick(int position) {

    }

    @Override
    public void OnSettingClick(int position) {

    }

    @Override
    public void OnDeleteClick(int position) {

    }
}
package com.example.coffeeshop;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserManagementFragment extends Fragment implements UserAdapter.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<User> userList;
    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    public UserManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserManagementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserManagementFragment newInstance(String param1, String param2) {
        UserManagementFragment fragment = new UserManagementFragment();
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
        View v = inflater.inflate(R.layout.fragment_user_management, container, false);
        initialComponent(v);

        //set up layout for RecyclerView
        userAdapter = new UserAdapter(UserManagementFragment.this.getContext(), userList, UserManagementFragment.this);
        recyclerView.setLayoutManager(new GridLayoutManager(UserManagementFragment.this.getContext(),2));
        recyclerView.setAdapter(userAdapter);

        fetchDataIntoRecyclerView();

        return v;
    }

    private void fetchDataIntoRecyclerView() {
        // the RecyclerView is auto update when the data is changed, we just need to update the data
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = new User("", "", "","");
                Log.e("check login","username "+ " hash pwd: ");

                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    user = id.getValue(User.class);
                    userList.add(user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void initialComponent(View v)
    {
        recyclerView = v.findViewById(R.id.recyclerViewUserManagement);
        userList = new ArrayList<User>();
    }

    @Override
    public void OnItemClick(int position) {
        Toast.makeText(this.getContext(), ""+userList.get(position).getUsername(), Toast.LENGTH_LONG).show();
    }
}
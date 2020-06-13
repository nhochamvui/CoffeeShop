package com.example.coffeeshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity implements UserAdapter.OnClickListener {
    ArrayList<User> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        User user = new User("tho","1234","admin");
        User user1 = new User("tai","1234","user");
        User user2 = new User("thao","1234","admin");
        userList = new ArrayList<User>();
        userList.add(user);
        userList.add(user1);
        userList.add(user2);
        UserAdapter userAdapter = new UserAdapter(this, userList, this);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
//        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen....);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
//        recyclerView.addItemDecoration(new SpacesItemDecoration(2, spacingInPixels, true, 0));
    }

    @Override
    public void OnItemClick(int position) {
        Toast.makeText(this, ""+userList.get(position).getUsername(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnSettingClick(int position) {

    }

    @Override
    public void OnDeleteClick(int position) {

    }


}
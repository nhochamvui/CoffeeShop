package com.example.coffeeshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity{
    private DatabaseReference mDatabase;
    private Intent intent;
    private boolean isAdmin = false;
    private Menu menu;
    private User user;
    private BottomNavigationView bottomNavigationView;
    private ImageView ivAvatar;
    private TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialComponent();
        doAuth();
        tvUsername.setText(user.getDisplayname());
        Glide.with(this)
                .load(user.getAvatar())
                .circleCrop()
                .into(ivAvatar);
        createFragments(savedInstanceState);

        /* //for testing methods
        User user = new User("thao", "1234", "admin");
        User userTest = new User("tho", "1234", "admin", "1");
        Item item = new Item("Bánh Phồng Tôm","Cùng với hương vị BBQ tôm hùm nướng, buổi tiệc trà của bạn sẽ thêm đậm hương vị","15000","hinhanh","food");
        Item itemTest = new Item("Bánh Phồng Cua","Cùng với hương vị BBQ tôm hùm nướng, buổi tiệc trà của bạn sẽ thêm đậm hương vị","30000","cua","food", "0");
        */
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        this.menu = menu;
        Log.e("onCreateOptionsMenu", "working");
        return true;
    }
    private void initialComponent(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        intent = getIntent();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        tvUsername = findViewById(R.id.tvUsername);
        ivAvatar = findViewById(R.id.ivAvatar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return true;
            }
        });
    }
    private void doAuth()
    {
        user = (User)intent.getSerializableExtra("user");
        if(user.getRole().equalsIgnoreCase("Admin")){

            isAdmin = true;
        }

        else{
            bottomNavigationView.getMenu().removeItem(R.id.menuAdminPanel);
            isAdmin = false;
        }
    }
    private void createFragments(Bundle savedInstanceState)
    {
        // Set default fragment for specific role
        if(savedInstanceState == null)
        {
            if(isAdmin)
            {
                bottomNavigationView.setSelectedItemId(R.id.menuAdminPanel);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AdminPanelFragment()).commit();
            }
            else{
                bottomNavigationView.setSelectedItemId(R.id.menuStore);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new StoreFragment()).commit();
            }
        }
        // Listen to fragment selection
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                switch (menuItem.getItemId())
                {
                    case R.id.menuAdminPanel:
                        fragment = new AdminPanelFragment();
                        break;
                    case R.id.menuStore:
                        fragment = new StoreFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                return true;
            }
        });
    }
    private String hash(String s)
    {
        StringBuilder pwdHex = new StringBuilder();
        try
        {
            MessageDigest messageDigest = java.security.MessageDigest.getInstance("MD5");
            messageDigest.update(s.getBytes());
            byte[] messageDigests = messageDigest.digest();
            for(byte tempMess : messageDigests)
            {
                String h = Integer.toHexString(0xFF & tempMess);
                while(h.length()<2)
                {
                    h = "0" +h;
                }
                pwdHex.append(h);
            }
            return pwdHex.toString();
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    private void createNewUser(final User user) {
        try {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            Query query = mDatabase.orderByKey().limitToLast(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int lastID = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren())
                        lastID = Integer.parseInt(data.getKey()) + 1;
                    mDatabase.child("" + lastID).child("username").setValue(user.getUsername());
                    mDatabase.child("" + lastID).child("pwd").setValue(hash(user.getPassword()));
                    mDatabase.child("" + lastID).child("role").setValue(user.getRole());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception err) {
            Log.d("create user", "" + err);
        }
    }
    private void updateUser(final User user, final MyCallBack myCallBack) {
        try {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isSuccess = false;
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        Log.e("UPDATE inner class", "liet ke ID: " + id.child("id").getValue() + " user id = ");
                        if (id.child("username").equals(user.getUsername()))
                        {
                            Log.e("UPDATE inner class", "found ID: " + id.getKey());
                            mDatabase.child(id.getKey()).child("username").setValue(user.getUsername());
                            mDatabase.child(id.getKey()).child("pwd").setValue(hash(user.getPassword()));
                            mDatabase.child(id.getKey()).child("role").setValue(user.getRole());


                            isSuccess = true;
                            break;
                        }
                    }
                    myCallBack.isSuccess(isSuccess);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception err) {
            Log.d("update user", "" + err);
        }
    }
    private void deleteUser(final User user, final MyCallBack myCallBack) {
        try {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isSuccess = false;
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        if (id.child("username").getValue().equals(user.getUsername()))
                        {
                            Log.e("DELETE inner class", "found ID: " + id.getKey());
                            mDatabase.child(id.getKey()).setValue(null);
                            isSuccess = true;
                            break;
                        }
                    }
                    myCallBack.isSuccess(isSuccess);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception err) {
            Log.d("update user", "" + err);
        }
    }
    //Kiểm tra mật khẩu và trả về đối tượng user
    private void retrieveUser(final User user, final MyCallBack myCallBack) {
        String pwd = new String("");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = false;
                User user1 = new User("", "", "", "","",false,false,false);
                String pwd = hash(user.getPassword());
                Log.e("RETRIEVE USER", "HASH: " +pwd);
                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    if (id.child("username").getValue().equals(user.getUsername()))
                    {
                        user1 = id.getValue(User.class);
                        Log.e("RETRIEVE USER", "inner class: " +user1.getUsername());
                        break;
                    }
                }
                myCallBack.onCallBack(user1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void createNewItem(final Item item) {
        try {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("items");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int lastID = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren())
                        lastID = Integer.parseInt(data.getKey()) + 1;
                    mDatabase.child("" + lastID).child("name").setValue(item.getName());
                    mDatabase.child("" + lastID).child("desc").setValue(item.getDesc());
                    mDatabase.child("" + lastID).child("price").setValue(item.getPrice());
                    mDatabase.child("" + lastID).child("img").setValue(item.getImg());
                    mDatabase.child("" + lastID).child("category").setValue(item.getCategory());
                    mDatabase.child("" + lastID).child("id").setValue("" + lastID);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception err) {
            Log.d("create user", "" + err);
        }
    }
    private void updateItem(final Item item, final MyCallBack myCallBack) {
        try {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("items");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isSuccess = false;
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        if(!item.getId().equals(""))
                        if (id.child("id").getValue().equals(item.getId()))
                        {
                            Log.e("UPDATE inner class", "found ID: " + id.getKey());
                            mDatabase.child(id.getKey()).child("name").setValue(item.getName());
                            mDatabase.child(id.getKey()).child("desc").setValue(item.getDesc());
                            mDatabase.child(id.getKey()).child("img").setValue(item.getImg());
                            mDatabase.child(id.getKey()).child("price").setValue(item.getPrice());
                            mDatabase.child(id.getKey()).child("category").setValue(item.getCategory());
                            mDatabase.child(id.getKey()).child("id").setValue(item.getId());
                            Log.e("UPDATE item", "inner class: " +item.getName());
                            isSuccess = true;
                            break;
                        }
                    }
                    myCallBack.isSuccess(isSuccess);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception err) {
            Log.d("update user", "" + err);
        }
    }
    private void deleteItem(final Item item, final MyCallBack myCallBack) {
        try {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("items");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isSuccess = false;
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        if(!item.getId().equals(""))
                        if (id.child("id").getValue().equals(item.getId()))
                        {
                            Log.e("DELETE inner class", "found ID: " + id.getKey());
                            mDatabase.child(id.getKey()).setValue(null);
                            isSuccess = true;
                            break;
                        }
                    }
                    myCallBack.isSuccess(isSuccess);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception err) {
            Log.d("update user", "" + err);
        }
    }
}

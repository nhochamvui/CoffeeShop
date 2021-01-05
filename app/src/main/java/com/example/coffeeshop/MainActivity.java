package com.example.coffeeshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private DatabaseReference mDatabase;
    private Intent intent;
    private boolean isAdmin = false;
    private Menu menu;
    private BottomNavigationView bottomNavigationView;
    private ImageView imageView7;
    static User2 user;
    private TabLayout tabLayoutAdminPanel;
    private ViewPager viewPagerAdminPanel;
    private TextView textViewDisplayName;
    private Button logoutButton;
    public static AuthorizeService authorizeService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialComponent();
        doAuth();
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
        intent = getIntent();
        user = (User2)intent.getSerializableExtra("User");
        this.authorizeService = new AuthorizeService("linh");
        textViewDisplayName = findViewById(R.id.textViewDisplayName);
        textViewDisplayName.setText("Hello "+user.getName()+",");
        viewPagerAdminPanel = findViewById(R.id.viewPagerAdmin2);
        tabLayoutAdminPanel = findViewById(R.id.tabLayoutAdminPanel);
        logoutButton = findViewById(R.id.logoutButton);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(new SewerManagementFragment(), "Sewer");
        viewPagerAdapter.addFragment(new UserManagementFragment(), "User");
        viewPagerAdapter.addFragment(new MsgBoardFragment(), "Message Board");
        viewPagerAdapter.addFragment(new ScheduleManagementFragment(), "Schedule");
        viewPagerAdminPanel.setOffscreenPageLimit(1);
        viewPagerAdminPanel.setAdapter(viewPagerAdapter);
        tabLayoutAdminPanel.setupWithViewPager(viewPagerAdminPanel);

        imageView7 = findViewById(R.id.imageView7);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return true;
            }
        });
        /*Glide.with(imageView7.getContext())
                .load(user.getAvatar())
                .circleCrop()
                .error(R.drawable.ic_round_broken_image_24)
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(imageView7);*/
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDiag();
            }
        });

    }
    private void doAuth()
    {
        if(user.getRole().equalsIgnoreCase("Admin")){
            isAdmin = true;
        }
        else{
            bottomNavigationView.getMenu().removeItem(R.id.menuAdminPanel);
            isAdmin = false;
        }
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragments_title = new ArrayList<>();
        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            Log.e("ViewPagerAdapter","working");
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            Log.e("getItem","working "+position);
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        private void addFragment(Fragment fragment, String title) {
            Log.e("add Fragment","working");
            fragments.add(fragment);
            fragments_title.add(title);
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            Log.e("getPageTitle","working "+fragments_title.get(position));
            return fragments_title.get(position);
        }
    }
    @Override
    public void onBackPressed() {
// TODO Auto-generated method stub
        logoutDiag();
    }
    public void logoutDiag() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        // builder.setCancelable(false);
        builder.setTitle("Logging out...");
        builder.setMessage("Do you want to Logout?");
        builder.setPositiveButton("yes",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("justLoggedOut", 1);
                finish();
                MainActivity.this.startActivity(intent);
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();

            }
        });
        AlertDialog alert=builder.create();
        alert.show();
        //super.onBackPressed();
    }
    public void reloadName() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("chat room", Context.MODE_PRIVATE);
//        tvUsername.setText(sharedPreferences.getString("display_name","Anonymous"));
    }
}

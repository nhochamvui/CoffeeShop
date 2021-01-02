package com.example.coffeeshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private DatabaseReference mDatabase;
    private Intent intent;
    private boolean isAdmin = false;
    private Menu menu;
    private BottomNavigationView bottomNavigationView;
    private ImageView imageView7;
    static User user = new User();
    private TabLayout tabLayoutAdminPanel;
    private ViewPager viewPagerAdminPanel;
    private TextView textViewDisplayName;
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
        user = (User)intent.getSerializableExtra("user");
        this.authorizeService = new AuthorizeService(user.getUsername());
        textViewDisplayName = findViewById(R.id.textViewDisplayName);
        textViewDisplayName.setText("Hello "+user.getDisplayname()+",");
        viewPagerAdminPanel = findViewById(R.id.viewPagerAdmin2);
        tabLayoutAdminPanel = findViewById(R.id.tabLayoutAdminPanel);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(new SewerManagementFragment(), "Sewer");
        viewPagerAdapter.addFragment(new UserManagementFragment(), "User");
        viewPagerAdapter.addFragment(new MsgBoardFragment(), "Message Board");
        viewPagerAdapter.addFragment(new SewerScheduling(), "Schedule");
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
        Glide.with(imageView7.getContext())
                .load(user.getAvatar())
                .circleCrop()
                .error(R.drawable.ic_round_broken_image_24)
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(imageView7);
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
}

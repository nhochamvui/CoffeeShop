package com.example.coffeeshop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminPanelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminPanelFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TabLayout tabLayoutAdminPanel;
    private ViewPager viewPagerAdminPanel;
    private TabItem tabItemUserManagement, tabItemDrink, tabItemFood;
    private MyPagerAdapter pagerAdapter;
    private Menu menu;
    private BottomNavigationView bottomNavigationView;

    public AdminPanelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminPanelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminPanelFragment newInstance(String param1, String param2) {
        AdminPanelFragment fragment = new AdminPanelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate","we are here");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_panel, container, false);
        Log.e("onCreateView","we are here");
        viewPagerAdminPanel = view.findViewById(R.id.viewPagerAdmin);
        tabLayoutAdminPanel = view.findViewById(R.id.tabLayoutAdminPanel);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);
        viewPagerAdapter.addFragment(new UserManagementFragment(), "User");
        viewPagerAdapter.addFragment(new DrinkManagementFragment(), "Drink");
        viewPagerAdapter.addFragment(new FoodManagementFragment(), "Food");

        viewPagerAdminPanel.setAdapter(viewPagerAdapter);
        tabLayoutAdminPanel.setupWithViewPager(viewPagerAdminPanel);

        return view;
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
        public void show()
        {
            for(String s : fragments_title)
            {
                Log.e("title", ""+s);
            }
        }
    }
}
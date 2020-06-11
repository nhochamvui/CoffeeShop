package com.example.coffeeshop;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

class MyPagerAdapter extends FragmentStatePagerAdapter {

    // tab titles
    private String[] tabTitles = new String[]{"Tab1", "Tab2", "Tab3"};

    public MyPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    // overriding getPageTitle()
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BlankFragment();
            case 1:
                return new BlankFragment();
            case 2:
                return new BlankFragment();
            default:
                throw new RuntimeException("Invalid tab position");
        }
    }
    @Override
    public int getCount() {
        return tabTitles.length;
    }
}
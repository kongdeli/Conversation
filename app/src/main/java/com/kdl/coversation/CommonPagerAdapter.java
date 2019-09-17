package com.kdl.coversation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class CommonPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mList;

    public CommonPagerAdapter(@NonNull FragmentManager fm, List<Fragment> list) {
        super(fm);
        mList = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }
}

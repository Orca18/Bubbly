package com.mainnet.bubbly.controller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mainnet.bubbly.tabFragments.Fragment_Tab1_follower;
import com.mainnet.bubbly.tabFragments.Fragment_Tab2_following;


public class FollowingFragmentAdapter extends FragmentStateAdapter {

    String uid;

    public FollowingFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String uid) {
        super(fragmentManager, lifecycle);
        this.uid = uid;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 1:
                return new Fragment_Tab2_following();

        }
        // 기본 1번 탭
        return new Fragment_Tab1_follower();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}

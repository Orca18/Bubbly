package com.example.bubbly.controller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bubbly.tabFragments.Fragment_Tab1_AllPosts_ss;
import com.example.bubbly.tabFragments.Fragment_Tab2_Replies_ss;
import com.example.bubbly.tabFragments.Fragment_Tab3_NFTs_ss;
import com.example.bubbly.tabFragments.Fragment_Tab4_Likes_ss;

public class FragmentAdapter_SS extends FragmentStateAdapter {

    String uid;

    public FragmentAdapter_SS(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String uid) {
        super(fragmentManager, lifecycle);
        this.uid = uid;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            // 2번 탭
            case 1:
                return new Fragment_Tab2_Replies_ss();
            // 3번 탭
            case 2:
                return new Fragment_Tab3_NFTs_ss();
            // 4번 탭
            case 3:
                return new Fragment_Tab4_Likes_ss();

        }
        // 기본 1번 탭
        return new Fragment_Tab1_AllPosts_ss();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
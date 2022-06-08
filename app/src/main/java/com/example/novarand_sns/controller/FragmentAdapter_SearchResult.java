package com.example.novarand_sns.controller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.novarand_sns.tabFragments.FragmentSR_Tab1_Hot;
import com.example.novarand_sns.tabFragments.FragmentSR_Tab2_Recents;
import com.example.novarand_sns.tabFragments.FragmentSR_Tab3_Users;
import com.example.novarand_sns.tabFragments.FragmentSR_Tab5_Community;
import com.example.novarand_sns.tabFragments.FragmentSR_Tab4_NFTs;

public class FragmentAdapter_SearchResult extends FragmentStateAdapter {

    String uid;

    public FragmentAdapter_SearchResult(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String uid) {
        super(fragmentManager, lifecycle);
        this.uid = uid;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            // 2번 탭
            case 1:
                return new FragmentSR_Tab2_Recents();
            // 3번 탭
            case 2:
                return new FragmentSR_Tab3_Users();
            // 4번 탭
            case 3:
                return new FragmentSR_Tab4_NFTs();
            // 5번 탭
            case 4:
                return new FragmentSR_Tab5_Community();
        }
        // 기본 1번 탭
        return new FragmentSR_Tab1_Hot();
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
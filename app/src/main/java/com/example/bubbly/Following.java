package com.example.bubbly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bubbly.controller.FollowingFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

public class Following extends AppCompatActivity {

    // 탭 레이아웃
    TabLayout tabLayout;
    ViewPager2 pager2;
    FollowingFragmentAdapter adapter;

    String uid;

    ImageView iv_back;
    TextView tv_user_nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        iv_back = findViewById(R.id.iv_back);
        tv_user_nick = findViewById(R.id.tv_user_nick);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tabInit(); // 프래그먼트 탭

    } // onCreate 닫는곳

    private void tabInit() {
        tabLayout = findViewById(R.id.following_tab_layout);
        pager2 = findViewById(R.id.following_view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        adapter = new FollowingFragmentAdapter(fm, getLifecycle(), uid);
        pager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("팔로워"));
        tabLayout.addTab(tabLayout.newTab().setText("팔로잉"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }


    // 프래그먼트 어답터에서 Uid 받기 위해서 필요
    public String getUid(){
        return uid;
    }

}
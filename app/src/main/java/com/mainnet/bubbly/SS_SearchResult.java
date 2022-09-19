package com.mainnet.bubbly;
import android.util.Log;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.controller.FragmentAdapter_SearchResult;
import com.google.android.material.tabs.TabLayout;

public class SS_SearchResult extends AppCompatActivity {

    Toolbar toolbar;

    LinearLayout searching;

    // 탭 레이아웃
    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter_SearchResult adapter;
    String uid;

    TextView searchkeyword;
    String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ss_search);

        Bundle extras = getIntent().getExtras();
        keyword = extras.getString("keyword");


        initiallize();
        tabinit();

        searchkeyword.setText(keyword);
        Log.i("정보태그", "zzz"+keyword);
    }


    private void initiallize() {
        toolbar = findViewById(R.id.toolbar_searchResult);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searching = findViewById(R.id.searchresult_toSearching);
        searchkeyword = findViewById(R.id.searchresult_toSearching_text);

        searching.setOnClickListener(v -> {
            Intent mIntent = new Intent(getApplicationContext(), SS_SearchMode.class);
            mIntent.putExtra("keyword", keyword);
            startActivity(mIntent);
        });
    }

    private void tabinit() {
        tabLayout = findViewById(R.id.searchresult_tablayout);
        pager2 = findViewById(R.id.searchresult_pager2);

        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter_SearchResult(fm, getLifecycle(), uid);
        pager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("인기"));
        tabLayout.addTab(tabLayout.newTab().setText("최신"));
        tabLayout.addTab(tabLayout.newTab().setText("유저"));
        tabLayout.addTab(tabLayout.newTab().setText("NFT"));
        tabLayout.addTab(tabLayout.newTab().setText("커뮤니티"));

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                // 왼쪽 상단 버튼 눌렀을 때
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // 프래그먼트 어답터에서 Uid 받기 위해서 필요
    public String getUid() {
        return uid;
    }
    public String getKeyword(){
        return keyword;
    }

}
package com.example.novarand_sns;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.novarand_sns.controller.Posts_Adapter;
import com.example.novarand_sns.model.Posts_Item;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    // 뒤로가기 시간
    private long backKeyPressedTime = 0;
    private Toast toast;

    androidx.appcompat.widget.Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView sidemenu;

    ProgressBar progressBar;

    private Posts_Adapter adapter;
    private List<Posts_Item> exampleList;

    RecyclerView recyclerView;

    private Parcelable recyclerViewState;

    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_a_home);

        // 리소스 ID 선언
        initiallize();
        // 리사이클러뷰 데이터 가져오기
        loadrecycler();
        // 클릭 리스너 모음
        clickListeners();
    }

    // ========================================================
    // 데이터 http 요청
    private void loadrecycler() {
        // 쓰레드 http 요청 & run 데이터 넣기
        fillList();
    }

    // loadrecycler 에서 요청/응답 받은 데이터 채워넣기
    private void fillList() {
        this.exampleList = new ArrayList();

        String 임시프사 = "https://s2.coinmarketcap.com/static/img/coins/200x200/4030.png";
        String 임시미디어 = "https://image.shutterstock.com/image-vector/example-sign-paper-origami-speech-260nw-1164503347.jpg";


        for (int i = 0; i < 20; i++){
            // TODO 시간 계산 → String 으로 넣어주기
            this.exampleList.add(new Posts_Item(임시프사, "이름"+i,"아이디"+i,"내용","",1,2,3,"", i+"h"));

        }

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        this.adapter = new Posts_Adapter(getApplicationContext(), this.exampleList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(this.adapter);

        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        recyclerView.addOnScrollListener(onScrollListener);
    }

    // 바닥에 도달했을 때...
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
            if (lastVisibleItemPosition == itemTotalCount) {
                //TODO 바닥 작업
//                progressBar.setVisibility(View.VISIBLE);
//                loadMoreData();
            }
        }
    };

    // 클릭 이벤트 모음
    private void clickListeners() {
        
        // 좌측 상단 메뉴 버튼
        sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // DrawerLayer (사이드 메뉴) 내부 카테고리 클릭 = 별로인듯... 그냥 참고용으로 쓰기
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
//                    case R.id.nav_camera:
//                        item.setChecked(true);
//                        Toast.makeText(getApplicationContext(), "ㅇㅇ",Toast.LENGTH_SHORT).show();
//                        drawerLayout.closeDrawers();
//                        return true;

                }
                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
//                        loadrecycler();
                        Toast.makeText(getApplicationContext(), "TODO 새로고침",Toast.LENGTH_SHORT).show();
                        /* 업데이트가 끝났음을 알림 */
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recyclerView);
        navigationView = findViewById(R.id.navigation_view);
        sidemenu = findViewById(R.id.home_sidemenu);
        swipeRefreshLayout = findViewById(R.id.home_refresh);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                // 왼쪽 상단 버튼 눌렀을 때
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //뒤로가기 했을 때
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
        else {
            super.onBackPressed();
        }
    }



}

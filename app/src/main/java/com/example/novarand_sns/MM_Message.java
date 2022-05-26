package com.example.novarand_sns;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.novarand_sns.controller.Messages_Adapter;
import com.example.novarand_sns.controller.Posts_Adapter;
import com.example.novarand_sns.model.Messages_Item;
import com.example.novarand_sns.model.Posts_Item;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MM_Message extends AppCompatActivity {

    // 뒤로가기 시간
    private long backKeyPressedTime = 0;
    private Toast toast;

    // 바텀 메뉴
    LinearLayout bthome, btissue, btwallet, btmessage, btprofile;

    // 툴바, 사이드 메뉴
    androidx.appcompat.widget.Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView sidemenu;

    // 새로고침, 프로그레스바
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;

    // 리사이클러 뷰
    private Messages_Adapter adapter;
    private List<Messages_Item> exampleList;
    RecyclerView recyclerView;

    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_c_message);

        // 리소스 ID 선언
        initiallize();
        // 바텀 메뉴 - 스택 X 액티비티 이동 (TODO 바텀 내비게이션으로 변경하는 작업)
        bottomNavi();
        // 클릭 리스너 모음 - 스택 O
        clickListeners();
        // 리사이클러뷰 데이터 가져오기
        loadrecycler();
    }


    // ========================================================

    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = findViewById(R.id.message_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.message_navigation_view);
        sidemenu = findViewById(R.id.message_sidemenu);
        swipeRefreshLayout = findViewById(R.id.message_refresh);
        recyclerView = findViewById(R.id.message_recyclerView);

        // 바텀 메뉴
        bthome = findViewById(R.id.message_tohome);
        btissue = findViewById(R.id.message_toissue);
        btmessage = findViewById(R.id.message_tomessage);
        btprofile = findViewById(R.id.message_toprofile);
        btwallet = findViewById(R.id.message_towallet);

    }

    // 바텀 메뉴 클릭
    private void bottomNavi() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.message_tohome:
                        Intent mIntent1 = new Intent(getApplicationContext(), MM_Home.class);
                        mIntent1.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent1);
                        finish();
                        break;

                    case R.id.message_toissue:
                        Intent mIntent2 = new Intent(getApplicationContext(), MM_Issue.class);
                        mIntent2.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent2);
                        finish();
                        break;

                    case R.id.message_tomessage:
                        break;

                    case R.id.message_toprofile:
                        Intent mIntent3 = new Intent(getApplicationContext(), MM_Profile.class);
                        mIntent3.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent3);
                        finish();
                        break;

                    case R.id.message_towallet:
                        Intent mIntent4 = new Intent(getApplicationContext(), MM_Wallet.class);
                        mIntent4.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent4);
                        finish();
                        break;

                    default:
                        break;
                }
            }
        };

        bthome.setOnClickListener(clickListener);
        btissue.setOnClickListener(clickListener);
        btwallet.setOnClickListener(clickListener);
        btmessage.setOnClickListener(clickListener);
        btprofile.setOnClickListener(clickListener);

    }


    // 클릭 이벤트 모음
    private void clickListeners() {

        // 좌측 상단 메뉴 버튼
        sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // DrawerLayer (사이드 메뉴) 내부 카테고리 클릭 = 별로인듯... 그냥 참고용으로 쓰기 (메뉴 대신 헤더 xml 에서 전부 완성 시킴)
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
//                    case R.id.nav_camera:
//                        item.setChecked(true);
//                        Toast.makeText(getApplicationContext(), "ㅇㅇ",Toast.LENGTH_SHORT).show();
//                        drawerLayout.closeDrawers();
//                        return true;

                }
                return false;
            }
        });

        // 리사이클러뷰 새로고침 인식
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
//                        loadrecycler();
                        Toast.makeText(getApplicationContext(), "TODO 새로고침", Toast.LENGTH_SHORT).show();
                        /* 업데이트가 끝났음을 알림 */
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
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
        } else if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        } else {
            super.onBackPressed();
        }
    }

    // 액티비티 종료 시, 애니메이션 효과 없애기
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    // ==============================================================================================

    // 데이터 http 요청
    private void loadrecycler() {
        // 쓰레드 http 요청 & run 데이터 넣기
        fillList();
    }

    // loadrecycler 에서 요청/응답 받은 데이터 채워넣기
    private void fillList() {
        this.exampleList = new ArrayList();

        String 임시이미지1 = "https://cdn.econovill.com/news/photo/202204/573467_495356_727.png";
        String 임시이미지2 = "https://img-lb.inews24.com/image_gisa/202107/1626418981_1779.jpg";
        String 임시이미지3 = "https://w.namu.la/s/faadee31d521afd584990cb798403ae1d9a5affbef213ae894026604566bbf1f00de845e734b94678f1ed7280f3729755e39422db14d905ee5a9798d568487e0f6b9dbdc235301185c0aa03aab341dbc";
        String 임시이미지4 = "data:image/jpeg;base64,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";


        this.exampleList.add(new Messages_Item(임시이미지1, "도지", "?????", "22:00"));
        this.exampleList.add(new Messages_Item(임시이미지2, "이더리움", "2.0 발표!", "18:18"));
        this.exampleList.add(new Messages_Item(임시이미지3, "솔라나", "역사증명", "12:34"));
        this.exampleList.add(new Messages_Item(임시이미지4, "김동현", "...", "10:08"));

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        this.adapter = new Messages_Adapter(getApplicationContext(), this.exampleList);
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
}

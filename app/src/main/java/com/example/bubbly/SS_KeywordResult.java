package com.example.bubbly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bubbly.controller.Feed_Adapter;
import com.example.bubbly.model.Feed_Item;

import java.util.ArrayList;
import java.util.List;

public class SS_KeywordResult extends AppCompatActivity {

    Toolbar toolbar;
    ImageView option;

    String keywork = "기본";

    private Feed_Adapter adapter;
    private List<Feed_Item> postsList;

    RecyclerView recyclerView;

    private Parcelable recyclerViewState;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ss_keyword_result);

        // 어떤 카테고리를 들어온건지 (어떤 카테고리의 글들을 불러올 것인지)
        Bundle extras = getIntent().getExtras();
        keywork = extras.getString("keyword");


        // 리소스 ID 선언
        initiallize();
        // 새로고침 리스너
        refreshListeners();
        // 리사이클러뷰 데이터 가져오기 TODO 키워드 요청에 따라 가져오기
//        loadrecycler();
    }

    private void initiallize() {
        toolbar = findViewById(R.id.toolbar_KeywordResult);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""+keywork);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        option = findViewById(R.id.keywordresult_option);
        recyclerView = findViewById(R.id.keyword_recyclerView);
        swipeRefreshLayout = findViewById(R.id.keyword_refresh);
    }


    // 데이터 http 요청
    private void loadrecycler() {
        // 쓰레드 http 요청 & run 데이터 넣기
        fillList();
    }

    // loadrecycler 에서 요청/응답 받은 데이터 채워넣기
    private void fillList() {
        this.postsList = new ArrayList();

        String 임시프사 = "https://s2.coinmarketcap.com/static/img/coins/200x200/4030.png";
        String 임시미디어 = "https://image.shutterstock.com/image-vector/example-sign-paper-origami-speech-260nw-1164503347.jpg";


        for (int i = 0; i < 20; i++) {
            // TODO 시간 계산 → String 으로 넣어주기
            this.postsList.add(new Feed_Item(임시프사, "이름" + i, "아이디" + i, "내용", "", 1, 2, 3, "", i + "h",  false));

        }

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        this.adapter = new Feed_Adapter(getApplicationContext(), this.postsList);
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
    private void refreshListeners() {

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
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
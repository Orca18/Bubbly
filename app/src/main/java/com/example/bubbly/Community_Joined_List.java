package com.example.bubbly;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bubbly.controller.JoinedCom_Adapter;
import com.example.bubbly.kim_util_test.Kim_ApiClient;
import com.example.bubbly.kim_util_test.Kim_ApiInterface;
import com.example.bubbly.kim_util_test.Kim_JoinedCom_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Community_Joined_List extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;


    SharedPreferences preferences;
    String user_id;

    RecyclerView recyclerView;
    private JoinedCom_Adapter adapter;
    private Parcelable recyclerViewState;

    ArrayList<Kim_JoinedCom_Response> list;
    LinearLayoutManager linearLayoutManager;

    SwipeRefreshLayout swipeRefreshLayout;

    Button toFeeds;

    ImageView create_com;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ss_community_home2);

        preferences = getSharedPreferences("novarand",MODE_PRIVATE);

        // 리소스 ID 선언
        ini();
        // 리스너
        listeners();
        // 리사이클러뷰 채우기
        getJoinedComList();
    }


    private void listeners() {
        // 리사이클러뷰 새로고침 인식
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getJoinedComList();
                        /* 업데이트가 끝났음을 알림 */
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        toFeeds.setOnClickListener(v -> {
            Intent mIntent = new Intent(getApplicationContext(), Community_Home_Feeds.class);
            startActivity(mIntent);
            finish();
        });

        create_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), Community_Create.class);
                startActivity(mIntent);
            }
        });


    }

    private void ini() {
        toolbar = findViewById(R.id.community_home_toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toFeeds = findViewById(R.id.community_toFeeds);

        recyclerView = findViewById(R.id.community_home_recyclerView2);
        swipeRefreshLayout = findViewById(R.id.community_home_refresh);
        create_com = findViewById(R.id.create_com);

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 액티비티 종료 시, 애니메이션 효과 없애기
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }















    // TODO 커뮤니티 생성 완료 된건지 확인하고 삭제하기!
    private void getJoinedComList() {

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        list = new ArrayList<>();
        adapter = new JoinedCom_Adapter(getApplicationContext(), this.list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        String user_id;
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값
        Kim_ApiInterface api = Kim_ApiClient.getApiClient().create(Kim_ApiInterface.class);
        Call<List<Kim_JoinedCom_Response>> call = api.selectCommunityListUsingUserId(user_id);
        call.enqueue(new Callback<List<Kim_JoinedCom_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<Kim_JoinedCom_Response>> call, @NonNull Response<List<Kim_JoinedCom_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<Kim_JoinedCom_Response> responseResult = response.body();
                    // 결과
                    for(int i=0; i<responseResult.size(); i++){
                        list.add(new Kim_JoinedCom_Response(responseResult.get(i).getCommunity_id(),
                                responseResult.get(i).getCommunity_owner_id(),
                                responseResult.get(i).getCommunity_name(),
                                responseResult.get(i).getCommunity_desc(),
                                responseResult.get(i).getProfile_file_name()));
                        Log.d("디버그태그", "무야::"+responseResult.get(i).getCommunity_id());
                        Log.d("디버그태그", "무야::"+responseResult.size());
                        Log.d("디버그태그", "무야::"+i);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Kim_JoinedCom_Response>> call, @NonNull Throwable t)
            {
                Toast.makeText(getApplicationContext(), "응 다시해",Toast.LENGTH_SHORT).show();
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        getJoinedComList();
    }
}
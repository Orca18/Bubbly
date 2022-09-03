package com.example.bubbly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.example.bubbly.controller.MyCommunitiesFeeds_Adapter;
import com.example.bubbly.kim_util_test.Kim_ApiClient;
import com.example.bubbly.kim_util_test.Kim_ApiInterface;
import com.example.bubbly.kim_util_test.Kim_Com_post_Response;
import com.example.bubbly.kim_util_test.Kim_Post_Adapter;
import com.example.bubbly.model.MyCommunitiesFeeds_Item;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Community_Home_Feeds extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;

    RecyclerView recyclerView;

    private Parcelable recyclerViewState;

    SwipeRefreshLayout swipeRefreshLayout;

    Button toList;

    Kim_ApiInterface api;

    ImageView toCreatePost;

    //////////////////////////////////////////////
    Kim_Post_Adapter post_adapter;
    ArrayList<Kim_Com_post_Response> postList;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ss_community_home);

        initialize();

        listeners();

        GetJoinedComPosters();

    }

    private void listeners() {
        toCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tocreating = new Intent(getApplicationContext(), Post_Create.class);
                tocreating.putExtra("com_id", "0");
                tocreating.putExtra("com_name", "내 피드");
                startActivity(tocreating);
            }
        });
    }

    private void initialize() {
        api = Kim_ApiClient.getApiClient(Community_Home_Feeds.this).create(Kim_ApiInterface.class);

        // 툴바
        toolbar = findViewById(R.id.community_home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toList = findViewById(R.id.community_toList);

        toCreatePost = findViewById(R.id.community_home_createPost);

        recyclerView = findViewById(R.id.community_home_recyclerView);
        swipeRefreshLayout = findViewById(R.id.community_home_refresh);


        // 리사이클러뷰 새로고침 인식
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        GetJoinedComPosters();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        toList.setOnClickListener(v -> {
            Intent mIntent = new Intent(getApplicationContext(), Community_Joined_List.class);
            startActivity(mIntent);
            finish();
        });
    }


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




    private void GetJoinedComPosters() {
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        postList = new ArrayList<>();
        post_adapter = new Kim_Post_Adapter(getApplicationContext(), this.postList, getApplicationContext());
        recyclerView.setAdapter(post_adapter);
        post_adapter.notifyDataSetChanged();


        SharedPreferences preferences = getSharedPreferences("novarand", MODE_PRIVATE);
        String user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

        Log.i("정보태그", "user_id = " + user_id);

        Call<List<Kim_Com_post_Response>> call = api.selectAllCommunityPost(user_id);
        call.enqueue(new Callback<List<Kim_Com_post_Response>>() {
            @Override
            public void onResponse(Call<List<Kim_Com_post_Response>> call, Response<List<Kim_Com_post_Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Kim_Com_post_Response> responseResult = response.body();
                    for (int i = 0; i < responseResult.size(); i++) {

                        postList.add(new Kim_Com_post_Response(responseResult.get(i).getPost_id(),
                                responseResult.get(i).getPost_writer_id(),
                                responseResult.get(i).getNick_name(),
                                responseResult.get(i).getPost_contents(),
                                responseResult.get(i).getFile_save_names(),
                                responseResult.get(i).getLike_count(),
                                responseResult.get(i).getLike_yn(),
                                responseResult.get(i).getShare_post_yn(),
                                responseResult.get(i).getNft_post_yn(),
                                responseResult.get(i).getProfile_file_name(),
                                responseResult.get(i).getCre_datetime(),
                                responseResult.get(i).getCommunity_id(),
                                responseResult.get(i).getMentioned_user_list(),
                                responseResult.get(i).getPost_type()
                        ));
                    }
                    post_adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<List<Kim_Com_post_Response>> call, Throwable t) {
                Log.e("오류태그", "리스폰스 실패");
            }
        });

    }
}
package com.example.bubbly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;

import com.example.bubbly.controller.Post_Adapter;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.post_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Community_Memberlist extends AppCompatActivity {

    String com_id;
    Post_Adapter post_adapter;
    ArrayList<post_Response> postList;
    LinearLayoutManager linearLayoutManager;

    SharedPreferences preferences;
    String user_id;

    RecyclerView recyclerView;

    private Parcelable recyclerViewState;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_member);

        Intent intent = getIntent();
        com_id = intent.getStringExtra("com_id");

        initialize();
        selectPost_Followee_Communit();
    }

    private void initialize() {
        // 툴바
        toolbar = findViewById(R.id.com_member_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.com_member_list);
    }

    private void selectPost_Followee_Communit() {
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        postList = new ArrayList<>();
        post_adapter = new Post_Adapter(getApplicationContext(), this.postList, getApplicationContext());
        recyclerView.setAdapter(post_adapter);
        post_adapter.notifyDataSetChanged();

        preferences = getSharedPreferences("novarand", MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

        // 1. 레트로핏 빌드 & 인터페이스 지정?
        ApiInterface take = ApiClient.getApiClient().create(ApiInterface.class);
        // 2. Response = 인터페이스내함수 // user_id 보내서 원하는 response 기다림
        Call<List<post_Response>> call = take.selectPostMeAndFolloweeAndCommunity(user_id);
        // 3. 선언한 call 을 게시글용 DTO
        call.enqueue(new Callback<List<post_Response>>() {
            @Override
            public void onResponse(@NonNull Call<List<post_Response>> call, @NonNull Response<List<post_Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 리스트 새로 만들어서
                    List<post_Response> responseResult = response.body();
                    for (int i = 0; i < responseResult.size(); i++) {
                        // Response DTO
                        postList.add(new post_Response(responseResult.get(i).getPost_id(),
                                responseResult.get(i).getPost_writer_id(),
                                responseResult.get(i).getWriter_name(),
                                responseResult.get(i).getPost_contents(),
                                responseResult.get(i).getFile_save_names(),
                                responseResult.get(i).getLike_count(),
                                responseResult.get(i).getLike_yn(),
                                responseResult.get(i).getShare_post_yn(),
                                responseResult.get(i).getNft_post_yn(),
                                responseResult.get(i).getNick_name(),
                                responseResult.get(i).getProfile_file_name(),
                                responseResult.get(i).getCre_datetime(),
                                responseResult.get(i).getMentioned_user_list()));
                    }
                    post_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<post_Response>> call, @NonNull Throwable t) {
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
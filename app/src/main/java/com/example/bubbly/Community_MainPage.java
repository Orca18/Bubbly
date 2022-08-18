package com.example.bubbly;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bubbly.controller.Post_Adapter;
import com.example.bubbly.kim_util_test.Kim_ApiClient;
import com.example.bubbly.kim_util_test.Kim_ApiInterface;
import com.example.bubbly.kim_util_test.Kim_Com_Info_Response;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.post_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Community_MainPage extends AppCompatActivity {

    String com_id;
    Toolbar toolbar;
    //////////////////////////////////////////////
    Post_Adapter post_adapter;
    ArrayList<post_Response> postList;
    LinearLayoutManager linearLayoutManager;

    SharedPreferences preferences;
    String user_id;
    String com_name;
    String com_owner;

    RecyclerView recyclerView;

    private Parcelable recyclerViewState;


    // 대문 이미지 => 정보 (소개+공지)
    // LL : 가입 여부 표시 / 멤버 목록 액티비티 / 정보 - 소개 = 공지 / 게시글 작성 그룹 아이디 인텐트 / NFTs = nft 상점?
    RelativeLayout rl_title;
    LinearLayout ll_join, ll_member, ll_notice, ll_nfts;
    TextView title_name;
    ImageView title_image;
    // 게시글 생성
    LinearLayout posting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_mainpage);

        Intent intent = getIntent();
        com_id = intent.getStringExtra("com_id");
        Log.i("정보태그", "com_id = " + com_id);

        initialize();
        // 커뮤니티 정보 가져오기
        GetComInfo();
        // 커뮤니티 게시글 가져오기 - 리사이클러뷰
        GetComPosters();
        // 리사이클러뷰 위 쪽 클릭 리스너
        listeners();

    }


    private void initialize() {
        // 툴바
        toolbar = findViewById(R.id.com_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 게시글
        recyclerView = findViewById(R.id.com_main_recyclerview);
        // 나머지
        rl_title = findViewById(R.id.com_info_maintitle);
        ll_join = findViewById(R.id.com_main_join);
        ll_member = findViewById(R.id.com_main_member);
        ll_notice = findViewById(R.id.com_main_notice);
        ll_nfts = findViewById(R.id.com_main_nfts);
        posting = findViewById(R.id.community_info_posting);
        title_name = findViewById(R.id.com_main_name);
        title_image =findViewById(R.id.com_info_titleimage);

    }


    private void listeners() {

        rl_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 커뮤니티 상세 정보 페이지
                Intent mIntent = new Intent(getApplicationContext(), Community_Info.class);
                mIntent.putExtra("com_id",com_id);
                mIntent.putExtra("com_name",com_name);
                mIntent.putExtra("com_owner",com_owner);
                startActivity(mIntent);
            }
        });

        ll_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 가입함 or 가입가능 상태에 따라 보여주는 상태 변경
            }
        });

        // 멤버 목록 보여주기
        ll_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), Community_Memberlist.class);
                mIntent.putExtra("com_id", com_id);
                mIntent.putExtra("com_name", com_name);
                startActivity(mIntent);
            }
        });

        // 공지 보여주기
        ll_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), Community_Memberlist.class);
                mIntent.putExtra("com_id", com_id);
                mIntent.putExtra("com_name", com_name);
                startActivity(mIntent);
                Toast.makeText(getApplicationContext(), "like 카카오톡",Toast.LENGTH_SHORT).show();
            }
        });

        // NFT 목록 보여주기
        ll_nfts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 가입함 or 가입가능 상태에 따라 보여주는 상태 변경...
                // 어떻게 해당 커뮤니티에 해당하는 nft를 가져올지 고민하기
            }
        });


        // 현재 커뮤니티에 게시글 작성하기
        posting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), Add_Posting_Create.class);
                mIntent.putExtra("com_id", "커뮤아이디");
                startActivity(mIntent);
            }
        });
    }


    private void GetComInfo() {
        Kim_ApiInterface api = Kim_ApiClient.getApiClient().create(Kim_ApiInterface.class);
        Call<List<Kim_Com_Info_Response>> call = api.selectCommunityUsingCommunityId(com_id);
        call.enqueue(new Callback<List<Kim_Com_Info_Response>>() {
            @Override
            public void onResponse(Call<List<Kim_Com_Info_Response>> call, Response<List<Kim_Com_Info_Response>> response) {

                com_name = response.body().get(0).getCommunity_name();
                com_owner = response.body().get(0).getCommunity_owner_id();
                title_name.setText(com_name);
                Glide.with(getApplicationContext()) //해당 환경의 Context나 객체 입력
                        .load("https://d2gf68dbj51k8e.cloudfront.net/"+response.body().get(0).getProfile_file_name()) //URL, URI 등등 이미지를 받아올 경로
                        .centerCrop()
                        .into(title_image);


            }

            @Override
            public void onFailure(Call<List<Kim_Com_Info_Response>> call, Throwable t) {

            }
        });


    }


    private void GetComPosters() {
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
        ApiInterface selectPostMeAndFolloweeAndCommunity_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<post_Response>> call = selectPostMeAndFolloweeAndCommunity_api.selectPostMeAndFolloweeAndCommunity(user_id);
        call.enqueue(new Callback<List<post_Response>>() {
            @Override
            public void onResponse(@NonNull Call<List<post_Response>> call, @NonNull Response<List<post_Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
//                    progressBar.setVisibility(View.GONE);
                    List<post_Response> responseResult = response.body();
                    for (int i = 0; i < responseResult.size(); i++) {
                        ;
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
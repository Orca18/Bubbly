package com.example.bubbly;

import com.bumptech.glide.Glide;

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

import com.example.bubbly.kim_util_test.Kim_ApiClient;
import com.example.bubbly.kim_util_test.Kim_ApiInterface;
import com.example.bubbly.kim_util_test.Kim_Com_Info_Response;
import com.example.bubbly.kim_util_test.Kim_Com_post_Response;
import com.example.bubbly.kim_util_test.Kim_Post_Adapter;
import com.example.bubbly.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Community_MainPage extends AppCompatActivity {

    String com_id;
    Toolbar toolbar;
    //////////////////////////////////////////////
    Kim_Post_Adapter post_adapter;
    ArrayList<Kim_Com_post_Response> postList;
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
    CircleImageView cv_profile;

    Kim_ApiInterface api;

    String join_yn;
    ImageView iv_join_yn;
    TextView tv_join_yn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_mainpage);

        api = Kim_ApiClient.getApiClient().create(Kim_ApiInterface.class);

        Intent intent = getIntent();
        com_id = intent.getStringExtra("com_id");
        join_yn = intent.getStringExtra("y");



        initialize();
        // 커뮤니티 정보 가져오기
        GetComInfo();
        // 커뮤니티 내 본인 정보 가져오기
        GetUserStatus();
        // 리사이클러뷰 위 쪽 클릭 리스너
        listeners();

        // 커뮤니티 게시글 가져오기 - 리사이클러뷰
        GetComPosters();

    }

    private void GetUserStatus() {
        // 가입 가능한 목록에서 왔을 경우에는, n 이기 때문에 '가입 신청' 버튼
        if(join_yn.equals("y")){
            tv_join_yn.setText("가입함");
            iv_join_yn.setImageResource(R.drawable.ic_baseline_check_24);
        } else {
            tv_join_yn.setText("가입 신청");
            iv_join_yn.setImageResource(R.drawable.ic_baseline_add_circle_24);
        }
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
        rl_title = findViewById(R.id.com_info_edit_rl);
        ll_join = findViewById(R.id.com_main_join);
        ll_member = findViewById(R.id.com_main_member);
        ll_notice = findViewById(R.id.com_main_notice);
        ll_nfts = findViewById(R.id.com_main_nfts);
        posting = findViewById(R.id.community_info_posting);
        title_name = findViewById(R.id.com_main_name);
        title_image = findViewById(R.id.com_main_titleimage);
        cv_profile = findViewById(R.id.com_main_profile);

        iv_join_yn = findViewById(R.id.com_main_join_checkiv);
        tv_join_yn = findViewById(R.id.com_main_join_checktv);
        
    }


    private void listeners() {

        rl_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                Intent mIntent = new Intent(getApplicationContext(), Post_Create.class);
                mIntent.putExtra("com_id", com_id);
                mIntent.putExtra("com_name", com_name);
                startActivity(mIntent);
            }
        });
    }


    private void GetComInfo() {
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



        // 프로파일 이미지
        if(UserInfo.profile_file_name!=null && !UserInfo.profile_file_name.equals("")){
            Glide.with(getApplicationContext())
                    .load(UserInfo.profile_file_name)
                    .circleCrop()
                    .into(cv_profile);
        }



    }


    private void GetComPosters() {
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

        preferences = getSharedPreferences("novarand", MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

        Log.i("정보태그", "user_id = " + user_id);

        Call<List<Kim_Com_post_Response>> call = api.selectCommunityPost(user_id, com_id);
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
                                responseResult.get(i).getMentioned_user_list()
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        GetComInfo();
    }
}
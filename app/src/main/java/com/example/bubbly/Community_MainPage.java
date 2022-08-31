package com.example.bubbly;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


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
import android.widget.Toast;

import com.example.bubbly.config.Config;
import com.example.bubbly.kim_util_test.Kim_ApiClient;
import com.example.bubbly.kim_util_test.Kim_ApiInterface;
import com.example.bubbly.kim_util_test.Kim_Com_Info_Response;
import com.example.bubbly.kim_util_test.Kim_Com_JoinYn_Response;
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
    LinearLayout ll_join, ll_member, ll_invite, ll_nfts;
    TextView title_name;
    ImageView title_image;
    // 게시글 생성
    LinearLayout posting;
    CircleImageView cv_profile;

    Kim_ApiInterface api;

    ImageView iv_join_yn;
    TextView tv_join_yn;

    SwipeRefreshLayout swipeRefreshLayout;

    String str_join_yn_truefalse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_mainpage);

        api = Kim_ApiClient.getApiClient().create(Kim_ApiInterface.class);

        Intent intent = getIntent();
        com_id = intent.getStringExtra("com_id");

        // TODO 인텐트로 온 경우, 예외처리하기
        if(intent.getData() != null){
            com_id = intent.getDataString().replace("bubbly2://3.39.84.115/","");
        }

        preferences = getSharedPreferences("novarand",MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값


        initialize();
        // 커뮤니티 정보 가져오기
        GetComInfo();

        // 리사이클러뷰 위 쪽 클릭 리스너
        listeners();

        // 커뮤니티 게시글 가져오기 - 리사이클러뷰
        GetComPosters();

    }

    private void GetUserStatus() {

        Log.d("디버그태그", "com_owner/user_id:"+com_owner+user_id);
        if(com_owner.equals(user_id)){
            ll_join.setClickable(false);
            tv_join_yn.setText("관리자"); // 적당한 아이콘 찾아보기
            iv_join_yn.setImageResource(R.drawable.user_circle);
        } else {
            Call<List<Kim_Com_JoinYn_Response>> call = api.selectCommunityJoinYn(com_id,user_id);
            call.enqueue(new Callback<List<Kim_Com_JoinYn_Response>>() {
                @Override
                public void onResponse(Call<List<Kim_Com_JoinYn_Response>> call, Response<List<Kim_Com_JoinYn_Response>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Kim_Com_JoinYn_Response> responseResult = response.body();
                        str_join_yn_truefalse = responseResult.get(0).getJoin_yn();
                        setJoinButton();
                    }
                }
                @Override
                public void onFailure(Call<List<Kim_Com_JoinYn_Response>> call, Throwable t) {
                    Log.e("오류태그", "리스폰스 실패_joinyn");
                }
            });
        };
        
    }

    private void setJoinButton() {
        if (str_join_yn_truefalse.equals("false")) {        // 아직 회원이 아니라면, 가입 신청 버튼 띄우기
            tv_join_yn.setText("가입 신청"); // 적당한 아이콘 찾아보기
            iv_join_yn.setImageResource(R.drawable.user_plus);
        } // 가입 상태면 탈퇴 신청 (y/n) 다이얼로그 띄우기 ==> 어차피 처음에는 가입함으로 뜸
        
        ll_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (str_join_yn_truefalse.equals("false")) {
                    JoinComRequest(); // 가입 신청
                } else { // 가입중이라, 탈퇴
//                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            switch (which) {
//                                case DialogInterface.BUTTON_POSITIVE:
//                                    //Yes 버튼을 클릭했을때 처리 == 탈퇴하고, 가입 상태 n으로 변경
                    ExitCom(); // 탈퇴
                    str_join_yn_truefalse = "n";
//                                    break;
//
//                                case DialogInterface.BUTTON_NEGATIVE:
//                                    //No 버튼을 클릭했을때 처리
//                                    break;
//                            }
//                        }
//                    };
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                    builder.setMessage("탈퇴하시겠습니까?").setPositiveButton("예", dialogClickListener)
//                            .setNegativeButton("아니요", dialogClickListener).show();


                }

            }
        });
    }


    private void JoinComRequest() {
        Log.e("오류태그", "user_id, com_id: " + user_id + com_id);
        Call<String> call = api.createCommunityParticipationReq(user_id, com_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplicationContext(), "가입 신청 보냄", Toast.LENGTH_SHORT).show();

                    JoinComReqestAuto();// 일단은 자동 승인

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("게시글 생성 에러", t.getMessage());
            }
        });
    }


    private void ExitCom() {
        Call<String> call = api.deleteCommunityParticipant(user_id, com_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplicationContext(), "탈퇴 완료", Toast.LENGTH_SHORT).show();
                    tv_join_yn.setText("가입 신청");
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("게시글 생성 에러", t.getMessage());
            }
        });
    }


    private void JoinComReqestAuto() {
        Call<String> call = api.approveCommunityParticipation(user_id, com_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplicationContext(), "가입 승인", Toast.LENGTH_SHORT).show();
                    tv_join_yn.setText("가입됨");
                    str_join_yn_truefalse = "y";
                    iv_join_yn.setImageResource(R.drawable.user_check);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("게시글 생성 에러", t.getMessage());
            }
        });
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
        ll_invite = findViewById(R.id.com_main_invite);
        ll_nfts = findViewById(R.id.com_main_nfts);
        posting = findViewById(R.id.community_info_posting);
        title_name = findViewById(R.id.com_main_name);
        title_image = findViewById(R.id.com_main_titleimage);
        cv_profile = findViewById(R.id.com_main_profile);

        iv_join_yn = findViewById(R.id.com_main_join_checkiv);
        tv_join_yn = findViewById(R.id.com_main_join_checktv);

        swipeRefreshLayout = findViewById(R.id.com_main_refresh);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        GetComPosters();
                        GetComInfo();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
        
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
        ll_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                // tODO 링크 넣기 String으로 받아서 넣기
                String sendMessage = Config.api_server_addr + "/share/deep_community?id="+com_id;

//                String sendMessage = "10.0.2.2:3000/community?id="+com_id;
                intent.putExtra(Intent.EXTRA_TEXT, sendMessage);

                Intent shareIntent = Intent.createChooser(intent, "share");
                startActivity(shareIntent);
            }
        });

        // NFT 목록 보여주기
        ll_nfts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 가입함 or 가입가능 상태에 따라 보여주는 상태 변경...
                // 어떻게 해당 커뮤니티에 해당하는 nft를 가져올지 고민하기
                Toast.makeText(Community_MainPage.this, "준비 중...", Toast.LENGTH_SHORT).show();
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
                        .load(Config.cloudfront_addr+response.body().get(0).getProfile_file_name()) //URL, URI 등등 이미지를 받아올 경로
                        .centerCrop()
                        .into(title_image);

                // 커뮤니티 내 본인 정보 가져오기 (가입 상태)
                GetUserStatus();

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
                                responseResult.get(i).getMentioned_user_list(),
                                responseResult.get(i).getPost_type()
                        ));
                    }
                    post_adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<List<Kim_Com_post_Response>> call, Throwable t) {
                Log.e("오류태그", "리스폰스 실패_post");
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
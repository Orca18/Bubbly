package com.example.bubbly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.bubbly.controller.FragmentAdapter_SS;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.follower_Response;
import com.example.bubbly.retrofit.following_Response;
import com.example.bubbly.retrofit.user_Response;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SS_Profile extends AppCompatActivity {

    // 뒤로가기 시간
    private long backKeyPressedTime = 0;
    private Toast toast;

    // 바텀 메뉴
    LinearLayout bthome, btissue, btwallet, btmessage, btprofile;

    // 툴바, 사이드 메뉴
    androidx.appcompat.widget.Toolbar toolbar;
    TextView tv_title;
    ImageView bt_search;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView sidemenu;

    // 새로고침, 프로그레스바
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;

    // 탭 레이아웃
    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter_SS adapter;

    public static String uid;

    //프로필 데이터 보여주기
    ImageView iv_user_image,iv_chat;
    TextView tv_user_nick, tv_user_id, tv_user_intro, tv_following,tv_follower;
    LinearLayout bt_following, bt_unfollowing; //팔로잉 버튼


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_profile);

        // 유저 아이디 uid 받음
        Bundle extras = getIntent().getExtras();
        uid = extras.getString("user_id");
        System.out.println("user_id in post"+uid);
        Log.d("디버그태그", "user_id in post"+uid);

        // (기본) 리소스 ID 선언
        initiallize();
        // (추가) 탭 레이아웃
        tabInit();
        // 클릭 리스너 모음 - 스택 O
        clickListeners();
    }


    //프로필데이터 가져와서 디스플레이하기
    private void setProfileData(){
        //회원정보를 요청한다.
        ApiInterface api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<user_Response>> call_userInfo = api.selectUserInfo(uid);
        call_userInfo.enqueue(new Callback<List<user_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<user_Response>> call, @NonNull Response<List<user_Response>> response) {
                System.out.println(response.body());
                List<user_Response> responseResult = response.body();
                String user_id = responseResult.get(0).getUser_id();
                String login_id = responseResult.get(0).getLogin_id();
                String email_addr = responseResult.get(0).getEmail_addr();
                String novaland_account_addr = responseResult.get(0).getNovaland_account_addr();
                String phone_num = responseResult.get(0).getPhone_num();
                String user_nick = responseResult.get(0).getUser_nick();
                String self_info = responseResult.get(0).getSelf_info();
                String token = responseResult.get(0).getToken();
                String profile_file_name = "";
                if (responseResult.get(0).getProfile_file_name() != null && !responseResult.get(0).getProfile_file_name().equals("")) {
                    profile_file_name = "https://d2gf68dbj51k8e.cloudfront.net/" + responseResult.get(0).getProfile_file_name();
                }
                System.out.println(uid);
                //이미지. 닉네임, id, 자기소개, 팔로잉, 팔로워
                if (profile_file_name != null && !profile_file_name.equals("")) {
                    Glide.with(SS_Profile.this)
                            .load(profile_file_name)
                            .circleCrop()
                            .into(iv_user_image);
                } else {
                    //아무런 처리하지 않음. 레이아웃에 설정된 default 값 표시
                }

                if (user_nick != null && !user_nick.equals("")) {
                    tv_user_nick.setText(user_nick);
                } else {
                    tv_user_nick.setText(login_id);
                }

                tv_user_id.setText(login_id);

                if (self_info != null && !self_info.equals("")) {
                    tv_user_intro.setText(self_info);
                } else {
                    tv_user_intro.setText("");
                }
                //툴바 타이틀
                tv_title.setText(user_nick);

                //팔로워리스트 가져오기
                ApiInterface selectFollowerList_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<List<follower_Response>> call_follower = selectFollowerList_api.selectFollowerList(user_id);
                call_follower.enqueue(new Callback<List<follower_Response>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<follower_Response>> call, @NonNull Response<List<follower_Response>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<follower_Response> responseResult = response.body();
                            //만약 그 사람을 팔로잉하는 사람 중에 내가 있으면 팔로우 버튼을 언팔로우로 바꾼다.
                            for(int i = 0; i<responseResult.size(); i++){
                                System.out.println(responseResult.get(i).getFollower_id());
                                if(UserInfo.user_id.equals(responseResult.get(i).getFollower_id())){
                                    bt_following.setVisibility(View.GONE);
                                    bt_unfollowing.setVisibility(View.VISIBLE);
                                    break;
                                }
                            }
                            tv_follower.setText("" + responseResult.size());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<follower_Response>> call, @NonNull Throwable t) {
                        Log.e("에러", t.getMessage());
                    }
                });

                //팔로잉 리스트 가져오기
                ApiInterface selectFolloweeList_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<List<following_Response>> call_following = selectFolloweeList_api.selectFolloweeList(user_id);
                call_following.enqueue(new Callback<List<following_Response>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<following_Response>> call, @NonNull Response<List<following_Response>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<following_Response> responseResult = response.body();
                            tv_following.setText("" + responseResult.size());
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<following_Response>> call, @NonNull Throwable t) {
                        Log.e("에러", t.getMessage());
                    }
                });
            }
            @Override
            public void onFailure(@NonNull Call<List<user_Response>> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });
    }


    private void tabInit() {
        tabLayout = findViewById(R.id.profile_tab_layout);
        pager2 = findViewById(R.id.profile_view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        Log.d("디버그태그", "user_id in post22"+uid);
        adapter = new FragmentAdapter_SS(fm, getLifecycle(), uid);
        pager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("모든 글"));
        tabLayout.addTab(tabLayout.newTab().setText("답글"));
        tabLayout.addTab(tabLayout.newTab().setText("NFT"));
        tabLayout.addTab(tabLayout.newTab().setText("좋아요"));

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


    // ========================================================

    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = findViewById(R.id.ss_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //서치 버튼
        tv_title = findViewById(R.id.tv_title_ss_profile);
        // 툴바 안 검색 버튼
        bt_search = findViewById(R.id.bt_search_ss_profile);
        swipeRefreshLayout = findViewById(R.id.ss_profile_refresh);


        //프로필 데이터 보여주기
        iv_user_image = findViewById(R.id.iv_user_image_ss_profile);
        tv_user_nick = findViewById(R.id.tx_user_nick_ss_profile);
        tv_user_id = findViewById(R.id.ss_profile_id);
        tv_user_intro = findViewById(R.id.tv_self_info_ss_profile);
        tv_following = findViewById(R.id.tv_following_ss_profile);
        tv_follower = findViewById(R.id.tv_follower_ss_profile);

        bt_following = findViewById(R.id.bt_following_ss_profile);
        bt_unfollowing = findViewById(R.id.bt_unfollowing_ss_profile);
        iv_chat = findViewById(R.id.iv_chat_ss_profile);
    }



    // 클릭 이벤트 모음
    private void clickListeners() {
        bt_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface createFollowing_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = createFollowing_api.createFollowing(uid,UserInfo.user_id);
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {

                            Toast.makeText(getApplicationContext(), "팔로워로 추가되었습니다.", Toast.LENGTH_SHORT).show();
                            setProfileData();
                            swipeRefreshLayout.setRefreshing(false);
                            Log.e("팔로우 추가 성공", response.body().toString());

                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("팔로우 추가 에러", t.getMessage());
                    }
                });
            }
        });


        bt_unfollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiInterface deleteFollowing_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = deleteFollowing_api.deleteFollowing(uid,UserInfo.user_id);
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            Toast.makeText(getApplicationContext(), "팔로잉이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                            setProfileData();
                            swipeRefreshLayout.setRefreshing(false);
                            Log.e("팔로우 삭제 성공", response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("팔로우 삭제 에러", t.getMessage());
                    }
                });
            }
        });

        iv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //채팅요청
            }
        });

        //툴바 서치 버튼
        bt_search.setOnClickListener(v -> {
            Intent mIntent = new Intent(getApplicationContext(), SS_SearchMode.class);
            mIntent.putExtra("keyword", "");
            startActivity(mIntent);
        });

        // 리사이클러뷰 새로고침 인식
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        setProfileData();
                        //Toast.makeText(getApplicationContext(), "TODO 새로고침", Toast.LENGTH_SHORT).show();
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

    // 액티비티 종료 시, 애니메이션 효과 없애기
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    // 프래그먼트 어답터에서 Uid 받기 위해서 필요
    public String getUid() {
        return uid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("리줌");
        //프로필 데이터 표시
        setProfileData();
    }

}

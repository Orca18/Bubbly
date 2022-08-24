package com.example.bubbly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.bubbly.controller.FragmentAdapter;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.follower_Response;
import com.example.bubbly.retrofit.following_Response;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MM_Profile extends AppCompatActivity {

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
    NestedScrollView scrollView;

    // 탭 레이아웃
    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;

    String uid;

    CircleImageView myAccount;
    LinearLayout myActivity, myList, myCommunity;
    TextView settingOption, info, logout;
    View view;

    //프로필 데이터 보여주기
    ImageView iv_user_image;
    TextView tv_user_nick, tv_user_id, tv_user_intro, tv_following,tv_follower;
    LinearLayout bt_modify_profile; //프로필 수정 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_d_profile);

        // 리소스 ID 선언
        initialize();
        // 탭 레이아웃
        tabInit();
        // 바텀 메뉴 - 스택 X 액티비티 이동 (TODO 바텀 내비게이션으로 변경하는 작업)
        bottomNavi();
        // 클릭 리스너 모음 - 스택 O
        clickListeners();
        // 내비 터치
        NaviTouch();



    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("스타트");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("리줌");
        //프로필 데이터 표시
        setProfileData();
    }

    private void tabInit() {
        tabLayout = findViewById(R.id.profile_tab_layout);
        pager2 = findViewById(R.id.profile_view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle(), uid);
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
    private void initialize() {
        // 툴바
        toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.profile_navigation_view);
        sidemenu = findViewById(R.id.profile_sidemenu);
        swipeRefreshLayout = findViewById(R.id.profile_refresh);
        scrollView = findViewById(R.id.text_scrollview);

        // 내비 안 메뉴
        view = navigationView.getHeaderView(0);
        myAccount = view.findViewById(R.id.navi_header_profileimg);
        myActivity = view.findViewById(R.id.navi_header_myActivity);
        myList = view.findViewById(R.id.navi_header_myList);
        myCommunity = view.findViewById(R.id.navi_header_myCommunity);
        settingOption = view.findViewById(R.id.navi_header_setting_option);
        info = view.findViewById(R.id.navi_header_info);
        logout = view.findViewById(R.id.navi_header_logout);

        // 바텀 메뉴
        bthome = findViewById(R.id.profile_tohome);
        btissue = findViewById(R.id.profile_toissue);
        btmessage = findViewById(R.id.profile_tomessage);
        btprofile = findViewById(R.id.profile_toprofile);
        btwallet = findViewById(R.id.profile_towallet);

        //프로필 데이터 보여주기
        iv_user_image = findViewById(R.id.iv_user_image);
        tv_user_nick = findViewById(R.id.tv_user_nick);
        tv_user_id = findViewById(R.id.tv_user_id);
        tv_user_intro = findViewById(R.id.tv_user_intro);
        tv_following = findViewById(R.id.tv_following);
        tv_follower = findViewById(R.id.tv_follower);

        bt_modify_profile = findViewById(R.id.bt_modify_profile);
    }

    //프로필데이터 가져와서 디스플레이하기
    private void setProfileData(){
        //이미지. 닉네임, id, 자기소개, 팔로잉, 팔로워
        if(UserInfo.profile_file_name!=null && !UserInfo.profile_file_name.equals("")){
            Glide.with(MM_Profile.this)
                    .load(UserInfo.profile_file_name)
                    .circleCrop()
                    .into(iv_user_image);
        }else{
            //아무런 처리하지 않음. 레이아웃에 설정된 default 값 표시
        }

        if(UserInfo.user_nick!=null && !UserInfo.user_nick.equals("")){
            tv_user_nick.setText(UserInfo.user_nick);
        }else{
            tv_user_nick.setText(UserInfo.login_id);
        }

        tv_user_id.setText(UserInfo.login_id);

        if(UserInfo.self_info!=null && !UserInfo.self_info.equals("")){
            tv_user_intro.setText(UserInfo.self_info);
        }else{
            //아무런 처리하지 않음. 레이아웃에 설정된 default 값 표시
        }

        //팔로워리스트 가져오기
        ApiInterface selectFollowerList_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<follower_Response>> call_follower = selectFollowerList_api.selectFollowerList(UserInfo.user_id);
        call_follower.enqueue(new Callback<List<follower_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<follower_Response>> call, @NonNull Response<List<follower_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<follower_Response> responseResult = response.body();
                    tv_follower.setText(""+responseResult.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<follower_Response>> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });

        //팔로잉 리스트 가져오기
        ApiInterface selectFolloweeList_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<following_Response>> call_following = selectFolloweeList_api.selectFolloweeList(UserInfo.user_id);
        call_following.enqueue(new Callback<List<following_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<following_Response>> call, @NonNull Response<List<following_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<following_Response> responseResult = response.body();
                    tv_following.setText(""+responseResult.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<following_Response>> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });





    }

    private void NaviTouch() {

        // 내비뷰 메뉴 레이아웃에 직접 구현
//       CircleImageView myAccount;
//       LinearLayout myActivity, myList, myCommunity;
//       TextView settingOption, info, logout;
        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "사이드메뉴 닫기 TODO", Toast.LENGTH_SHORT).show();
            }
        });
        myActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "TODO 보상 체계 구현 (with 지갑)", Toast.LENGTH_SHORT).show();
            }
        });
        myList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "겉멋", Toast.LENGTH_SHORT).show();
            }
        });
        myCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), Community_Home_Feeds.class);
                startActivity(mIntent);
            }
        });
        settingOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getApplicationContext(), SS_Setting.class);
                startActivity(settingIntent);
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "고객센터", Toast.LENGTH_SHORT).show();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(getApplicationContext(), LL_Login.class);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                startActivity(toLogin);
                finish();
                Toast.makeText(getApplicationContext(), "로그아웃", Toast.LENGTH_SHORT).show();
            }
        });


    }

    // 바텀 메뉴 클릭
    private void bottomNavi() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.profile_tohome:
                        Intent mIntent1 = new Intent(getApplicationContext(), MM_Home.class);
                        mIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent1);
                        finish();
                        break;

                    case R.id.profile_toissue:
                        Intent mIntent2 = new Intent(getApplicationContext(), MM_Issue.class);
                        mIntent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent2);
                        finish();
                        break;

                    case R.id.profile_tomessage:
                        Intent mIntent3 = new Intent(getApplicationContext(), MM_Message.class);
                        mIntent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent3);
                        finish();
                        break;

                    case R.id.profile_toprofile:
                        break;

                    case R.id.profile_towallet:
                        Intent mIntent4 = new Intent(getApplicationContext(), MM_Wallet.class);
                        mIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
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



        //팔로잉 보기 (인텐트로 넘기지 않고 Following class에서 svr로 새로 요청해서 받을 것)
        tv_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), Following.class);
                startActivity(mIntent);
            }
        });

        //팔로워 보기
        tv_follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), Follower.class);
                startActivity(mIntent);
            }
        });


        //프로필 수정하기
        bt_modify_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), ModifyProfile.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mIntent);
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

    // 프래그먼트 어답터에서 Uid 받기 위해서 필요
    public String getUid() {
        return uid;
    }



}

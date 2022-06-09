package com.example.novarand_sns;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.novarand_sns.controller.FragmentAdapter;
import com.example.novarand_sns.controller.WalletFragmentAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class MM_Wallet extends AppCompatActivity {

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
    ProgressBar progressBar;
    ScrollView scrollView;

    // 탭 레이아웃
    TabLayout tabLayout;
    ViewPager2 pager2;
    WalletFragmentAdapter adapter;
    String uid;

    CircleImageView myAccount;
    LinearLayout myActivity, myList, myCommunity;
    TextView settingOption, info, logout;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_e_wallet);

        // 리소스 ID 선언
        initiallize();
        // 바텀 메뉴 - 스택 X 액티비티 이동 (TODO 바텀 내비게이션으로 변경하는 작업)
        bottomNavi();
        // 클릭 리스너 모음 - 스택 O
        clickListeners();
        // 내비 터치
        NaviTouch();

        tabInit();
    }


    // ========================================================

    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = findViewById(R.id.wallet_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.wallet_navigation_view);
        sidemenu = findViewById(R.id.wallet_sidemenu);
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
        bthome = findViewById(R.id.wallet_tohome);
        btissue = findViewById(R.id.wallet_toissue);
        btmessage = findViewById(R.id.wallet_tomessage);
        btprofile = findViewById(R.id.wallet_toprofile);
        btwallet = findViewById(R.id.wallet_towallet);

    }

    // 바텀 메뉴 클릭
    private void bottomNavi() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.wallet_tohome:
                        Intent mIntent1 = new Intent(getApplicationContext(), MM_Home.class);
                        mIntent1.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent1);
                        finish();
                        break;

                    case R.id.wallet_toissue:
                        Intent mIntent2 = new Intent(getApplicationContext(), MM_Issue.class);
                        mIntent2.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent2);
                        finish();
                        break;

                    case R.id.wallet_tomessage:
                        Intent mIntent3 = new Intent(getApplicationContext(), MM_Message.class);
                        mIntent3.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent3);
                        finish();
                        break;

                    case R.id.wallet_toprofile:
                        Intent mIntent4 = new Intent(getApplicationContext(), MM_Profile.class);
                        mIntent4.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent4);
                        finish();
                        break;

                    case R.id.wallet_towallet:
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

    // 내비 터치치
    private void NaviTouch() {

        // 내비뷰 메뉴 레이아웃에 직접 구현
//       CircleImageView myAccount;
//       LinearLayout myActivity, myList, myCommunity;
//       TextView settingOption, info, logout;
        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent3 = new Intent(getApplicationContext(), MM_Profile.class);
                mIntent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mIntent3);
                finish();            }
        });
        myActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "TODO 보상 체계 구현 (with 지갑)",Toast.LENGTH_SHORT).show();
            }
        });
        myList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "겉멋",Toast.LENGTH_SHORT).show();
            }
        });
        myCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), Community_Home.class);
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
                Toast.makeText(getApplicationContext(), "고객센터",Toast.LENGTH_SHORT).show();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(getApplicationContext(), LL_Login.class);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                startActivity(toLogin);
                finish();
                Toast.makeText(getApplicationContext(), "로그아웃",Toast.LENGTH_SHORT).show();            }
        });


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




    private void tabInit() {
        tabLayout = findViewById(R.id.wallet_tab_layout);
        pager2 = findViewById(R.id.wallet_view_pager2);

        FragmentManager fm = getSupportFragmentManager();
        adapter = new WalletFragmentAdapter(fm, getLifecycle(), uid);
        pager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("활동"));
        tabLayout.addTab(tabLayout.newTab().setText("광고"));
        tabLayout.addTab(tabLayout.newTab().setText("활동 내역"));

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

    // 프래그먼트 어답터에서 Uid 받기 위해서 필요
    public String getUid(){
        return uid;
    }
}

package com.example.bubbly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bubbly.controller.Ranking_Adapter;
import com.example.bubbly.controller.Searched_Adapter_Callback;
import com.example.bubbly.model.Ranking_Item;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MM_Issue extends AppCompatActivity {

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

    ListView listView;
    ArrayList<Ranking_Item> rankingList;
    ScrollView scrollView;

    // 카테고리 (임시 1.종합 / 2.잡담 / 3.커뮤니티)
    LinearLayout cat1, cat2, cat3;

    CircleImageView myAccount;
    LinearLayout myActivity, myList, myCommunity;
    TextView settingOption, info, logout;

    LinearLayout search;
    View view;

    Searched_Adapter_Callback callback = new Searched_Adapter_Callback() {
        @Override
        public void updateListRecentlySearched(String keyword) {
            //검색 키워드 저장하기 - 서버
            ApiInterface api = ApiClient.getApiClient(getApplicationContext()).create(ApiInterface.class);
            Call<String> call = api.createSerarchText(UserInfo.user_id,keyword);
            call.enqueue(new Callback<String>()
            {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        ArrayList<String> recentlySearchedList = new ArrayList<String>();
                        SharedPreferences preferences = getSharedPreferences("novarand",MODE_PRIVATE);
                        String recentlySearched = preferences.getString("recentlySearched", "");
                        try {
                            JSONArray jsonArray = new JSONArray(recentlySearched);
                            for(int i=0;i<jsonArray.length();i++){
                                String recentlySearchedItem = jsonArray.getString(i);
                                recentlySearchedList.add(recentlySearchedItem);
                            }
                            //리스트뷰 역순으로 보이게 하기(최신 검색어가 맨 상위로)
                            Collections.reverse(recentlySearchedList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //역순으로 보여주었던 것을 저장시 올바른 순서로 저장하기 위해서 다시 역순(원래 순서)로 재배치
                        Collections.reverse(recentlySearchedList);
                        //검색 키워드 저장하기 - 로컬 (화면 이동 후 업데이트)
                        //만약 동일 키워드가 이미 존재하면 해당 키워드를 지우고 새로 추가
                        for(int i =0;i<recentlySearchedList.size();i++){
                            if(recentlySearchedList.get(i).equals(keyword)){
                                recentlySearchedList.remove(i);
                            }
                        }
                        recentlySearchedList.add(keyword);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("recentlySearched", recentlySearchedList.toString());
                        editor.commit();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("에러", t.getMessage());
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_b_issue);

        // 리소스 ID 선언
        initiallize();
        // 바텀 메뉴 - 스택 X 액티비티 이동
        bottomNavi();
        // 클릭 리스너 모음 - 스택 O
        clickListeners();
        // 내비 터치
        NaviTouch();
    }


    // ========================================================

    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = findViewById(R.id.issue_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.issue_navigation_view);
        sidemenu = findViewById(R.id.issue_sidemenu);
        swipeRefreshLayout = findViewById(R.id.issue_refresh);
        listView = findViewById(R.id.issue_ranking_listview);

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
        bthome = findViewById(R.id.issue_tohome);
        btissue = findViewById(R.id.issue_toissue);
        btmessage = findViewById(R.id.issue_tomessage);
        btprofile = findViewById(R.id.issue_toprofile);
        btwallet = findViewById(R.id.issue_towallet);



        scrollView = findViewById(R.id.issue__scrollview);

        search = findViewById(R.id.action_search);


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
                finish();
            }
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
                //Toast.makeText(getApplicationContext(), "겉멋",Toast.LENGTH_SHORT).show();
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

    // 바텀 메뉴 클릭
    private void bottomNavi() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.issue_tohome:
                        Intent mIntent1 = new Intent(getApplicationContext(), MM_Home.class);
                        mIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent1);
                        finish();
                        break;

                    case R.id.issue_toissue:
                        break;

                    case R.id.issue_tomessage:
                        Intent mIntent2 = new Intent(getApplicationContext(), MM_Message.class);
                        mIntent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent2);
                        finish();
                        break;

                    case R.id.issue_toprofile:
                        Intent mIntent3 = new Intent(getApplicationContext(), MM_Profile.class);
                        mIntent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mIntent3);
                        finish();
                        break;

                    case R.id.issue_towallet:
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
                        RankingList();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        // 카테고리들
        cat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), SS_KeywordResult.class);
                mIntent.putExtra("keyword", "종합");
                startActivity(mIntent);
            }
        });

        cat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issacToast("간단한 유머글? - 카테고리 상세 분할은 다음에...");
                Intent mIntent2 = new Intent(getApplicationContext(), SS_KeywordResult.class);
                mIntent2.putExtra("keyword", "NFT");
                startActivity(mIntent2);
            }

        });

        cat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issacToast("공개 커뮤니티 글 추천? - 카테고리 상세 분할은 다음에...");
                Intent mIntent2 = new Intent(getApplicationContext(), SS_KeywordResult.class);
                mIntent2.putExtra("keyword", "커뮤니티");
                startActivity(mIntent2);
            }
        });

        search.setOnClickListener(v -> {
            Intent mIntent = new Intent(getApplicationContext(), SS_SearchMode.class);
            mIntent.putExtra("keyword","");
            startActivity(mIntent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        });

    }

    private void issacToast(String msg) {
        Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        RankingList();
    }

    // 액티비티 종료 시, 애니메이션 효과 없애기
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }


    // 랭킹 리스트 채우기
    private void RankingList() {
        rankingList = new ArrayList<>();
        // 리스트뷰 어답터 - 리스트뷰 연결
        final Ranking_Adapter adapter = new Ranking_Adapter(this, rankingList, callback);
        listView.setAdapter(adapter);

        //실시간 트랜드 데이터 가져오기
        //현재시간
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date now = new Date();
        String strNow = sdfDate.format(now);

        ApiInterface api = ApiClient.getApiClient(this).create(ApiInterface.class);
        Call<String> call = api.selectRealTimeTrends(strNow);
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if(response.body()!=null){
                    try {
                        JSONArray jsonArray = new JSONArray(response.body());
                        for(int i = 0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int rank = i+1;
                            String keyword = jsonObject.getString("name");
                            int count = jsonObject.getInt("cnt");
                            Ranking_Item ri = new Ranking_Item(rank,keyword,count);
                            rankingList.add(ri);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("실시간 검색어 트랜드 에러", t.getMessage());
            }
        });
    }




}

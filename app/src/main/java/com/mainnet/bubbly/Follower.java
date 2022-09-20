package com.mainnet.bubbly;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.controller.Follower_Adapter;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.follower_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Follower extends AppCompatActivity {

    // 탭 레이아웃
//    TabLayout tabLayout;
//    ViewPager2 pager2;
//    FollowingFragmentAdapter adapter;

    String uid;
    TextView tv_user_nick;

    Follower_Adapter follower_adapter;
    ArrayList<follower_Response> followerList;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;

//    SharedPreferences preferences;
//    String user_id;


    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);


        toolbar = findViewById(R.id.follower_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        tv_user_nick = findViewById(R.id.tv_user_nick);



        recyclerView = findViewById(R.id.tab_recyclerview);
        linearLayoutManager = new LinearLayoutManager(Follower.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        recyclerView.addItemDecoration(new DividerItemDecoration(Follower.this.getApplicationContext(), DividerItemDecoration.VERTICAL));

        followerList = new ArrayList<>();
        follower_adapter = new Follower_Adapter(Follower.this.getApplicationContext(), followerList);
        recyclerView.setAdapter(follower_adapter);
        follower_adapter.notifyDataSetChanged();
//        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

        ApiInterface selectFollowerList_api = ApiClient.getApiClient(this).create(ApiInterface.class);
        Call<List<follower_Response>> call = selectFollowerList_api.selectFollowerList(UserInfo.user_id);
        call.enqueue(new Callback<List<follower_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<follower_Response>> call, @NonNull Response<List<follower_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<follower_Response> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){
                        followerList.add(new follower_Response(responseResult.get(i).getFollower_id(),
                                responseResult.get(i).getNick_name(),responseResult.get(i).getProfile_file_name(),
                                responseResult.get(i).getLogin_id()
                                ));
                    }
                    follower_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<follower_Response>> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });

//        tabInit(); // 프래그먼트 탭

    } // onCreate 닫는곳

//    private void tabInit() {
//        tabLayout = findViewById(R.id.following_tab_layout);
//        pager2 = findViewById(R.id.following_view_pager2);
//
//        FragmentManager fm = getSupportFragmentManager();
//        adapter = new FollowingFragmentAdapter(fm, getLifecycle(), uid);
//        pager2.setAdapter(adapter);
//
//        tabLayout.addTab(tabLayout.newTab().setText("팔로워"));
//        tabLayout.addTab(tabLayout.newTab().setText("팔로잉"));
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                pager2.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
//
//        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                tabLayout.selectTab(tabLayout.getTabAt(position));
//            }
//        });
//
//    }


    // 프래그먼트 어답터에서 Uid 받기 위해서 필요
    public String getUid(){
        return uid;
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

}
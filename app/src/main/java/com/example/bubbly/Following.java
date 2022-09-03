package com.example.bubbly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bubbly.controller.FollowingFragmentAdapter;
import com.example.bubbly.controller.Following_Adapter;
import com.example.bubbly.controller.Following_Adapter_Callback;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.following_Response;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Following extends AppCompatActivity {

    // 탭 레이아웃
//    TabLayout tabLayout;
//    ViewPager2 pager2;
//    FollowingFragmentAdapter adapter;

    String uid;
    ImageView iv_back;
    TextView tv_user_nick;

    Following_Adapter following_adapter;
    ArrayList<following_Response> followingList;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        iv_back = findViewById(R.id.iv_back);
        tv_user_nick = findViewById(R.id.tv_user_nick);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.tab_recyclerview);
        linearLayoutManager = new LinearLayoutManager(Following.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        recyclerView.addItemDecoration(new DividerItemDecoration(Following.this.getApplicationContext(), DividerItemDecoration.VERTICAL));

        followingList = new ArrayList<>();
        following_adapter = new Following_Adapter(Following.this.getApplicationContext() , followingList,new Following_Adapter_Callback() {
            @Override
            public void unfollow(int position) {
                //어댑터 item내 unfollow click결과 수신용 콜백
                followingList.remove(position);
                following_adapter.notifyDataSetChanged();
            }
        } );
        recyclerView.setAdapter(following_adapter);
        ApiInterface selectFolloweeList_api = ApiClient.getApiClient(this).create(ApiInterface.class);
        Call<List<following_Response>> call = selectFolloweeList_api.selectFolloweeList(UserInfo.user_id);
        call.enqueue(new Callback<List<following_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<following_Response>> call, @NonNull Response<List<following_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<following_Response> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){
                        followingList.add(new following_Response(responseResult.get(i).getFollowee_id(),
                                responseResult.get(i).getNick_name(),responseResult.get(i).getProfile_file_name(),
                                responseResult.get(i).getLogin_id()));
                    }
                    following_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<following_Response>> call, @NonNull Throwable t)
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

}
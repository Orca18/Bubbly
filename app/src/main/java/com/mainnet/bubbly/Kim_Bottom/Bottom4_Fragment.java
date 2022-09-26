package com.mainnet.bubbly.Kim_Bottom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.mainnet.bubbly.Follower;
import com.mainnet.bubbly.Following;
import com.mainnet.bubbly.MainActivity;
import com.mainnet.bubbly.ModifyProfile;
import com.mainnet.bubbly.Post_Create;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.controller.FragmentAdapter;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.follower_Response;
import com.mainnet.bubbly.retrofit.following_Response;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Bottom4_Fragment extends Fragment {

    // ★프래그먼트 View그룹
    private ViewGroup view;
    // 툴바
    androidx.appcompat.widget.Toolbar toolbar;
    // 툴바 안에 버튼 목록 (사이드메뉴 - 알림, 작성)
    ImageView sidemenu, alarm, creating;
    // 현재 유저 uid
    SharedPreferences preferences;
    String user_id;
    // 새로고침
    SwipeRefreshLayout swipeRefreshLayout;
    // 탭 레이아웃
    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;

    //프로필 데이터 보여주기
    ImageView iv_user_image;
    TextView tv_user_nick, tv_user_id, tv_user_intro, tv_following,tv_follower;
    LinearLayout bt_modify_profile; //프로필 수정 버튼

    private static String uid;

    // 게시글 작성 페이지로 이동
    ImageView toCreate;

    LinearLayout ll_following, ll_follower;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.bottom4_frame, container, false);

        // 리소스 ID 선언
        initialize();
        // 탭 레이아웃
        tabInit();
        // 클릭 리스너 모음 - 스택 O
        clickListeners();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("스타트");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("리줌");
        //프로필 데이터 표시
        setProfileData();
    }


    private void tabInit() {
        tabLayout = view.findViewById(R.id.profile_tab_layout);
        pager2 = view.findViewById(R.id.profile_view_pager2);


        FragmentManager fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
        // adapter = new FragmentAdapter(fm, getLifecycle(), uid);
        // 에러 : FragmentManager is already executing transactions
        // 바텀네비게이션(프래그먼트) 안에  넣으려면, 위 코드 -> getChildFragmentManager() 를 사용한다.
        adapter = new FragmentAdapter(getChildFragmentManager(), getLifecycle(), uid);
        pager2.setAdapter(adapter);
        // 에러 : Fragment no longer exists for key
        pager2.setSaveEnabled(false);

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
    // 리소스 아이디 선언
    private void initialize() {
        // 툴바
        toolbar = view.findViewById(R.id.profile_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        sidemenu = view.findViewById(R.id.profile_sidemenu);
        swipeRefreshLayout = view.findViewById(R.id.profile_refresh);
//        scrollView = findViewById(R.id.text_scrollview);

        //프로필 데이터 보여주기
        iv_user_image = view.findViewById(R.id.iv_user_image);
        tv_user_nick = view.findViewById(R.id.tv_user_nick);
        tv_user_id = view.findViewById(R.id.tv_user_id);
        tv_user_intro = view.findViewById(R.id.tv_user_intro);
        tv_following = view.findViewById(R.id.tv_following);
        tv_follower = view.findViewById(R.id.tv_follower);

        bt_modify_profile = view.findViewById(R.id.bt_modify_profile);

        // 게시글 작성
        toCreate = view.findViewById(R.id.bottom4_toCreate);


        ll_following = view.findViewById(R.id.ll_toFollow);
        ll_follower = view.findViewById(R.id.ll_toFollower);
    }

    //프로필데이터 가져와서 디스플레이하기
    private void setProfileData(){
        //이미지. 닉네임, id, 자기소개, 팔로잉, 팔로워
        if(UserInfo.profile_file_name!=null && !UserInfo.profile_file_name.equals("")){
            Glide.with(getContext())
                    .load(UserInfo.profile_file_name)
                    .circleCrop()
                    .into(iv_user_image);
        }else{
            Glide.with(getContext())
                    .load(R.drawable.blank_profile)
                    .circleCrop()
                    .into(iv_user_image);
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
        ApiInterface selectFollowerList_api = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
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
        ApiInterface selectFolloweeList_api = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
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

    // 클릭 이벤트 모음
    private void clickListeners() {

        // 좌측 상단 메뉴 버튼
        sidemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        // 리사이클러뷰 새로고침 인식
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // 새로고침
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });



        //팔로잉 보기 (인텐트로 넘기지 않고 Following class에서 svr로 새로 요청해서 받을 것)
        ll_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getContext(), Following.class);
                startActivity(mIntent);
            }
        });

        //팔로워 보기
        ll_follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getContext(), Follower.class);
                startActivity(mIntent);
            }
        });


        //프로필 수정하기
        bt_modify_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getContext(), ModifyProfile.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mIntent);
            }
        });

        toCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tocreating = new Intent(getContext(), Post_Create.class);
                tocreating.putExtra("com_id", "0");
                tocreating.putExtra("com_name", "내 피드");
                startActivity(tocreating);
            }
        });
    }


    // 프래그먼트 어답터에서 Uid 받기 위해서 필요
    public static String getUid() {
        return uid;
    }
}

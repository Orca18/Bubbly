package com.example.bubbly.Kim_Bottom;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bubbly.MainActivity;
import com.example.bubbly.Option_Notice_List;
import com.example.bubbly.Post_Create;
import com.example.bubbly.R;
import com.example.bubbly.controller.Post_Adapter;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.post_Response;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Bottom1_Fragment extends Fragment {

    // ★프래그먼트 View그룹
    private ViewGroup view;
    // 툴바
    androidx.appcompat.widget.Toolbar toolbar;
    // 툴바 안에 버튼 목록 (사이드메뉴 - 알림, 작성)
    ImageView sidemenu, alarm, creating;
    // 현재 유저 uid
    SharedPreferences preferences;
    String user_id;
    // 리사이클러뷰 (게시물)
    RecyclerView recyclerView;
    Post_Adapter post_adapter;
    ArrayList<post_Response> postList;
    LinearLayoutManager linearLayoutManager;
    private Parcelable recyclerViewState; // 위치
    SwipeRefreshLayout swipeRefreshLayout; // 새로고침

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.bottom1_frame, container, false);

        initialize(); // 1. 리소스 ID 선언
        clickListeners(); // 2. 클릭 리스너
        selectPost_Followee_Communit(); // 3. 나와 팔로워, 속한 커뮤니티의 게시물 조회 api


        return view;
    }

    private void initialize() {
        toolbar = view.findViewById(R.id.home_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        sidemenu = view.findViewById(R.id.home_sidemenu);
        creating = view.findViewById(R.id.home_creating);
        alarm = view.findViewById(R.id.home_alarm);

        swipeRefreshLayout = view.findViewById(R.id.home_refresh);
        recyclerView = view.findViewById(R.id.home_recyclerView);


        preferences = getActivity().getSharedPreferences("novarand",MODE_PRIVATE);
        user_id = preferences.getString("user_id", "");
    }


    // 클릭 이벤트 모음
    private void clickListeners() {
        // 작성
        creating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tocreating = new Intent(getContext(), Post_Create.class);
                tocreating.putExtra("com_id", "0");
                tocreating.putExtra("com_name", "내 피드");
                startActivity(tocreating);
            }
        });

        // 알림
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarm = new Intent(getContext(), Option_Notice_List.class);
                startActivity(alarm);
            }
        });

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
                        selectPost_Followee_Communit();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }




    private void selectPost_Followee_Communit() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        postList = new ArrayList<>();
        post_adapter = new Post_Adapter(getContext(), this.postList, getContext(),getActivity());
        recyclerView.setAdapter(post_adapter);
        post_adapter.notifyDataSetChanged();

        preferences = getActivity().getSharedPreferences("novarand", MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값
        ApiInterface api = ApiClient.getApiClient(requireActivity()).create(ApiInterface.class);
        Call<List<post_Response>> call = api.selectPostMeAndFolloweeAndCommunity(user_id);
        call.enqueue(new Callback<List<post_Response>>() {
            @Override
            public void onResponse(@NonNull Call<List<post_Response>> call, @NonNull Response<List<post_Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
//                    progressBar.setVisibility(View.GONE);
                    List<post_Response> responseResult = response.body();
                    for (int i = 0; i < responseResult.size(); i++) {
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
                                responseResult.get(i).getMentioned_user_list(),
                                responseResult.get(i).getCommunity_id(),
                                responseResult.get(i).getLogin_id(),
                                responseResult.get(i).getPost_type()));
                    }
                    post_adapter.notifyDataSetChanged();
                }

                Log.d("디버그태그", "Status:"+response);

            }

            @Override
            public void onFailure(@NonNull Call<List<post_Response>> call, @NonNull Throwable t) {
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
            }
        });
    }


}



package com.example.bubbly.Kim_Bottom;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bubbly.MainActivity;
import com.example.bubbly.R;
import com.example.bubbly.controller.Messages_Adapter;
import com.example.bubbly.model.Chat_Room_Info;

import java.util.ArrayList;
import java.util.List;

public class Bottom3_Fragment extends Fragment {

    // ★프래그먼트 View그룹
    private ViewGroup view;
    // 툴바 (& 버튼)
    androidx.appcompat.widget.Toolbar toolbar;
    ImageView sidemenu, sidemenubtnChatRoomAdd, btnChatOption;
    // 새로고침, 프로그레스바
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    // 리사이클러 뷰
    private Messages_Adapter adapter;
    private List<Chat_Room_Info> chatRoomList;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;
    // 유저 정보
    SharedPreferences preferences;
    String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 결과값을 가지고 있는 인텐트 설정 setUpStartActForrResult();
        view = (ViewGroup) inflater.inflate(R.layout.bottom3_frame, container, false);

        // 리소스 ID 선언
        initiallize();
        clickListeners();


            return view;
    }



    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = view.findViewById(R.id.message_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        // 사이드바 메뉴를 열기위한 변수
        sidemenu = view.findViewById(R.id.message_sidemenu);
        // 채팅방 추가 버튼
        sidemenubtnChatRoomAdd= view.findViewById(R.id.message_chat_room_add);
        // 설정
        btnChatOption = view.findViewById(R.id.message_option);
        
        // 리사이클러뷰를 새로고침할 수 있게하기 위한 레이아웃
        swipeRefreshLayout = view.findViewById(R.id.message_refresh);
        // 채팅방리스트를 저장하기 위한 리사이클러뷰
        recyclerView = view.findViewById(R.id.message_recyclerView);
        
        // 채팅방 리스트
        this.chatRoomList = new ArrayList();
        


        preferences = getActivity().getSharedPreferences("novarand",MODE_PRIVATE);
        userId = preferences.getString("user_id", "");
    }

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
                        Toast.makeText(getContext(), "TODO 새로고침", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }
}

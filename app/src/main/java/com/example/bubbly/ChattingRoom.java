package com.example.bubbly;

import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Parcelable;

import com.example.bubbly.controller.Chat_Adapter;
import com.example.bubbly.model.Chat_Item;

import java.util.ArrayList;
import java.util.List;

public class ChattingRoom extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;

    RecyclerView recyclerView;

    private Chat_Adapter adapter;
    private List<Chat_Item> chatList;
    private Parcelable recyclerViewState;

    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_room);

        toolbar = findViewById(R.id.chatroom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.chatroom_recyclerview);

        // 리사이클러뷰 데이터 가져오기
        loadrecycler();


    }

    // 데이터 http 요청
    private void loadrecycler() {
        // 쓰레드 http 요청 & run 데이터 넣기
        fillList();
    }

    // loadrecycler 에서 요청/응답 받은 데이터 채워넣기
    private void fillList() {
        this.chatList = new ArrayList();

        // TODO 시간 계산 → String 으로 넣어주기
        for (int i = 0; i < 10; i++) {
            this.chatList.add(new Chat_Item("안녕하세요요 " + i + "트"));
            this.chatList.add(new Chat_Item("안녕 못해요~~ " + i + "트"));
        }
        
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        // 채팅방의 경우 데이터를 보여줄 때 가장 최신 데이터(밑에있는 데이터!)부터 보여줘야 하므로 해당 설정을 적용시켜준다.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        this.adapter = new Chat_Adapter(getApplicationContext(), this.chatList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(this.adapter);

        recyclerView.scrollToPosition(position);

        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        recyclerView.addOnScrollListener(onScrollListener);
    }

    // 리사이클러뷰의 스크롤이 최상단에 도달했을 때 새로운 채팅 데이터를 가져오기 위함!
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {


            if (recyclerView.computeVerticalScrollOffset() == 0) {
                // 최상단
                Toast.makeText(getApplicationContext(), "상단 인식 테스트 TODO 페이징", Toast.LENGTH_SHORT).show();
                position = position + (chatList.size()-1);
                Toast.makeText(getApplicationContext(), "dd"+position, Toast.LENGTH_SHORT).show();
            }

        }
    };

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
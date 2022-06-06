package com.example.novarand_sns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;

import com.example.novarand_sns.controller.Reply_Adapter;
import com.example.novarand_sns.model.Reply_Item;

import java.util.ArrayList;
import java.util.List;

public class SS_PostDetail extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView; // TODO replys 로 할 수 있긴하지만, 그냥 나중에 shift + f6 사용하던지...

    private Reply_Adapter adapter;
    private List<Reply_Item> exampleList;
    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_details);

        initiallize();
        LoadRecyclerView();
    }

    private void LoadRecyclerView() {
        FillList();
    }

    private void FillList() {
        this.exampleList = new ArrayList();

        // TODO 시간 계산 → String 으로 넣어주기
        // 파싱해서 넣어주기
        for (int i = 0; i < 10; i++) {
            this.exampleList.add(new Reply_Item("안녕하세요요 " + i + "트"));
        }

        // 리사이클러뷰 셋업
        SetupRecyclerView();
    }

    private void SetupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        this.adapter = new Reply_Adapter(getApplicationContext(), this.exampleList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(this.adapter);

        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        //바닥 도달 리스너
        recyclerView.addOnScrollListener(onScrollListener);
    }

    // 바닥에 도달했을 때...
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
            if (lastVisibleItemPosition == itemTotalCount) {
                //TODO 바닥 작업
//                progressBar.setVisibility(View.VISIBLE);
//                loadMoreData();
            }
        }
    };

    private void initiallize() {
        toolbar = findViewById(R.id.post_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.post_details_recyclerview);
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
package com.mainnet.bubbly;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.controller.Noti_Adapter;
import com.mainnet.bubbly.model.Noti_Item;

import java.util.ArrayList;
import java.util.List;

public class Option_Notice_List extends AppCompatActivity {

    Toolbar toolbar;

    private Noti_Adapter adapter;
    private List<Noti_Item> exampleList;
    RecyclerView recyclerView;
    private Parcelable recyclerViewState;

    private ItemTouchHelper mItemTouchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noti_list);

        initiallize();

        loadrecycler();
    }

    private void initiallize() {
        toolbar = findViewById(R.id.toolbar_noti);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("알림");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.noti_recyclerView);

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


    // 데이터 http 요청
    private void loadrecycler() {
        // 쓰레드 http 요청 & run 데이터 넣기
        fillList();
    }

    // loadrecycler 에서 요청/응답 받은 데이터 채워넣기
    private void fillList() {
        this.exampleList = new ArrayList();


        this.exampleList.add(new Noti_Item("알림 추가한 이름"));
        this.exampleList.add(new Noti_Item("알림 추가한 이름"));
        this.exampleList.add(new Noti_Item("알림 추가한 이름"));
        this.exampleList.add(new Noti_Item("알림 추가한 이름"));
        this.exampleList.add(new Noti_Item("알림 추가한 이름"));
        this.exampleList.add(new Noti_Item("알림 추가한 이름"));
        this.exampleList.add(new Noti_Item("알림 추가한 이름"));
        this.exampleList.add(new Noti_Item("알림 추가한 이름"));


        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        this.adapter = new Noti_Adapter(getApplicationContext(), this.exampleList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(this.adapter);

        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

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
}
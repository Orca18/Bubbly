package com.mainnet.bubbly;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.controller.Memberlists_Adapter;
import com.mainnet.bubbly.kim_util_test.Kim_ApiClient;
import com.mainnet.bubbly.kim_util_test.Kim_ApiInterface;
import com.mainnet.bubbly.kim_util_test.Kim_Com_Members_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Community_Memberlist extends AppCompatActivity {

    String com_id;
    Memberlists_Adapter adapter;
    ArrayList<Kim_Com_Members_Response> list;
    LinearLayoutManager linearLayoutManager;

    SharedPreferences preferences;
    String user_id, com_name;

    RecyclerView recyclerView;

    private Parcelable recyclerViewState;

    Toolbar toolbar;

    TextView toolbar_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_member);

        Intent intent = getIntent();
        com_id = intent.getStringExtra("com_id");
        com_name = intent.getStringExtra("com_name");

        initialize();
        GetComMemberList();
    }

    private void initialize() {
        // 툴바
        toolbar = findViewById(R.id.com_member_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar_title = findViewById(R.id.com_member_comname);
        toolbar_title.setText(com_name);

        recyclerView = findViewById(R.id.com_member_list);
    }

    private void GetComMemberList() {
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        list = new ArrayList<>();
        adapter = new Memberlists_Adapter(getApplicationContext(), this.list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        preferences = getSharedPreferences("novarand", MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

        // 1. 레트로핏 빌드 & 인터페이스 지정?
        Kim_ApiInterface take = Kim_ApiClient.getApiClient(Community_Memberlist.this).create(Kim_ApiInterface.class);
        // 2. Response = 인터페이스내함수 // user_id 보내서 원하는 response 기다림
        Call<List<Kim_Com_Members_Response>> call = take.selectCommunityParticipantList(com_id);
        // 3. 선언한 call 을 게시글용 DTO
        call.enqueue(new Callback<List<Kim_Com_Members_Response>>() {
            @Override
            public void onResponse(@NonNull Call<List<Kim_Com_Members_Response>> call, @NonNull Response<List<Kim_Com_Members_Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 리스트 새로 만들어서
                    List<Kim_Com_Members_Response> responseResult = response.body();
                    for (int i = 0; i < responseResult.size(); i++) {
                        list.add(new Kim_Com_Members_Response(responseResult.get(i).getCommunity_id(),
                                responseResult.get(i).getUser_id(),
                                responseResult.get(i).getNick_name(),
                                responseResult.get(i).getProfile_file_name()));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Kim_Com_Members_Response>> call, Throwable t) {
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
            }

        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.mainnet.bubbly;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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
    ArrayList<Kim_Com_Members_Response> list, filteredList;
    LinearLayoutManager linearLayoutManager;

    SharedPreferences preferences;
    String user_id, com_name;

    RecyclerView recyclerView;

    private Parcelable recyclerViewState;

    Toolbar toolbar;

    TextView toolbar_title;

    EditText et_member;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_member);

        Intent intent = getIntent();
        com_id = intent.getStringExtra("com_id");
        com_name = intent.getStringExtra("com_name");


        initialize();
        GetComMemberList();

        et_member.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchFilter(et_member.getText().toString());
            }
        });
    }



    public void searchFilter(String searchText) {
        filteredList.clear();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getNick_name().toLowerCase().contains(searchText.toLowerCase()) || list.get(i).getUser_id().toLowerCase().contains(searchText.toLowerCase()) ) {
                filteredList.add(list.get(i));
            }
        }

        adapter.setFilter(filteredList);
    }

    private void initialize() {
        // ??????
        toolbar = findViewById(R.id.com_member_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar_title = findViewById(R.id.com_member_comname);
        toolbar_title.setText(com_name);

        recyclerView = findViewById(R.id.com_member_list);

        et_member = findViewById(R.id.member_list_search);

    }

    private void GetComMemberList() {
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //?????? ??????
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //?????? ??????
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        list = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new Memberlists_Adapter(getApplicationContext(), this.list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        preferences = getSharedPreferences("novarand", MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // ???????????? user_id???

        // 1. ???????????? ?????? & ??????????????? ???????
        Kim_ApiInterface take = Kim_ApiClient.getApiClient(Community_Memberlist.this).create(Kim_ApiInterface.class);
        // 2. Response = ???????????????????????? // user_id ????????? ????????? response ?????????
        Call<List<Kim_Com_Members_Response>> call = take.selectCommunityParticipantList(com_id);
        // 3. ????????? call ??? ???????????? DTO
        call.enqueue(new Callback<List<Kim_Com_Members_Response>>() {
            @Override
            public void onResponse(@NonNull Call<List<Kim_Com_Members_Response>> call, @NonNull Response<List<Kim_Com_Members_Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // ????????? ?????? ????????????
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
                Log.e("????????? ???????????? ????????? ??????", t.getMessage());
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
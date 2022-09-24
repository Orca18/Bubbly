package com.mainnet.bubbly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mainnet.bubbly.controller.Memberlists_Adapter;
import com.mainnet.bubbly.controller.SearchedNFT_Adapter;
import com.mainnet.bubbly.kim_util_test.Kim_ApiClient;
import com.mainnet.bubbly.kim_util_test.Kim_ApiInterface;
import com.mainnet.bubbly.kim_util_test.Kim_Com_Members_Response;
import com.mainnet.bubbly.model.NFTSearched_Item;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Community_NFTlist extends AppCompatActivity {

    String com_id;
    SearchedNFT_Adapter adapter;
    ArrayList<NFTSearched_Item> list = new ArrayList<>();
    ArrayList<NFTSearched_Item> nonFilteredList = new ArrayList<>();
    ArrayList<NFTSearched_Item> filteredList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;

    SharedPreferences preferences;
    String user_id, com_name;

    RecyclerView recyclerView;

    private Parcelable recyclerViewState;

    Toolbar toolbar;

    TextView toolbar_title;
    EditText search_member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_nft);

        Intent intent = getIntent();
        com_id = intent.getStringExtra("com_id");
        com_name = intent.getStringExtra("com_name");

        initialize();

        GetComNFTList(false, this.filteredList);
    }

    private void initialize() {
        // 툴바
        toolbar = findViewById(R.id.com_nft_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar_title = findViewById(R.id.com_nft_comname);
        toolbar_title.setText(com_name);


        recyclerView = findViewById(R.id.com_nft_list);

        search_member = findViewById(R.id.com_nft_searchMem);
        search_member.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println(list.size());
                //검색어를 입력받는다.
                String keyword = search_member.getText().toString();
                System.out.println(keyword);
                if(keyword.equals("")){
                    GetComNFTList(false, filteredList);
                }else{
                    filteredList.clear();
                    System.out.println(list.size());
                    for(int i=0; i<list.size(); i++){
                        System.out.println(list.get(i));
                        if(list.get(i).getUserName().contains(keyword)||list.get(i).getLoginId().contains(keyword)){
                            System.out.println(list.get(i));
                            filteredList.add(list.get(i));
                        }else{
                            System.out.println("else");
                        }
                    }
                    GetComNFTList(true, filteredList);
                }
            }
        });
    }

    private void GetComNFTList(boolean isFiltered, ArrayList<NFTSearched_Item> filterList) {
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        ViewGroup view = (ViewGroup) findViewById(android.R.id.content);

        if(isFiltered){
            adapter = new SearchedNFT_Adapter(getApplicationContext(), filterList, this, view);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else{
            list.clear();
            adapter = new SearchedNFT_Adapter(getApplicationContext(), list, this, view);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            ApiInterface api = ApiClient.getApiClient(Community_NFTlist.this).create(ApiInterface.class);
            Call<List<NFTSearched_Item>> call = api.selectCommunityParticipantNftList(com_id);
            call.enqueue(new Callback<List<NFTSearched_Item>>() {
                @Override
                public void onResponse(@NonNull Call<List<NFTSearched_Item>> call, @NonNull Response<List<NFTSearched_Item>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<NFTSearched_Item> responseResult = response.body();
                        for(int i=0; i<responseResult.size(); i++){
                            System.out.println("nft 보유 목록"+responseResult.get(i).getNft_id());
                            list.add(new NFTSearched_Item(responseResult.get(i).getProfileImageURL(),responseResult.get(i).getUserName(),responseResult.get(i).getLoginId(),
                                    responseResult.get(i).getUserId(),responseResult.get(i).getNft_id(),responseResult.get(i).getHolder_id(),
                                    responseResult.get(i).getCreation_time(), responseResult.get(i).getIsSell(),responseResult.get(i).getSeller_id(),
                                    responseResult.get(i).getSell_price(),responseResult.get(i).getApp_id(),responseResult.get(i).getNft_des(),
                                    responseResult.get(i).getNft_name(), responseResult.get(i).getFile_save_url(),responseResult.get(i).getNovaland_account_addr()));
                        }
                        nonFilteredList = list;
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<NFTSearched_Item>> call, Throwable t) {
                    Log.e("커뮤니티 nft 조회 에러", t.getMessage());
                }

            });
        }

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
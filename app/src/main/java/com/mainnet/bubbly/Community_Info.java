package com.mainnet.bubbly;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.kim_util_test.Kim_ApiClient;
import com.mainnet.bubbly.kim_util_test.Kim_ApiInterface;
import com.mainnet.bubbly.kim_util_test.Kim_Com_Info_Response;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.user_Response;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Community_Info extends AppCompatActivity {

    SharedPreferences preferences;
    String user_id;

    String com_id;
    String com_name;
    String com_owner;
    String com_desc;

    Toolbar toolbar;
    TextView toolbarname, tv_name, tv_desc, tv_rule;
    Button bt_edit;

    RelativeLayout rl;
    ImageView iv_title;

    CircleImageView cv_owner;
    TextView nick_owner, id_owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_info);

        Intent intent = getIntent();
        com_id = intent.getStringExtra("com_id");
        com_name = intent.getStringExtra("com_name");
        com_owner = intent.getStringExtra("com_owner");

        preferences = getSharedPreferences("novarand", MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값


        initialize();

        if (!com_owner.equals(user_id)) {
            bt_edit.setVisibility(View.INVISIBLE);
        }

        listeners();
        GetComInfo();

    }


    private void initialize() {
        // 툴바
        toolbar = findViewById(R.id.com_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarname = findViewById(R.id.com_info_comname);
        toolbarname.setText(com_name);

        bt_edit = findViewById(R.id.com_info_edit);

        tv_name = findViewById(R.id.com_info_detail_name);
        tv_desc = findViewById(R.id.com_info_detail_desc);
        tv_rule = findViewById(R.id.com_info_detail_rule);

        rl = findViewById(R.id.com_info_detail_rl);
        iv_title = findViewById(R.id.com_info_detail_titleimage);

        cv_owner = findViewById(R.id.item_com_owner_profile);
        nick_owner = findViewById(R.id.item_com_info_ownername);
        id_owner = findViewById(R.id.item_com_info_ownerid);
    }

    private void listeners() {
        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), Community_Info_Edit.class);
                mIntent.putExtra("com_id", com_id);
                startActivity(mIntent);
                finish();
            }
        });
    }


    private void GetComInfo() {
        Kim_ApiInterface api = Kim_ApiClient.getApiClient(Community_Info.this).create(Kim_ApiInterface.class);
        Call<List<Kim_Com_Info_Response>> call = api.selectCommunityUsingCommunityId(com_id);
        call.enqueue(new Callback<List<Kim_Com_Info_Response>>() {
            @Override
            public void onResponse(Call<List<Kim_Com_Info_Response>> call, Response<List<Kim_Com_Info_Response>> response) {

                com_name = response.body().get(0).getCommunity_name();
                com_owner = response.body().get(0).getCommunity_owner_id();
                com_desc = response.body().get(0).getCommunity_desc();

                tv_rule.setText(response.body().get(0).getRule());
                tv_desc.setText(com_desc);
                tv_name.setText(com_name);
                Glide.with(getApplicationContext()) //해당 환경의 Context나 객체 입력
                        .load(Config.cloudfront_addr + response.body().get(0).getProfile_file_name()) //URL, URI 등등 이미지를 받아올 경로
                        .centerCrop()
                        .into(iv_title);

                GetOwnerInfo(com_owner);
            }

            @Override
            public void onFailure(Call<List<Kim_Com_Info_Response>> call, Throwable t) {

            }
        });


    }

    private void GetOwnerInfo(String owner_id) {
        ApiInterface api = ApiClient.getApiClient(this).create(ApiInterface.class);
        Call<List<user_Response>> call_userInfo = api.selectUserInfo(owner_id);
        call_userInfo.enqueue(new Callback<List<user_Response>>() {
            @Override
            public void onResponse(@NonNull Call<List<user_Response>> call, @NonNull Response<List<user_Response>> response) {
                List<user_Response> responseResult = response.body();
                user_id = responseResult.get(0).getUser_id();

                Log.d("마지막 테스트", Config.cloudfront_addr+responseResult.get(0).getProfile_file_name()+"하핳");

                Glide.with(Community_Info.this)
                        .load(Config.cloudfront_addr+responseResult.get(0).getProfile_file_name())
                        .centerCrop()
                        .into(cv_owner);

                nick_owner.setText(responseResult.get(0).getUser_nick());
                id_owner.setText(responseResult.get(0).getUser_id());

            }

            @Override
            public void onFailure(Call<List<user_Response>> call, Throwable t) {

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
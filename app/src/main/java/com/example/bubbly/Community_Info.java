package com.example.bubbly;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.bubbly.config.Config;
import com.example.bubbly.kim_util_test.Kim_ApiClient;
import com.example.bubbly.kim_util_test.Kim_ApiInterface;
import com.example.bubbly.kim_util_test.Kim_Com_Info_Response;
import com.example.bubbly.model.UserInfo;

import java.util.List;

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
        
        if(!com_owner.equals(user_id)){
            bt_edit.setVisibility(View.GONE);
        }

        initialize();
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
    }

    private void listeners() {
        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), Community_Info_Edit.class);
                mIntent.putExtra("com_id",com_id);
                startActivity(mIntent);
                finish();
            }
        });
    }


    private void GetComInfo() {
        Kim_ApiInterface api = Kim_ApiClient.getApiClient().create(Kim_ApiInterface.class);
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
                        .load(Config.cloudfront_addr+response.body().get(0).getProfile_file_name()) //URL, URI 등등 이미지를 받아올 경로
                        .centerCrop()
                        .into(iv_title);


            }

            @Override
            public void onFailure(Call<List<Kim_Com_Info_Response>> call, Throwable t) {

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
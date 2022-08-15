package com.example.bubbly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Community_Create extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout done;
    ImageView thumb;
    EditText title, desc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);

        initialize();
        linsteners();

    }

    private void linsteners() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 조건이 맞을 때만 업로드
                // TODO API 36. 커뮤니티 정보 저장 http://3.39.84.115:80/community/createCommunity
                //"Multipart
                //community_owner_id,
                //writer_name,
                //community_name,
                //community_desc,
                //profile_file)"

                // 성공 시,
                finish();
            }
        });
    }

    private void initialize() {

        toolbar = findViewById(R.id.community_create_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        done = findViewById(R.id.community_create_done);
        thumb = findViewById(R.id.community_create_image);
        title = findViewById(R.id.community_create_title);
        desc = findViewById(R.id.community_create_desc);
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
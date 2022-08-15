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
import android.widget.Toast;

public class Community_Create extends AppCompatActivity {
    // 뒤로가기 시간
    private long backKeyPressedTime = 0;
    private Toast toast;

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
                if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                    backKeyPressedTime = System.currentTimeMillis();
                    toast = Toast.makeText(this, "작성 중인거 있을 때, 질문", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                    finish();
                    toast.cancel();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //뒤로가기 했을 때
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "작성 중인거 있을 때, 질문", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        } else {
            super.onBackPressed();
        }
    }

}
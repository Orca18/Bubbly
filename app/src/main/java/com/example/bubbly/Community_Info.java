package com.example.bubbly;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class Community_Info extends AppCompatActivity {
    
    SharedPreferences preferences;
    String user_id;
    
    String com_id;
    String com_name;
    String com_owner;

    Toolbar toolbar;
    TextView toolbarname;

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
        
        if(com_owner.equals(user_id)){
            Toast.makeText(getApplicationContext(), "커뮤니티장입니다. 편집 기능 제공 & 위임 기능",Toast.LENGTH_LONG).show();
        }

        initialize();


        
    }

    private void initialize() {
        // 툴바
        toolbar = findViewById(R.id.com_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarname = findViewById(R.id.com_info_comname);
        toolbarname.setText(com_name);

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
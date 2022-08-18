package com.example.bubbly;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class SS_Setting extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout myaccount, accountsecure, myactive, security, notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_option);

        // (기본) 리소스 ID 선언
        initiallize();
        // 클릭 리스너 모음 - 스택 O
        clickListeners();
    }

    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = findViewById(R.id.toolbar_setting_option);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myaccount = findViewById(R.id.setting_option_myaccount);
        accountsecure = findViewById(R.id.setting_option_myaccountsecurity);
        myactive = findViewById(R.id.setting_option_myactivity);
        security = findViewById(R.id.setting_option_security);
        notify = findViewById(R.id.setting_option_notify);

    }


    private void clickListeners() {
        myaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), SS_Setting_MyAccount.class);
                startActivity(mIntent);

            }
        });

        accountsecure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasting();

            }
        });

        myactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasting();

            }
        });

        security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasting();

            }
        });

        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasting();

            }
        });

    }

    private void Toasting() {
        Toast.makeText(getApplicationContext(), "상세 설정 설계", Toast.LENGTH_SHORT).show();
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
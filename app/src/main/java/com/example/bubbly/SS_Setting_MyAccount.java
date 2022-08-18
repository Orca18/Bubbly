package com.example.bubbly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SS_Setting_MyAccount extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout ll_accountInfo, ll_changeID, ll_changePW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ss_setting_myaccount);

        // (기본) 리소스 ID 선언
        initiallize();
        // 클릭 리스너 모음 - 스택 O
        clickListeners();
    }

    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = findViewById(R.id.toolbar_setting_myAccount);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ll_accountInfo = findViewById(R.id.ll_accountInfo_myAccount);
        ll_changeID = findViewById(R.id.ll_changeID_myAccount);
        ll_changePW = findViewById(R.id.ll_changePW_myAccount);

    }


    private void clickListeners() {
        ll_accountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), SS_Setting_MyAccount_ConfirmPW.class);
                mIntent.putExtra("class","AccountInfo");
                startActivity(mIntent);
            }
        });

        ll_changeID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), SS_Setting_MyAccount_ConfirmPW.class);
                mIntent.putExtra("class","ChangeID");
                startActivity(mIntent);

            }
        });

        ll_changePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), SS_Setting_MyAccount_ConfirmPW.class);
                mIntent.putExtra("class","ChangePW");
                startActivity(mIntent);

            }
        });
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

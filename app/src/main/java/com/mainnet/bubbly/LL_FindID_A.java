package com.mainnet.bubbly;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LL_FindID_A extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout bt_send_findID;
    EditText et_phone_findID;
    TextView tv_send_findID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ll_findid_a);
        // 툴바
        toolbar = findViewById(R.id.toolbar_findID_A);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bt_send_findID = findViewById(R.id.bt_send_findID);
        et_phone_findID = findViewById(R.id.et_phone_findID);
        tv_send_findID = findViewById(R.id.tv_send_findID);


        // 휴대폰 인증번호 전송
        bt_send_findID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface api = ApiClient.getApiClient(LL_FindID_A.this).create(ApiInterface.class);
                Call<String> call = api.sendPhoneCertificationNumForFind(et_phone_findID.getText().toString());
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if(response.body().toString().equals("not exist")){
                                Toast.makeText(getApplicationContext(), "입력한 휴대폰 번호가 존재하지 않습니다.",Toast.LENGTH_SHORT).show();
                            }else if(response.body().toString().equals("success")){
                                //코드 입력 페이지 이동
                                Intent mIntent = new Intent(getApplicationContext(), LL_FindID_B.class);
                                mIntent.putExtra("user_phone",et_phone_findID.getText().toString());
                                startActivity(mIntent);
                                Toast.makeText(getApplicationContext(), "휴대폰 인증 전송", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "문자 전송에 실패했습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.e("휴대폰 인증번호 전송 에러", t.getMessage());
                    }
                });
            }
        });

        et_phone_findID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                //정규식 확인(숫자, 9-12자리만 가능
                String rex = "^[0-9]{9,}$";
                Pattern pattern = Pattern.compile(rex);
                if(pattern.matcher(et_phone_findID.getText().toString()).matches()){
                    InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(et_phone_findID.getWindowToken(), 0);
                    tv_send_findID.setTextColor(Color.parseColor("#FFFFFF")); // 아이디,비밀번호 정상 입력시 다음 텍스트 색상 검정색
                    bt_send_findID.setEnabled(true); // 인증완료 시 다음 버튼 활성화
                    bt_send_findID.setBackgroundColor(Color.parseColor("#FF000000")); // 아이디,비밀번호 정상 시 다음 버튼 색상 검정색}
                }
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

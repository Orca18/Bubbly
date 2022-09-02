package com.example.bubbly;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

import com.example.bubbly.model.AccessAndRefreshToken;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SS_Setting_MyAccount_ConfirmPW extends AppCompatActivity {

    Toolbar toolbar;
    EditText et_password;
    LinearLayout bt_confirm;
    TextView tv_pw_check;
    String className;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ss_setting_myaccount_confirmpw);
        //intent로 비밀번호 확인 후 이동할 클래스 정보 가져오기(비밀번호 확인 페이지를 모든 설정-계정정보 페이지에 공통 적용)
        Intent mIntent = getIntent();
        className = mIntent.getStringExtra("class");

        // (기본) 리소스 ID 선언
        initiallize();
        // 클릭 리스너 모음 - 스택 O
        clickListeners();
    }

    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = findViewById(R.id.toolbar_setting_myAccount_confirmPW);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_password = findViewById(R.id.et_password_setting_confirmPW);
        bt_confirm = findViewById(R.id.bt_confirm_setting_confirmPW);
        tv_pw_check = findViewById(R.id.tv_pw_check_setting_confirmPW);

    }


    private void clickListeners() {
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface login_api = ApiClient.getApiClient(SS_Setting_MyAccount_ConfirmPW.this).create(ApiInterface.class);
                Call<String> call = login_api.login(UserInfo.login_id, et_password.getText().toString());
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("로그인 데이터", response.body().toString());

                            if (response.body().toString().equals("fail")) {
                                tv_pw_check.setText("올바른 비밀번호가 아닙니다.");
                                tv_pw_check.setTextColor(Color.parseColor("#FF0000"));
                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                            } else {
                                //수신한 데이터를 json으로 파싱한다.
                                JSONObject json = null;
                                try {
                                    //log in api 재활용을 위해서 토큰을 새로 업데이트 한다.
                                    json = new JSONObject(response.body().toString());
                                    AccessAndRefreshToken.accessToken = json.getString("accessToken");
                                    AccessAndRefreshToken.refreshToken = json.getString("refreshToken");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //이동할 페이지 지정
                                if(className.equals("AccountInfo")){
                                    Intent mIntent = new Intent(getApplicationContext(), SS_Setting_MyAccount_AccountInfo.class);
                                    startActivity(mIntent);
                                }else if(className.equals("ChangeID")){
                                    Intent mIntent = new Intent(getApplicationContext(), SS_Setting_MyAccount_ChangeID.class);
                                    startActivity(mIntent);
                                }else if(className.equals("ChangePW")){
                                    Intent mIntent = new Intent(getApplicationContext(), SS_Setting_MyAccount_ChangePW.class);
                                    startActivity(mIntent);
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("로그인 에러", t.getMessage());
                    }
                });

            }
        });

        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_pw_check.setText("");// 비밀번호를 다시입력시, 비밀번호 확인란 입력값 초기화
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //키보드 내리기
        et_password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_password.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
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

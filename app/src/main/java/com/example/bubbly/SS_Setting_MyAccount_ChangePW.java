package com.example.bubbly;

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
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SS_Setting_MyAccount_ChangePW extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout bt_changePW_findID;
    EditText et_password_findID, et_password_check_findID;
    TextView tv_pw_check_findID, tv_changePW_findID;
    int pw_check = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ll_findid_d);
        toolbar = findViewById(R.id.toolbar_findID_D);
        bt_changePW_findID = findViewById(R.id.bt_changePW_findID);
        et_password_findID = findViewById(R.id.et_password_findID);
        et_password_check_findID = findViewById(R.id.et_password_check_findID);
        tv_pw_check_findID = findViewById(R.id.tv_pw_check_findID);
        tv_changePW_findID = findViewById(R.id.tv_changePW_findID);
        String  user_id = UserInfo.user_id;

        //비밀번호 변경
        bt_changePW_findID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface api = ApiClient.getApiClient(SS_Setting_MyAccount_ChangePW.this).create(ApiInterface.class);
                Call<String> call = api.modifyPassword(user_id, et_password_check_findID.getText().toString());
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if(response.body().toString().equals("fail")){
                                Toast.makeText(getApplicationContext(), "변경에 실패했습니다.",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "성공적으로 변경했습니다.",Toast.LENGTH_SHORT).show();
                                Intent mIntent = new Intent(getApplicationContext(), SS_Setting.class);
                                startActivity(mIntent);
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.e("비밀번호 변경 에러", t.getMessage());
                    }
                });
            }
        });


        // pw
        et_password_findID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_password_check_findID.setText(null); // 비밀번호를 다시입력시, 비밀번호 확인란 입력값 초기화
                tv_pw_check_findID.setText("");
                pw_check = 0;
                InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(et_password_check_findID.getWindowToken(), 0);
                bt_changePW_findID.setEnabled(false); // 다시 입력 시도하면 버튼 비활성화
                bt_changePW_findID.setBackgroundColor(Color.parseColor("#eeeeee")); // 회색
                tv_changePW_findID.setTextColor(Color.parseColor("#FF000000"));
            }
            @Override
            public void afterTextChanged(Editable s) {
                //비밀번호 정규식 확인
                String pwRex = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,16}.$";
                Pattern pattern = Pattern.compile(pwRex);
                if(pattern.matcher(et_password_findID.getText().toString()).matches()){
                    tv_pw_check_findID.setText("비밀번호 규칮에 맞습니다.");
                    tv_pw_check_findID.setTextColor(Color.parseColor("#002AFF"));
                    et_password_check_findID.setEnabled(true);
                }else{
                    tv_pw_check_findID.setText("비밀번호 규칮에 맞지 않습니다.");
                    tv_pw_check_findID.setTextColor(Color.parseColor("#FF0000"));
                }
            }
        });

        // pw 확인
        et_password_check_findID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(et_password_check_findID.getText().toString().equals(et_password_findID.getText().toString())){
                    tv_pw_check_findID.setText("비밀번호가 일치합니다.");
                    tv_pw_check_findID.setTextColor(Color.parseColor("#002AFF"));
                    pw_check = 1;
                    if(pw_check == 1){
                        InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        keyboard.hideSoftInputFromWindow(et_password_check_findID.getWindowToken(), 0);
                        bt_changePW_findID.setEnabled(true); // 인증완료 시 다음 버튼 활성화
                        bt_changePW_findID.setBackgroundColor(Color.parseColor("#FF000000")); // 검정색
                        tv_changePW_findID.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                }
                else{
                    pw_check = 0;
                    tv_pw_check_findID.setText("비밀번호가 일치하지 않습니다.");
                    tv_pw_check_findID.setTextColor(Color.parseColor("#FF0000"));
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

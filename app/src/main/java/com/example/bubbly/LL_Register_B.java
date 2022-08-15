package com.example.bubbly;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LL_Register_B extends AppCompatActivity {

    // 뒤로가기 시간
    private long backKeyPressedTime = 0;
    private Toast toast;

    LinearLayout done;
    androidx.appcompat.widget.Toolbar toolbar;

    EditText et_id, et_password, et_password_check;
    TextView tv_id_check, tv_pw_check, tv_done;

    int id_check, pw_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_b);

        toolbar = findViewById(R.id.toolbar_registerB);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // 뒤로가기 버튼, 디폴트로 true 만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_id = findViewById(R.id.et_id);
        tv_id_check = findViewById(R.id.tv_id_check);
        et_password = findViewById(R.id.et_password);
        et_password_check = findViewById(R.id.et_password_check);
        tv_pw_check = findViewById(R.id.tv_pw_check);
        done = findViewById(R.id.done);
        tv_done = findViewById(R.id.tv_done);

        Intent mIntent = getIntent();
        String user_email = mIntent.getStringExtra("user_email");
        String user_phone = mIntent.getStringExtra("user_phone");

        // 회원가입 (회원정보 저장)
        done.setEnabled(false);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_id_check.getText().toString().equals("사용가능한 아이디입니다.") && tv_pw_check.getText().toString().equals("비밀번호가 일치합니다.")){
                    ApiInterface createUserInfo_api = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<String> call = createUserInfo_api.createUserInfo(et_id.getText().toString(), et_password.getText().toString(), user_email, user_phone, "test123");
                    call.enqueue(new Callback<String>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                        {
                            if (response.isSuccessful() && response.body() != null)
                            {
                                Log.e("희원가입 성공", response.body().toString());
                                if(response.body().toString().equals("success")){
                                    Toast.makeText(getApplicationContext(), "희원가입 성공",Toast.LENGTH_SHORT).show();
                                    //블록체인 계정생성 요청

                                    //다이얼로그로 니모닉과 버튼표시 (지갑저장 / 버블리 저장소 저장(pw암호화) / copy)

                                    //다이얼로그 닫기 후 액티비티 종료
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "희원가입 실패",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                        {
                            Log.e("희원가입 에러", t.getMessage());
                        }
                    });
                }
            }
        });
//        InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        keyboard.hideSoftInputFromWindow(et_reply_reply.getWindowToken(), 0);
//        tv_done.setTextColor(Color.parseColor("#FFFFFF")); // 아이디,비밀번호 정상 입력시 다음 텍스트 색상 검정색
//        done.setEnabled(true); // 인증완료 시 다음 버튼 활성화
//        done.setBackgroundColor(Color.parseColor("#FF000000")); // 아이디,비밀번호 정상 시 다음 버튼 색상 검정색}

        // 아이디 중복체크
        et_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(et_id.length() == 0){
                    tv_id_check.setText("");
                    id_check = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(et_id.length() > 0){
                    ApiInterface selectIsExistingId_api = ApiClient.getApiClient().create(ApiInterface.class);
                    Call<String> call = selectIsExistingId_api.selectIsExistingId(et_id.getText().toString());
                    call.enqueue(new Callback<String>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                        {
                            if (response.isSuccessful() && response.body() != null)
                            {
                                Log.e("아이디 중복 체크 성공", response.body().toString());
                                if(response.body().toString().equals("exist")){ // 아이디 중복
                                    id_check = 0;
                                    tv_id_check.setText("중복된 아이디입니다.");
                                    tv_id_check.setTextColor(Color.parseColor("#FF0000"));
                                }
                                else if(response.body().toString().equals("not exist")){ // 아이디 중복x 사용가능
                                    id_check = 1;
                                    tv_id_check.setText("사용가능한 아이디입니다.");
                                    tv_id_check.setTextColor(Color.parseColor("#4CAF50"));
                                    if(id_check == 1 && pw_check == 1){
                                        InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                        keyboard.hideSoftInputFromWindow(et_id.getWindowToken(), 0);
                                        tv_done.setTextColor(Color.parseColor("#FFFFFF")); // 아이디,비밀번호 정상 입력시 다음 텍스트 색상 흰색
                                        done.setEnabled(true); // 인증완료 시 다음 버튼 활성화
                                        done.setBackgroundColor(Color.parseColor("#FF000000")); // 아이디,비밀번호 정상 시 다음 버튼 색상 검정색
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                        {
                            Log.e("아이디 중복 체크 에러", t.getMessage());
                        }
                    });
                }
            }
        });

        // pw
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_password_check.setText(null); // 비밀번호를 다시입력시, 비밀번호 확인란 입력값 초기화
                tv_pw_check.setText("");
                pw_check = 0;
            }
            @Override
            public void afterTextChanged(Editable s) {
                //비밀번호 정규식 확인
                String pwRex = "^(?=.*[A-Za-z])(?=.*[$@$!%*#?&.])[A-Za-z$@$!%*#?&.]{8,16}$";
                Pattern pattern = Pattern.compile(pwRex);
                if(pattern.matcher(et_password.getText().toString()).matches()){
                    et_password_check.setEnabled(true);
                }else{
                    Toast.makeText(getApplicationContext(), "비밀번호 규칮에 맞지 않습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // pw 확인
        et_password_check.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(et_password.getText().toString().equals(et_password_check.getText().toString())){
                    tv_pw_check.setText("비밀번호가 일치합니다.");
                    tv_pw_check.setTextColor(Color.parseColor("#FFFFFF"));
                    pw_check = 1;
                    if(id_check == 1 && pw_check == 1){
                        InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        keyboard.hideSoftInputFromWindow(et_password_check.getWindowToken(), 0);
                        tv_done.setTextColor(Color.parseColor("#FFFFFF")); // 아이디,비밀번호 정상 입력시 다음 텍스트 색상 흰색
                        done.setEnabled(true); // 인증완료 시 다음 버튼 활성화
                        done.setBackgroundColor(Color.parseColor("#FF000000")); // 아이디,비밀번호 정상 시 다음 버튼 색상 검정색
                    }
                }
                else{
                    pw_check = 0;
                    tv_pw_check.setText("비밀번호가 일치하지 않습니다.");
                    tv_pw_check.setTextColor(Color.parseColor("#FF0000"));
                }
            }
        });



    } // onCreate 닫는곳

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                    backKeyPressedTime = System.currentTimeMillis();
                    toast = Toast.makeText(this, "인증 다시 받아야되는데, 진짜 종료??", Toast.LENGTH_SHORT);
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
            toast = Toast.makeText(this, "인증 다시 받아야되는데, 진짜 종료??", Toast.LENGTH_SHORT);
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
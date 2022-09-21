package com.mainnet.bubbly;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mainnet.bubbly.R;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;

import org.json.JSONException;

import java.io.IOException;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LL_Register_A extends AppCompatActivity {

    LinearLayout next;
    androidx.appcompat.widget.Toolbar toolbar;

    EditText et_email,et_authentication_num,et_phone,et_authentication_phonenum;
    TextView tv_send_email,tv_authentication_num_check,tv_send_phone,tv_et_authentication_phonenum_check,tv_next_text,tv_privacyPolicy, tv_termsToUse;
    CheckBox ck_agree_all, ck_agree_privatePolicy, ck_agree_termsToUse;
    WebView wv_privacyPolicy, wv_termsToUse;
    int authentication_check = 0; // 휴대폰,이메일 인증, 약관2개 체크
    int authentication_check_target = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_a);
        
        toolbar = findViewById(R.id.toolbar_registerA);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        next = findViewById(R.id.next);
        tv_next_text = findViewById(R.id.tv_next_text);
        et_email = findViewById(R.id.et_email);
        tv_send_email = findViewById(R.id.tv_send_email);
        et_authentication_num = findViewById(R.id.et_authentication_num);
        tv_authentication_num_check = findViewById(R.id.tv_authentication_num_check);
        et_phone = findViewById(R.id.et_phone);
        tv_send_phone = findViewById(R.id.tv_send_phone);
        et_authentication_phonenum = findViewById(R.id.et_authentication_phonenum);
        tv_et_authentication_phonenum_check = findViewById(R.id.tv_et_authentication_phonenum_check);
        ck_agree_all = findViewById(R.id.ck_agree_all);
        ck_agree_privatePolicy = findViewById(R.id.ck_agree_privacyPolicy);
        ck_agree_termsToUse = findViewById(R.id.ck_agree_termsToUse);
        tv_privacyPolicy = findViewById(R.id.tv_privacyPolicy);
        tv_termsToUse = findViewById(R.id.tv_termsToUse);
        wv_privacyPolicy = findViewById(R.id.wv_privacyPolicy);
        wv_termsToUse = findViewById(R.id.wv_termsToUse);

        next.setEnabled(false); // 초기 다음버튼 비활성화
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), LL_Register_B.class);
                mIntent.putExtra("user_email",et_email.getText().toString());
                mIntent.putExtra("user_phone",et_phone.getText().toString());
                startActivity(mIntent);
                //Toast.makeText(getApplicationContext(), "나중에 뒤로가기 추가하기!!!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // 이메일 인증번호 전송
       tv_send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이메일 정규식 확인
                Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
                if(!pattern.matcher(et_email.getText().toString()).matches()){
                    Toast.makeText(getApplicationContext(), "이메일 형식에 맞게 입력해주세요.",Toast.LENGTH_SHORT).show();
                } else {
                    //정규식 확인 완료 후 서버에 요청
                    ApiInterface sendEmailCertificationNum_api = ApiClient.getApiClient(LL_Register_A.this).create(ApiInterface.class);
                    Call<String> call = sendEmailCertificationNum_api.sendEmailCertificationNum(et_email.getText().toString());
                    call.enqueue(new Callback<String>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                        {
                            if (response.isSuccessful() && response.body() != null)
                            {
                                Log.e("이메일 전송", response.body().toString());
                                if(response.body().toString().equals("exist")){
                                    Toast.makeText(getApplicationContext(), "동일한 이메일이 이미 존재합니다.",Toast.LENGTH_SHORT).show();
                                }else if(response.body().toString().equals("fail")){
                                    Toast.makeText(getApplicationContext(), "이메일 전송에 실패했습니다.",Toast.LENGTH_SHORT).show();
                                }else{
                                    et_email.setEnabled(false); // 이메일 전송 시 이메일 입력란 비활성화
                                    Toast.makeText(getApplicationContext(), "이메일의 수신함을 확인해주세요.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                        {
                            Log.e("이메일 전송 에러", t.getMessage());
                            Toast.makeText(getApplicationContext(), "이메일 전송에 실패했습니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

       // 이메일 인증번호 체크
       tv_authentication_num_check.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ApiInterface verifyEmailCertificationNum_api = ApiClient.getApiClient(LL_Register_A.this).create(ApiInterface.class);
               Call<String> call = verifyEmailCertificationNum_api.verifyEmailCertificationNum(et_email.getText().toString(),et_authentication_num.getText().toString());
               call.enqueue(new Callback<String>()
               {
                   @Override
                   public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                   {
                       if (response.isSuccessful() && response.body() != null)
                       {
                           Log.e("이메일 인증", response.body().toString());
                           if(response.body().toString().equals("equal")){
                               authentication_check+=1;
                               System.out.println("authentication_check : "+authentication_check);
                               Toast.makeText(getApplicationContext(), "이메일 인증 완료.",Toast.LENGTH_SHORT).show();
                               et_authentication_num.setEnabled(false); // 인증완료 시 인증번호 입력란 비활성화
                               if(authentication_check==authentication_check_target) { // 휴대폰, 이메일 인증 모두 완료 했을 떄
                                   tv_next_text.setTextColor(Color.parseColor("#FFFFFF")); // 인증완료 시 다음 텍스트 색상 하얀색
                                   next.setEnabled(true); // 인증완료 시 다음 버튼 활성화
                                   next.setBackgroundColor(Color.parseColor("#FF000000")); // 인증완료 시 다음 버튼 색상 검정색}
                               }
                           }
                           else{
                               Toast.makeText(getApplicationContext(), "이메일 인증 실패.",Toast.LENGTH_SHORT).show();
                           }
                       }
                   }

                   @Override
                   public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                   {
                       Log.e("이메일 인증 에러", t.getMessage());
                   }
               });
           }
       });


        // 휴대폰 인증번호 전송
        tv_send_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //하이픈 없이 전송
                if(et_phone.getText().toString().contains("-")){
                    Toast.makeText(getApplicationContext(), "숫자만 입력해주세요.",Toast.LENGTH_SHORT).show();
                }else {
                    ApiInterface sendPhoneCertificationNum_api = ApiClient.getApiClient(LL_Register_A.this).create(ApiInterface.class);
                    Call<String> call = sendPhoneCertificationNum_api.sendPhoneCertificationNum(et_phone.getText().toString());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Log.e("휴대폰 인증번호 전송 성공", response.body().toString());
                                if(response.body().toString().equals("exist")){
                                    Toast.makeText(getApplicationContext(), "동일한 휴대폰 번호가 이미 존재합니다.",Toast.LENGTH_SHORT).show();
                                }else if(response.body().toString().equals("fail")){
                                    Toast.makeText(getApplicationContext(), "문자 전송에 실패했습니다.",Toast.LENGTH_SHORT).show();
                                }else{
                                    et_phone.setEnabled(false); // 휴대폰 인증 전송 시 휴대폰번호 입력란 비활성화
                                    Toast.makeText(getApplicationContext(), "휴대폰 인증 전송", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.e("휴대폰 인증번호 전송 에러", t.getMessage());
                        }
                    });
                }
            }
        });

        // 휴대폰 인증번호 체크
        tv_et_authentication_phonenum_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface verifyPhoneCertificationNum_api = ApiClient.getApiClient(LL_Register_A.this).create(ApiInterface.class);
                Call<String> call = verifyPhoneCertificationNum_api.verifyPhoneCertificationNum(et_phone.getText().toString(),et_authentication_phonenum.getText().toString());
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            if(response.body().toString().equals("equal")){
                                authentication_check+=1;
                                System.out.println("authentication_check : "+authentication_check);
                                Log.e("휴대폰 인증", response.body().toString());
                                Toast.makeText(getApplicationContext(), "휴대폰 인증 완료.",Toast.LENGTH_SHORT).show();
                                et_authentication_phonenum.setEnabled(false); // 인증완료 시 인증번호 입력란 비활성화
                                if(authentication_check==authentication_check_target) { // 휴대폰, 이메일 인증 모두 완료 했을 떄
                                    tv_next_text.setTextColor(Color.parseColor("#FFFFFF")); // 인증완료 시 다음 텍스트 색상 검정색
                                    next.setEnabled(true); // 인증완료 시 다음 버튼 활성화
                                    next.setBackgroundColor(Color.parseColor("#FF000000")); // 인증완료 시 다음 버튼 색상 검정색}
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "휴대폰 인증 실패.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("휴대폰 인증 에러", t.getMessage());
                    }
                });
            }
        });


        ck_agree_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ck_agree_all.isChecked()){
                    ck_agree_privatePolicy.setChecked(false);
                    ck_agree_termsToUse.setChecked(false);
                    authentication_check-=2;
                }else{
                    ck_agree_privatePolicy.setChecked(true);
                    ck_agree_termsToUse.setChecked(true);
                    authentication_check+=2;
                    if(authentication_check==authentication_check_target) { // 휴대폰, 이메일 인증 모두 완료 했을 떄
                        tv_next_text.setTextColor(Color.parseColor("#FFFFFF")); // 인증완료 시 다음 텍스트 색상 검정색
                        next.setEnabled(true); // 인증완료 시 다음 버튼 활성화
                        next.setBackgroundColor(Color.parseColor("#FF000000")); // 인증완료 시 다음 버튼 색상 검정색}
                    }
                }
            }
        });

        ck_agree_privatePolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ck_agree_privatePolicy.isChecked()){
                    authentication_check-=1;
                }
                else{
                    authentication_check+=1;
                    if(authentication_check==authentication_check_target) { // 휴대폰, 이메일 인증 모두 완료 했을 떄
                        tv_next_text.setTextColor(Color.parseColor("#FFFFFF")); // 인증완료 시 다음 텍스트 색상 검정색
                        next.setEnabled(true); // 인증완료 시 다음 버튼 활성화
                        next.setBackgroundColor(Color.parseColor("#FF000000")); // 인증완료 시 다음 버튼 색상 검정색}
                    }
                }
            }
        });

        ck_agree_termsToUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ck_agree_termsToUse.isChecked()){
                    authentication_check-=1;
                }
                else{
                    authentication_check+=1;
                    if(authentication_check==authentication_check_target) { // 휴대폰, 이메일 인증 모두 완료 했을 떄
                        tv_next_text.setTextColor(Color.parseColor("#FFFFFF")); // 인증완료 시 다음 텍스트 색상 검정색
                        next.setEnabled(true); // 인증완료 시 다음 버튼 활성화
                        next.setBackgroundColor(Color.parseColor("#FF000000")); // 인증완료 시 다음 버튼 색상 검정색}
                    }
                }
            }
        });

        tv_privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Config.api_server_addr == null){
                    try {
                        new Config(getApplicationContext()).getConfigData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                displayTermsWithWebView("개인정보처리방침",Config.api_server_addr+"/share/privacyPolicy");
            }
        });

        tv_termsToUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Config.api_server_addr == null){
                    try {
                        new Config(getApplicationContext()).getConfigData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                displayTermsWithWebView("이용약관",Config.api_server_addr+"/share/termsToUse");
            }
        });
    } // onCreate 닫는곳

    public void displayTermsWithWebView (String title, String url){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        WebView wv = new WebView(this);
        wv.loadUrl(url);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        alert.setView(wv);
        alert.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
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
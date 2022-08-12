package com.example.bubbly;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LL_Register_A extends AppCompatActivity {

    LinearLayout next;
    androidx.appcompat.widget.Toolbar toolbar;

    EditText et_email,et_authentication_num,et_phone,et_authentication_phonenum;
    TextView tv_send_email,tv_authentication_num_check,tv_send_phone,tv_et_authentication_phonenum_check,tv_next_text;

    int authentication_check = 0; // 휴대폰,이메일 인증 체크

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


        //next.setEnabled(false); // 초기 다음버튼 비활성화
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
                ApiInterface sendEmailCertificationNum_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = sendEmailCertificationNum_api.sendEmailCertificationNum(et_email.getText().toString());
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            Log.e("이메일 전송", response.body().toString());
                            et_email.setEnabled(false); // 이메일 전송 시 이메일 입력란 비활성화
                            Toast.makeText(getApplicationContext(), "이메일 전송 완료!!",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("이메일 전송 에러", t.getMessage());
                    }
                });
            }
        });

       // 이메일 인증번호 체크
       tv_authentication_num_check.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ApiInterface verfyEmailCertificationNum_api = ApiClient.getApiClient().create(ApiInterface.class);
               Call<String> call = verfyEmailCertificationNum_api.verfyEmailCertificationNum(et_email.getText().toString(),et_authentication_num.getText().toString());
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
                               if(authentication_check==2) { // 휴대폰, 이메일 인증 모두 완료 했을 떄
                                   tv_next_text.setTextColor(Color.parseColor("#FFFFFF")); // 인증완료 시 다음 텍스트 색상 검정색
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
                ApiInterface sendPhoneCertificationNum_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = sendPhoneCertificationNum_api.sendPhoneCertificationNum(et_phone.getText().toString());
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            Log.e("휴대폰 인증번호 전송 성공", response.body().toString());
                            et_phone.setEnabled(false); // 휴대폰 인증 전송 시 휴대폰번호 입력란 비활성화
                            Toast.makeText(getApplicationContext(), "휴대폰 인증 전송",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("휴대폰 인증번호 전송 에러", t.getMessage());
                    }
                });
            }
        });

        // 휴대폰 인증번호 체크
        tv_et_authentication_phonenum_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface verfyPhoneCertificationNum_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = verfyPhoneCertificationNum_api.verfyPhoneCertificationNum(et_phone.getText().toString(),et_authentication_phonenum.getText().toString());
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
                                if(authentication_check==2) { // 휴대폰, 이메일 인증 모두 완료 했을 떄
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



    } // onCreate 닫는곳


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
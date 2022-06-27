package com.example.novarand_sns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.novarand_sns.retrofit.ApiClient;
import com.example.novarand_sns.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LL_Login extends AppCompatActivity {

    LinearLayout bt_login;
    TextView tv_register;
    EditText et_login_id,et_login_pw;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String id, pw;
    Boolean sptest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        bt_login = findViewById(R.id.bt_login);
        tv_register = findViewById(R.id.tv_register);
        et_login_id = findViewById(R.id.et_login_id);
        et_login_pw = findViewById(R.id.et_login_pw);

        preferences = getSharedPreferences("novarand",MODE_PRIVATE);
        editor = preferences.edit();

        editor.remove("user_id");
        editor.commit();

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface login_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = login_api.login(et_login_id.getText().toString(), et_login_pw.getText().toString());
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            Log.e("로그인 데이터", response.body().toString());
                            if(response.body().toString().equals("fail")){
                                Toast.makeText(getApplicationContext(), "로그인 실패",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "로그인 성공",Toast.LENGTH_SHORT).show();
                                String[] split = response.body().toString().split(":");
                                String splitId = split[3];
                                splitId = splitId.replace("}", "");
                                Log.e("userID", splitId);
                                editor.putString("user_id",splitId);
                                editor.commit();

                                startActivity(new Intent(LL_Login.this, MM_Home.class));
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                finish();
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

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LL_Login.this, LL_Register_A.class));
            }
        });

    }
}
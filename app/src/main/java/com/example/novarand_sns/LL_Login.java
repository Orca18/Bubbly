package com.example.novarand_sns;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LL_Login extends AppCompatActivity {

    LinearLayout login;
    TextView register;

    String id, pw;

    Boolean sptest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login = findViewById(R.id.btn_login_login);
        register = findViewById(R.id.login_register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(getApplicationContext(), "로그인 o_+",Toast.LENGTH_SHORT).show();

//                if(sptest){
//                    SharedPreferences sharedPreferences= getSharedPreferences("login", MODE_PRIVATE);
//                    SharedPreferences.Editor editor= sharedPreferences.edit();
//                    editor.putString("email",id);
//                    editor.putBoolean("status",true);
//                    editor.commit();    //최종 커밋. 커밋을 해야 저장이 된다.
//                    // 밑에 인텐트 여기에 넣어주기
//                }

                startActivity(new Intent(LL_Login.this, MM_Home.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LL_Login.this, LL_Register_A.class));
            }
        });

    }
}
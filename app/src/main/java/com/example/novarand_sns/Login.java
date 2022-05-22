package com.example.novarand_sns;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    Button login;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login = findViewById(R.id.btn_login_login);
        register = findViewById(R.id.login_register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Home.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                Toast.makeText(getApplicationContext(), "로그인 o_+",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register_A.class));
            }
        });

    }
}
package com.example.novarand_sns;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Register_A extends AppCompatActivity {

    LinearLayout next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_a);

        next = findViewById(R.id.register_A_Next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), Register_B.class);
                startActivity(mIntent);
                Toast.makeText(getApplicationContext(), "나중에 뒤로가기 추가하기!!!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
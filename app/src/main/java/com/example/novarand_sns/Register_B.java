package com.example.novarand_sns;
import android.widget.Toast;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Register_B extends AppCompatActivity {

    LinearLayout done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_b);

        done = findViewById(R.id.register_B_Done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Toast.makeText(getApplicationContext(), "나중에 뒤로가기 추가@@@",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
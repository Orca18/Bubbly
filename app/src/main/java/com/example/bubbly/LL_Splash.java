package com.example.bubbly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

public class LL_Splash extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar toolbar;

    ImageView appname,splashimg;

    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        // 툴바
        toolbar = findViewById(R.id.splash_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        lottieAnimationView = findViewById(R.id.lottie_voltz);

        lottieAnimationView.animate().setStartDelay(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(LL_Splash.this, LL_Login.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        },2300);
    }

}
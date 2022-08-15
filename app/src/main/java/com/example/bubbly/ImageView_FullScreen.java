package com.example.bubbly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

public class ImageView_FullScreen extends AppCompatActivity {

    PhotoView img;
    PhotoViewAttacher photoViewAttacher;
    androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_full_screen);


        toolbar = findViewById(R.id.toolbar_fullscreen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        img = findViewById(R.id.image_full);

        Intent intent = getIntent();
        String url = intent.getStringExtra("img_url");

        Glide.with(this)
                .load(url)
                .into(img);
        photoViewAttacher = new PhotoViewAttacher(img);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
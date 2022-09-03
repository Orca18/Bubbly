package com.example.bubbly;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.bubbly.chatting.util.GetDate;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.GlideImageViewFactory;
import com.github.piasy.biv.view.ImageSaveCallback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ImageView_FullScreen_Download extends AppCompatActivity {
    private BigImageView img;
    androidx.appcompat.widget.Toolbar toolbar;
    private DownloadManager mDownloadManager;
    String time = GetDate.getCurrentTime(false);
    private Long mDownloadQueueId;

    // 다운받은 파일이 저장될 위치 설정
    private final String outputFilePath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS + "/bubbly") + "/" + time;

    public ImageView_FullScreen_Download() throws Exception {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* toolbar = findViewById(R.id.toolbar_fullscreen_download);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/


        BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));

        setContentView(R.layout.activity_image_view_full_screen_download);

        String img_url = getIntent().getStringExtra("img_url");

        img = findViewById(R.id.image_full);
        img.setProgressIndicator(new ProgressPieIndicator());
        img.setImageViewFactory(new GlideImageViewFactory());

        Uri uri = Uri.parse(img_url);

        Path path = new File(img_url).toPath();
        try {
            String mimeType = Files.probeContentType(path);
            Log.d("mime:" , mimeType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        img.showImage(uri);

        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //URLDownloading(uri);

                img.setImageSaveCallback(new ImageSaveCallback() {
                    @Override
                    public void onSuccess(String uri) {
                        Toast.makeText(ImageView_FullScreen_Download.this,
                                "다운로드 완료",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(ImageView_FullScreen_Download.this,
                                "다운로드 실패",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                System.out.println("11aaaa");

                // should be called on worker/IO thread
                if (ActivityCompat.checkSelfPermission(ImageView_FullScreen_Download.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ImageView_FullScreen_Download.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
                    return;
                } else{
                    // 권한이 있다면 사진저장 실행
                    img.saveImageIntoGallery();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        long start = System.nanoTime();
        //Utils.fixLeakCanary696(getApplicationContext());
        long end = System.nanoTime();
        //Log.w(Utils.TAG, "fixLeakCanary696: " + (end - start));

        BigImageViewer.imageLoader().cancelAll();
    }

    // 권한 허락했을 때
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(ImageView_FullScreen_Download.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    img.saveImageIntoGallery();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
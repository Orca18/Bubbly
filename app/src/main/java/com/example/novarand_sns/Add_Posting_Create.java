package com.example.novarand_sns;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class Add_Posting_Create extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();


    Toolbar toolbar;
    ImageView add_image, add_video, my_image, thumbnail;
    TextView posting;
    VideoView videoView;
    ImageView playicon, imgdelete, thumbdelete;
    RelativeLayout r_img, r_thumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_create);

        initiallize();
        touchEvent();
    }


    private void initiallize() {
        toolbar = findViewById(R.id.toolbar_creating);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        add_image = findViewById(R.id.posting_create_addimage);
        posting = findViewById(R.id.posting_create_posting);
        my_image = findViewById(R.id.posting_create_myimage);
        add_video = findViewById(R.id.posting_create_addvideo);
        videoView = findViewById(R.id.posting_create_myvideo); // 비디오 첨부 시, 자동 재생되는 곳
        thumbnail = findViewById(R.id.posting_create_myvideoThumbnail);
        playicon = findViewById(R.id.posting_create_playicon);
        playicon.setVisibility(View.GONE);

        thumbdelete = findViewById(R.id.posting_create_myvideoThumbnail_delete);
        imgdelete = findViewById(R.id.posting_create_myimage_delete);

        r_img = findViewById(R.id.posting_create_img);
        r_thumb = findViewById(R.id.posting_create_thumb);

    }

    private void touchEvent() {
        add_image.setOnClickListener(v -> {
            Dexter.withContext(Add_Posting_Create.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    Intent mIntent = new Intent();
                    mIntent.setType("image/*");
                    mIntent.setAction(Intent.ACTION_GET_CONTENT);
                    launcher.launch(mIntent);
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();

        });

        add_video.setOnClickListener(v -> {

            Dexter.withContext(Add_Posting_Create.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    Intent mIntent2 = new Intent();
                    mIntent2.setType("video/*");
                    mIntent2.setAction(Intent.ACTION_GET_CONTENT);
                    launcher2.launch(mIntent2);
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();

        });

        posting.setOnClickListener(v -> {
            finish();
            Toast.makeText(getApplicationContext(), "업로드~ 로딩...!", Toast.LENGTH_SHORT).show();
        });

        thumbnail.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "동영상 미리보기 액티비티", Toast.LENGTH_SHORT).show();
        });

        my_image.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "이미지 전체화면", Toast.LENGTH_SHORT).show();
        });

        imgdelete.setOnClickListener(v -> {
            my_image.setImageBitmap(null);
            r_img.setVisibility(View.GONE);
        });

        thumbdelete.setOnClickListener(v -> {
            thumbnail.setImageBitmap(null);
            r_thumb.setVisibility(View.GONE);
        });

    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        r_thumb.setVisibility(View.GONE);
                        r_img.setVisibility(View.VISIBLE);

                        Intent intent = result.getData();
                        Uri uri = intent.getData();

                        Glide.with(Add_Posting_Create.this)
                                .load(uri)
                                .into(my_image);


                        Log.i("my_image : ", "");
                        Log.i("video_thumbnail : ", "");
                    }
                }
            });

    ActivityResultLauncher<Intent> launcher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        Uri uri = intent.getData();
                        videoView.setVideoURI(uri);
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });
                        r_thumb.setVisibility(View.VISIBLE);
                        r_img.setVisibility(View.GONE);
                        Glide.with(Add_Posting_Create.this)
                                .load(createThumbnail(Add_Posting_Create.this, uri.toString()))
                                .into(thumbnail);
//                        thumbnail.setImageBitmap(createThumbnail(Add_Posting_Create.this, uri.toString()));

                        Log.i("my_image : ", "");
                        Log.i("video_thumbnail : ", "");
                    }
                }
            });

    public static Bitmap createThumbnail(Context activity, String path) {
        MediaMetadataRetriever mediaMetadataRetriever = null;
        Bitmap bitmap = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(activity, Uri.parse(path));
            bitmap = mediaMetadataRetriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
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
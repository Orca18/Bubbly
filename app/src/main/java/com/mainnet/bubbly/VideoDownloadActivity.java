package com.mainnet.bubbly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.chatting.util.GetDate;
import com.mainnet.bubbly.config.Config;

import java.io.File;

/**
 * 동영상 다운로드 화면
 * */
public class VideoDownloadActivity extends AppCompatActivity {
    androidx.appcompat.widget.Toolbar toolbar;
    ImageView thumbnail;
    ImageView download;
    ImageView platBtn;
    String thumbnailFileName;
    String videoFileName;
    ProgressBar progressBar_download;
    TextView download_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_download);

        toolbar = findViewById(R.id.toolbar_fullscreen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 다운로드 버튼
        download = findViewById(R.id.download);
        // 동영상 시작 버튼
        platBtn = findViewById(R.id.play_btn);
        // 썸네일 이미지뷰
        thumbnail = findViewById(R.id.thumbnail);

        progressBar_download = findViewById(R.id.progressBar_download);
        download_text = findViewById(R.id.download_text);

        // 썸네일 파일명
        thumbnailFileName = getIntent().getStringExtra("thumbnailFileName");

        // 썸네일 넣기
        Glide.with(this).load(Config.cloudfront_addr + thumbnailFileName).into(thumbnail);

        // 비디오 파일명
        videoFileName = getIntent().getStringExtra("videoFileName");

        setClickListener();

        // 최초입장시엔 무조건 동영상재생화면으로 이동.
        moveToVideoPlayAct();
    }

    public void setClickListener(){
        // 동영상 플레이 클릭 시 동영상 재생화면으로 이동
        platBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 동영상 재생화면으로 이동
                moveToVideoPlayAct();
            }
        });


        // 다운로드
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비디오 다운로드
                AWSCredentials awsCredentials = new BasicAWSCredentials(Config.aws_access_key, Config.aws_secret_key);
                AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

                TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(VideoDownloadActivity.this).build();
                TransferNetworkLossHandler.getInstance(VideoDownloadActivity.this);

                // 동영상 확장자
                String extension = videoFileName.substring(videoFileName.indexOf("."));

                String videoFileName2 = GetDate.getTodayDateWithTime().replace(":", "-").replace(" ", "") + extension;

                File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/bubbly");
                boolean success = true;

                if (!storageDir.exists()) {
                    success = storageDir.mkdirs();
                }

                if (success) {
                    File videoFile = new File(storageDir, videoFileName2);
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    TransferObserver downloadObserver = transferUtility.download(Config.s3_bucket_name, videoFileName
                            , videoFile);

                    downloadObserver.setTransferListener(new TransferListener() {
                        @Override
                        public void onStateChanged(int id, TransferState state) {
                            if(state == TransferState.IN_PROGRESS){
                                progressBar_download.setVisibility(View.VISIBLE);
                                download_text.setVisibility(View.VISIBLE);
                                platBtn.setVisibility(View.GONE);
                            }

                            if (state == TransferState.COMPLETED) {
                                // 저장 완료
                                Log.d("저장 완료!", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/bubbly");

                                progressBar_download.setVisibility(View.GONE);
                                download_text.setVisibility(View.GONE);
                                platBtn.setVisibility(View.VISIBLE);

                                Toast.makeText(VideoDownloadActivity.this, "저장 완료", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                            Log.d("변화", "11");

                        }

                        @Override
                        public void onError(int id, Exception ex) {
                            Log.d("저장 실패", ex.getMessage());
                        }
                    });
                }
            }
        });
    }

    public void moveToVideoPlayAct(){
        // 동영상 재생화면으로 이동
        Intent intent = new Intent(VideoDownloadActivity.this, Video_Play.class);
        String url = Config.cloudfront_addr + videoFileName;

        intent.putExtra("videoUrl", url);
        // FLAG_ACTIVITY_NEW_TASK
        VideoDownloadActivity.this.startActivity(intent);
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
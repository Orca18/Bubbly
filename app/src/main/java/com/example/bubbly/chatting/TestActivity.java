package com.example.bubbly.chatting;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

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
import com.example.bubbly.R;
import com.example.bubbly.chatting.util.GetDate;
import com.example.bubbly.config.Config;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        try {
            new Config(this).getConfigData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //download();
    }

    public void download() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(Config.aws_access_key, Config.aws_secret_key);
        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(this).build();
        TransferNetworkLossHandler.getInstance(this);

        String videoFileName = GetDate.getTodayDateWithTime().replace(":", "-").replace(" ", "") + ".mp4";

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/bubbly");
        boolean success = true;

        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }

        if (success) {
            File videoFile = new File(storageDir, videoFileName);
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }

            TransferObserver downloadObserver = transferUtility.download("bubbly-s3", "aca6ebcc2fd5831b419bc1399ec200e9.mp4"
                    , videoFile);

            downloadObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.COMPLETED) {
                        // 저장 완료
                        Log.d("저장 완료!", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/bubbly");
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
}
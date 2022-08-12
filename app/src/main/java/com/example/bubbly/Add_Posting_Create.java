package com.example.bubbly;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.FileUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Add_Posting_Create extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();


    private ArrayList<Uri> imageList;
    private final int REQUEST_CODE_READ_STORAGE = 2;
    SharedPreferences preferences;
    String user_id;

    Toolbar toolbar;
    ImageView add_image, add_video, my_image, thumbnail;
    TextView posting;
    EditText et_content;
    VideoView videoView;
    ImageView playicon, imgdelete, thumbdelete;
    RelativeLayout r_img, r_thumb;

    String post_id,post_content,post_file,post_mention;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_create);

        initiallize();
        touchEvent();
        imageList = new ArrayList<>();

        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        post_content = intent.getStringExtra("post_content");
        post_file = intent.getStringExtra("post_file");


        if(post_content != null){
            et_content.setText(post_content);
        }

        System.out.println("post_file"+post_file);
        if(post_file != null){
            imageList.add(Uri.parse("https://d2gf68dbj51k8e.cloudfront.net/"+post_file));
            r_thumb.setVisibility(View.GONE);
            r_img.setVisibility(View.VISIBLE);
            Glide.with(Add_Posting_Create.this)
                    .load("https://d2gf68dbj51k8e.cloudfront.net/"+post_file)
                    .into(my_image);
        }



    }


    private void initiallize() {
        toolbar = findViewById(R.id.toolbar_creating);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_content = findViewById(R.id.et_content);

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

        preferences = getSharedPreferences("novarand",MODE_PRIVATE);

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
            if(post_id == null){
                createPost(); // 게시글 작성
            }
            else{
                updatePost(); // 게시글 수정
            }
        });

        thumbnail.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "동영상 미리보기 액티비티", Toast.LENGTH_SHORT).show();
        });

        my_image.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "이미지 전체화면", Toast.LENGTH_SHORT).show();
        });

        imgdelete.setOnClickListener(v -> {
            imageList.clear();
            my_image.setImageBitmap(null);
            r_img.setVisibility(View.GONE);
        });

        thumbdelete.setOnClickListener(v -> {
            thumbnail.setImageBitmap(null);
            r_thumb.setVisibility(View.GONE);
        });

    }


    public void createPost(){
        List<MultipartBody.Part> parts = new ArrayList<>(); //파일 정보를 담는다
        //arraylist값이 null이 아니라면 넣는 작업을 진행한다.
        if (imageList != null) {
            for (int i = 0; i < imageList.size(); i++) {
                //parts 에 파일 정보들을 저장 시킵니다. 파트네임은 임시로 설정이 되고, uri값을 통해서 실제 파일을 담는다
                parts.add(prepareFilePart("image"+i, imageList.get(i))); //partName 으로 구분하여 이미지를 등록한다. 그리고 파일객체에 값을 넣어준다.
            }
        }
        RequestBody size = createPartFromString(""+parts.size());
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값
        ApiInterface createPost_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<String> call = createPost_api.createPost(user_id,et_content.getText().toString(),size, parts,"n","0","1");
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e("게시글 생성", response.body().toString());
                    Intent mIntent = new Intent(getApplicationContext(), MM_Home.class);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(mIntent);
                    finish();
                    Toast.makeText(getApplicationContext(), "게시글 등록 완료!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("게시글 생성 에러", t.getMessage());
            }
        });
    }


    public void updatePost(){
        List<MultipartBody.Part> parts = new ArrayList<>(); //파일 정보를 담는다
        //arraylist값이 null이 아니라면 넣는 작업을 진행한다.
        if (imageList != null) {
            for (int i = 0; i < imageList.size(); i++) {
                //parts 에 파일 정보들을 저장 시킵니다. 파트네임은 임시로 설정이 되고, uri값을 통해서 실제 파일을 담는다
                parts.add(prepareFilePart("image"+i, imageList.get(i))); //partName 으로 구분하여 이미지를 등록한다. 그리고 파일객체에 값을 넣어준다.
            }
        }
        RequestBody size = createPartFromString(""+parts.size());
        ApiInterface updatePost_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<String> call = updatePost_api.updatePost(post_id,et_content.getText().toString(),size, parts,post_mention,post_mention);
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    Log.e("게시글 수정", response.body().toString());
                    Intent mIntent = new Intent(getApplicationContext(), MM_Home.class);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(mIntent);
                    finish();
                    Toast.makeText(getApplicationContext(), "게시글 수정 완료!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("게시글 수정 에러", t.getMessage());
            }
        });
    }


    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        imageList.clear();
                        r_thumb.setVisibility(View.GONE);
                        r_img.setVisibility(View.VISIBLE);

                        Intent intent = result.getData();
                        Uri uri = intent.getData();

                        Glide.with(Add_Posting_Create.this)
                                .load(uri)
                                .into(my_image);

                        Log.e("uri", String.valueOf(uri));
                        imageList.add(uri);


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


    //파일 파트를 준비하는 매서드 (파트이름, 그리고 파일의 Uri)
    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // use the FileUtils to get the actual file by uri uri를 통해서 실제 파일을 받아온다.
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file 리퀘스트바디를 파일로부터 만든다.
        RequestBody requestFile = RequestBody.create (MediaType.parse(FileUtils.MIME_TYPE_IMAGE), file);

        // MultipartBody.Part is used to send also the actual file name //
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    //문자열로 부터 파트 바디를 생성한다//
    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_TEXT), descriptionString);
    }

}
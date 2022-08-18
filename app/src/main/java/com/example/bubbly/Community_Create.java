package com.example.bubbly;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bubbly.kim_util_test.Kim_ApiClient;
import com.example.bubbly.kim_util_test.Kim_ApiInterface;
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

public class Community_Create extends AppCompatActivity {
    // 뒤로가기 시간
    private long backKeyPressedTime = 0;
    private Toast toast;

    Toolbar toolbar;
    LinearLayout done;
    ImageView thumb;
    EditText title, desc;


    // 1) community_owner_id, 2) writer_name, 3) community_name, 4) community_desc, 5) profile_file
    // 사용자 아이디       /
    SharedPreferences preferences;
    String user_id;
    private ArrayList<Uri> imageList;
    File imagefile;

    // 이미지 관련
    RelativeLayout r_img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);

        preferences = getSharedPreferences("novarand",MODE_PRIVATE);
        imageList = new ArrayList<>();

        initialize();
        linsteners();

    }

    private void linsteners() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 조건이 맞을 때만 업로드
                // TODO API 36. 커뮤니티 정보 저장 http://3.39.84.115:80/community/createCommunity
                //"Multipart
                //community_owner_id,
                //writer_name,
                //community_name,
                //community_desc,
                //profile_file)"
                createCom();
                finish();
            }
        });

        thumb.setOnClickListener(v -> {
            Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    Intent mIntent = new Intent();
                    mIntent.setType("image/*");
                    mIntent.setAction(Intent.ACTION_GET_CONTENT);
                    //런처
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
    }



    private void initialize() {

        toolbar = findViewById(R.id.community_create_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        done = findViewById(R.id.community_create_done);
        thumb = findViewById(R.id.community_create_image);
        title = findViewById(R.id.community_create_title);
        desc = findViewById(R.id.community_create_desc);
    }








    private void createCom() {
        List<MultipartBody.Part> parts = new ArrayList<>(); //파일 정보를 담는다
        MultipartBody.Part file = null;
        //arraylist값이 null이 아니라면 넣는 작업을 진행한다.
        if (imageList.size() != 0) {
            //parts 에 파일 정보들을 저장 시킵니다. 파트네임은 임시로 설정이 되고, uri값을 통해서 실제 파일을 담는다
            // !!! 이미지 하나라서 0 으로 지정해둠
            file = prepareFilePart("image" + 0, imageList.get(0));
        }


        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값
        Kim_ApiInterface kim_api = Kim_ApiClient.getApiClient().create(Kim_ApiInterface.class);
        Call<String> call = kim_api.createCommunity(user_id, null, title.getText().toString(), null, "뭐야", file);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // 성공 시, 참여 정보 또 저장 // TODO 생성한 커뮤니티 ID 가져와서 넣어야됨
//                    Call<String> call2 = kim_api.createCommunityParicipant(user_id, "여기에!!!!!");
//                    call2.enqueue(new Callback<String>() {
//                        @Override
//                        public void onResponse(Call<String> call, Response<String> response) {
//
//                            finish();
//                        }
//
//                        @Override
//                        public void onFailure(Call<String> call, Throwable t) {
//                            Toast.makeText(getApplicationContext(), "참여 정보 저장에 오류",Toast.LENGTH_SHORT).show();
//                        }
//                    });

                    finish();

//                    Toast.makeText(getApplicationContext(), "커뮤니티 생성 성공! TODO 해당 커뮤 화면으로 이동", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("게시글 생성 에러", t.getMessage());
            }
        });


    }


    //파일 파트를 준비하는 매서드 (파트이름, 그리고 파일의 Uri)
    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // use the FileUtils to get the actual file by uri uri를 통해서 실제 파일을 받아온다.
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file 리퀘스트바디를 파일로부터 만든다.
        RequestBody requestFile = RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_IMAGE), file);

        // MultipartBody.Part is used to send also the actual file name //
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    //문자열로 부터 파트 바디를 생성한다//
    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_TEXT), descriptionString);
    }






    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        imageList.clear();

                        Intent intent = result.getData();
                        Uri uri = intent.getData();

                        Glide.with(getApplicationContext())
                                .load(uri)
                                .centerCrop()
                                .into(thumb);

                        imageList.add(uri);

                    }
                }
            });




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                    backKeyPressedTime = System.currentTimeMillis();
                    toast = Toast.makeText(this, "작성 중인거 있을 때, 질문", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                    finish();
                    toast.cancel();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //뒤로가기 했을 때
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "작성 중인거 있을 때, 질문", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        } else {
            super.onBackPressed();
        }
    }

}
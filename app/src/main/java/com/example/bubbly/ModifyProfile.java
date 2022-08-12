package com.example.bubbly;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.FileUtils;
import com.example.bubbly.retrofit.user_Response;
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

public class ModifyProfile extends AppCompatActivity {

    ImageView iv_back,iv_user_image;
    EditText et_id_modify;
    Button bt_add;

    SharedPreferences preferences;
    private ArrayList<Uri> imageList;

    String user_id,getEmail_addr,getLogin_id,getPhone_num,update_user_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        iv_back = findViewById(R.id.iv_back);
        iv_user_image = findViewById(R.id.iv_user_image);
        et_id_modify = findViewById(R.id.et_id_modify);
        bt_add = findViewById(R.id.bt_add);

        imageList = new ArrayList<>();
        selectUserInfo();

        iv_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImage();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), MM_Profile.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mIntent);
            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });

    } // onCreate 닫는곳

    public void updateUserInfo(){
        List<MultipartBody.Part> parts = new ArrayList<>(); //파일 정보를 담는다
        //arraylist값이 null이 아니라면 넣는 작업을 진행한다.
        if (imageList != null) {
            for (int i = 0; i < imageList.size(); i++) {
                //parts 에 파일 정보들을 저장 시킵니다. 파트네임은 임시로 설정이 되고, uri값을 통해서 실제 파일을 담는다
                parts.add(prepareFilePart("image"+i, imageList.get(i))); //partName 으로 구분하여 이미지를 등록한다. 그리고 파일객체에 값을 넣어준다.
            }
        }
        RequestBody size = createPartFromString(""+parts.size());

        preferences = getSharedPreferences("novarand",MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값
        ApiInterface updateUserInfo_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<String> call = updateUserInfo_api.updateUserInfo(et_id_modify.getText().toString(),getEmail_addr,getPhone_num,"나중에수정",size, parts,user_id);
        call.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
            {
                Log.e("로그인 에러", t.getMessage());
            }
        });
    }

    public void selectUserInfo(){
        Log.e("dd","dddd");
        preferences = getSharedPreferences("novarand",MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값
        ApiInterface selectUserInfo_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<user_Response>> call = selectUserInfo_api.selectUserInfo(user_id);
        call.enqueue(new Callback<List<user_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<user_Response>> call, @NonNull Response<List<user_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<user_Response> responseResult = response.body();
                    Log.e("dd",responseResult.get(0).getLogin_id());
                    et_id_modify.setText(responseResult.get(0).getLogin_id());
                    getEmail_addr = responseResult.get(0).getEmail_addr();
                    getLogin_id = responseResult.get(0).getLogin_id();
                    getPhone_num = responseResult.get(0).getPhone_num();

                    Glide.with(ModifyProfile.this)
                            .load("https://d2gf68dbj51k8e.cloudfront.net/"+responseResult.get(0).getProfile_file_name())
                            .circleCrop()
                            .into(iv_user_image);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<user_Response>> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });
    }

    public void profileImage(){
        Dexter.withContext(ModifyProfile.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
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

    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        Uri uri = intent.getData();

                        Glide.with(ModifyProfile.this)
                                .load(uri)
                                .circleCrop()
                                .into(iv_user_image);
                        imageList.add(uri);

                    }
                }
            });

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
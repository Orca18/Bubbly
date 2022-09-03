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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bubbly.config.Config;
import com.example.bubbly.model.UserInfo;
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
import java.util.regex.Pattern;

import io.swagger.annotations.Api;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class ModifyProfile extends AppCompatActivity {
    Toolbar toolbar;
    ImageButton bt_change_user_image;
    ImageView iv_back,iv_user_image;
    EditText et_nick_modify, et_self_info_modify;
    Button bt_add;


    //SharedPreferences preferences;
    private ArrayList<Uri> imageList;

    //String user_id,getEmail_addr,getLogin_id,getPhone_num,update_user_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);
        // 툴바
        toolbar = findViewById(R.id.toolbar_modifyProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iv_back = findViewById(R.id.iv_back);
        bt_change_user_image = findViewById(R.id.bt_change_user_image);
        iv_user_image = findViewById(R.id.iv_user_image);
        et_nick_modify = findViewById(R.id.et_nick_modify);
        et_self_info_modify = findViewById(R.id.et_self_info_modify);
        bt_add = findViewById(R.id.bt_add);

        imageList = new ArrayList<>();
        selectUserInfo(); //사용자 프로필 정보 가져와서 표시하기

        bt_change_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImage();
            }
        });

//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(getApplicationContext(), MM_Profile.class);
//                mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(mIntent);
//            }
//        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });

    } // onCreate 닫는곳

    public void updateUserInfo(){
        //login id 정규식 확인
//        String idRex = "^([A-Za-z0-9]*)$";  //영숫자만 가능, 띄어쓰기 불가
//        Pattern pattern = Pattern.compile(idRex);
            //자기소개 100자 이내 확인
        if(et_nick_modify.getText().toString().length()<=100){
            List<MultipartBody.Part> parts = new ArrayList<>(); //파일 정보를 담는다
            //arraylist값이 null이 아니라면 넣는 작업을 진행한다.
            if (imageList != null) {
                for (int i = 0; i < imageList.size(); i++) {
                    //parts 에 파일 정보들을 저장 시킵니다. 파트네임은 임시로 설정이 되고, uri값을 통해서 실제 파일을 담는다
                    parts.add(prepareFilePart("image"+i, imageList.get(i))); //partName 으로 구분하여 이미지를 등록한다. 그리고 파일객체에 값을 넣어준다.
                }
            }
            RequestBody size = createPartFromString(""+parts.size());
//        preferences = getSharedPreferences("novarand",MODE_PRIVATE);
//        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

            //변경사항 static에 저장
            UserInfo.user_nick = et_nick_modify.getText().toString();
            UserInfo.self_info = et_self_info_modify.getText().toString();

            //api 전송 변수 설정
            String login_id = UserInfo.login_id;
            String email_addr = UserInfo.email_addr;
            String phone_num = UserInfo.phone_num;
            String nick_name = UserInfo.user_nick;
            String user_id = UserInfo.user_id;
            String self_info = UserInfo.self_info;

            ApiInterface updateUserInfo_api = ApiClient.getApiClient(this).create(ApiInterface.class);
            Call<String> call = updateUserInfo_api.updateUserInfo(login_id, email_addr, phone_num, nick_name, "test",parts, user_id, self_info);
            call.enqueue(new Callback<String>()
            {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        System.out.println(response.body());
                        if(response.body().equals("success")){
                            callbackToUpdateUserInfo(updateUserInfo_api, user_id, new callback() {
                                @Override
                                public void updateUserInfo() {
                                    Toast.makeText(getApplicationContext(), "회원 정보가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }else{

                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                {
                    Log.e("로그인 에러", t.getMessage());
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "자기소개 100자 이내로 작성해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    //사용자 프로필 정보 가져와서 표시하기
    public void selectUserInfo(){
        //이미지. 닉네임, id, 자기소개, 팔로잉, 팔로워
        if(UserInfo.profile_file_name!=null && !UserInfo.profile_file_name.equals("")){
            Glide.with(ModifyProfile.this)
                    .load(UserInfo.profile_file_name)
                    .circleCrop()
                    .into(iv_user_image);
        }else{
            //아무런 처리하지 않음. 레이아웃에 설정된 default 값 표시
        }
        if(UserInfo.user_nick!=null && !UserInfo.user_nick.equals("")){
            et_nick_modify.setText(UserInfo.user_nick);
        }else{
            et_nick_modify.setText(UserInfo.login_id);
        }
        if(UserInfo.self_info!=null && !UserInfo.self_info.equals("")){
            et_self_info_modify.setText(UserInfo.self_info);
        }else{
            //아무런 처리하지 않음. 레이아웃에 설정된 default 값 표시
        }
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


                        List<MultipartBody.Part> parts = new ArrayList<>(); //파일 정보를 담는다
                        //arraylist값이 null이 아니라면 넣는 작업을 진행한다.
                        if (imageList != null) {
                            for (int i = 0; i < imageList.size(); i++) {
                                //parts 에 파일 정보들을 저장 시킵니다. 파트네임은 임시로 설정이 되고, uri값을 통해서 실제 파일을 담는다
                                parts.add(prepareFilePart("image"+i, imageList.get(i))); //partName 으로 구분하여 이미지를 등록한다. 그리고 파일객체에 값을 넣어준다.
                                System.out.println(""+parts.get(i));
                            }
                        }

                        //updateUserProfile http 요청
                        ApiInterface selectUserInfo_api = ApiClient.getApiClient(ModifyProfile.this).create(ApiInterface.class);
                        Call<String> call = selectUserInfo_api.updateUserProfile(parts,UserInfo.user_id);
                        call.enqueue(new Callback<String>()
                        {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                            {
                                System.out.println(response.body());
                                if (response.isSuccessful() && response.body() != null)
                                {
                                    if(response.body().equals("success")){
                                        callbackToUpdateUserInfo(selectUserInfo_api, UserInfo.user_id, new callback() {
                                            @Override
                                            public void updateUserInfo() {
                                                Toast.makeText(getApplicationContext(), "이미지가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getApplicationContext(), "이미지 변경에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                            {
                                Log.e("에러", t.getMessage());
                            }
                        });

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

    private void callbackToUpdateUserInfo(ApiInterface api, String user_id, callback callback){
        Call<List<user_Response>> call_userInfo = api.selectUserInfo(user_id);
        call_userInfo.enqueue(new Callback<List<user_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<user_Response>> call, @NonNull Response<List<user_Response>> response)
            {
                System.out.println(response.body());
                //수신한 회원정보를 스태틱으로 저장한다.
                List<user_Response> responseResult = response.body();
                UserInfo.user_id = responseResult.get(0).getUser_id();
                UserInfo.login_id = responseResult.get(0).getLogin_id();
                UserInfo.email_addr = responseResult.get(0).getEmail_addr();
                UserInfo.novaland_account_addr = responseResult.get(0).getNovaland_account_addr();
                UserInfo.phone_num = responseResult.get(0).getPhone_num();
                UserInfo.user_nick = responseResult.get(0).getUser_nick();
                UserInfo.self_info = responseResult.get(0).getSelf_info();
                if(responseResult.get(0).getProfile_file_name()!=null && !responseResult.get(0).getProfile_file_name().equals("")){
                    UserInfo.profile_file_name = Config.cloudfront_addr+responseResult.get(0).getProfile_file_name();
                }
                callback.updateUserInfo();
                //프로필 변경 페이지로 이동
//                Toast.makeText(getApplicationContext(), "회원 정보가 변경되었습니다.", Toast.LENGTH_SHORT).show();
//                Intent mIntent = new Intent(getApplicationContext(), MM_Profile.class);
//                mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(mIntent);
            }
            @Override
            public void onFailure(@NonNull Call<List<user_Response>> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });
    }

    private interface callback{
        void updateUserInfo();
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
package com.mainnet.bubbly;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.kim_util_test.Kim_ApiClient;
import com.mainnet.bubbly.kim_util_test.Kim_ApiInterface;
import com.mainnet.bubbly.kim_util_test.Kim_Com_Info_Response;
import com.mainnet.bubbly.retrofit.FileUtils;
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

public class Community_Info_Edit extends AppCompatActivity {

    Toolbar toolbar;

    RelativeLayout rl_maintitle;
    ImageView com_image;
    TextView com_image_tv;
    EditText et_com_title, et_com_desc, et_com_rule;
    Button edit_done;

    SharedPreferences preferences;
    String com_id, com_desc, com_rule, com_name, com_owner, user_id;


    private ArrayList<Uri> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_info_edit);

        Intent intent = getIntent();
        com_id = intent.getStringExtra("com_id");


        preferences = getSharedPreferences("novarand", MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // ???????????? user_id???
        imageList = new ArrayList<>();


        initialize();
        listeners();
        GetComInfo();

    }
    private void initialize(){
        toolbar = findViewById(R.id.com_info_edit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rl_maintitle = findViewById(R.id.com_info_edit_rl);
        com_image = findViewById(R.id.com_info_edit_titleimage);
        com_image_tv = findViewById(R.id.com_info_edit_titleimage_tv);
        et_com_title = findViewById(R.id.com_info_edit_name);
        et_com_desc = findViewById(R.id.com_info_edit_desc);
        et_com_rule = findViewById(R.id.com_info_edit_rule);
        edit_done = findViewById(R.id.com_info_edit_done);
    }


    private void listeners() {
        edit_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ????????? ???, ?????? ???????????? ?????? ?????? ?????????... ????????? ???????????????
                updateCommunity();
           }
        });


        rl_maintitle.setOnClickListener(v -> {
            Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    Intent mIntent = new Intent();
                    mIntent.setType("image/*");
                    mIntent.setAction(Intent.ACTION_GET_CONTENT);
                    //??????
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

    private void updateCommunity() {
        List<MultipartBody.Part> parts = new ArrayList<>(); //?????? ????????? ?????????
        MultipartBody.Part file = null;
        //arraylist?????? null??? ???????????? ?????? ????????? ????????????.
        if (imageList.size() != 0) {
            //parts ??? ?????? ???????????? ?????? ????????????. ??????????????? ????????? ????????? ??????, uri?????? ????????? ?????? ????????? ?????????
            // !!! ????????? ???????????? 0 ?????? ????????????
            file = prepareFilePart("image" + 0, imageList.get(0));
        } else {
            file = null;
        }


        user_id = preferences.getString("user_id", ""); // ???????????? user_id???
        Kim_ApiInterface kim_api = Kim_ApiClient.getApiClient(Community_Info_Edit.this).create(Kim_ApiInterface.class);
        Call<String> call = kim_api.updateCommunity(et_com_title.getText().toString(), et_com_desc.getText().toString(),null, com_id, file, et_com_rule.getText().toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    finish();
                    Log.i("????????????", "????????? ????????? ??????"+imageList.size());
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("????????? ?????? ??????", t.getMessage());
            }
        });
    }


    //?????? ????????? ???????????? ????????? (????????????, ????????? ????????? Uri)
    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // use the FileUtils to get the actual file by uri uri??? ????????? ?????? ????????? ????????????.
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file ????????????????????? ??????????????? ?????????.
        RequestBody requestFile = RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_IMAGE), file);

        // MultipartBody.Part is used to send also the actual file name //
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    //???????????? ?????? ?????? ????????? ????????????//
    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_TEXT), descriptionString);
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
                                .into(com_image);

                        imageList.add(uri);

                    }
                }
            });


    private void GetComInfo() {
        Kim_ApiInterface api = Kim_ApiClient.getApiClient(Community_Info_Edit.this).create(Kim_ApiInterface.class);
        Call<List<Kim_Com_Info_Response>> call = api.selectCommunityUsingCommunityId(com_id);
        call.enqueue(new Callback<List<Kim_Com_Info_Response>>() {
            @Override
            public void onResponse(Call<List<Kim_Com_Info_Response>> call, Response<List<Kim_Com_Info_Response>> response) {

                com_name = response.body().get(0).getCommunity_name();
                com_owner = response.body().get(0).getCommunity_owner_id();
                com_desc = response.body().get(0).getCommunity_desc();
                com_rule = response.body().get(0).getRule();

                et_com_title.setText(com_name);
                et_com_desc.setText(com_desc);
                et_com_rule.setText(com_rule);

                Glide.with(getApplicationContext()) //?????? ????????? Context??? ?????? ??????
                        .load(Config.cloudfront_addr+response.body().get(0).getProfile_file_name()) //URL, URI ?????? ???????????? ????????? ??????
                        .centerCrop()
                        .into(com_image);


            }

            @Override
            public void onFailure(Call<List<Kim_Com_Info_Response>> call, Throwable t) {

            }
        });




    }

}
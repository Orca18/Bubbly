package com.example.bubbly;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.bubbly.config.Config;
import com.example.bubbly.controller.SnackAndToast;
import com.example.bubbly.kim_util_test.Kim_DateUtil;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.FileUtils;
import com.example.bubbly.retrofit.post_Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Post_ApplyNFT_A extends AppCompatActivity {

    Toolbar toolbar;
    EditText et_assetName, et_assetDes;
    ImageView iv_user_image, iv_media, iv_preview;
    TextView tv_usernick, tv_userid, tv_contents, tv_time;
    LinearLayout ll_item_layout, bt_apply;
    String post_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_applynft_a);

        //intent로 보낸 포스트 id 가져오기
        Intent mIntent = getIntent();
        post_id = mIntent.getStringExtra("post_id");

        // (기본) 리소스 ID 선언
        initiallize();

        //이미지 미리보기
        selectPost();

        //클릭 리스너
        clickListeners();



    }

    private void selectPost(){
        ApiInterface api = ApiClient.getApiClient(this).create(ApiInterface.class);
        Call<List<post_Response>> call = api.selectPostUsingPostId(post_id,UserInfo.user_id);
        call.enqueue(new Callback<List<post_Response>>() {
            @Override
            public void onResponse(@NonNull Call<List<post_Response>> call, @NonNull Response<List<post_Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<post_Response> responseResult = response.body();
                    for (int i = 0; i < responseResult.size(); i++) {
                        if(responseResult.get(i).getProfile_file_name()!=null&&!responseResult.get(i).getProfile_file_name().equals("")){
                            Glide.with(getApplicationContext())
                                    .load(Config.cloudfront_addr+responseResult.get(i).getProfile_file_name())
                                    .into(iv_user_image);
                        }
                        if(responseResult.get(i).getNick_name()!=null&&!responseResult.get(i).getNick_name().equals("")){
                            tv_usernick.setText(responseResult.get(i).getNick_name());
                        }else{
                            tv_usernick.setText(responseResult.get(i).getLogin_id());
                        }
                        tv_userid.setText(responseResult.get(i).getLogin_id());
                        //작성일로부터 현재시간까지 차이
//                        try {
//                            //현재시간
//                            Date currentTime = new Date();
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                            Date createdTime = sdf.parse(responseResult.get(i).getCre_datetime());
//                            long diff = currentTime.getTime() - createdTime.getTime();
//                            String timeDiff;
//                            int hours = (int)(diff/(60*60*1000));
//                            timeDiff = hours+"H";
//                            if(hours>24){
//                                int days = (int)(hours/24);
//                                timeDiff = days+"D";
//                                if(days>30){
//                                    int months = (int)(days/30);
//                                    timeDiff = months+"M";
//                                    if(months>12){
//                                        int years = (int)(months/12);
//                                        timeDiff = years+"Y";
//                                    }
//                                }
//                            }
//                            tv_time.setText(timeDiff);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
                        String a = null;
                        try {
                            a = Kim_DateUtil.beforeTime(getDate(responseResult.get(i).getCre_datetime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        // SNS 형식 시간
                        tv_time.setText(a);
                        tv_contents.setText(responseResult.get(i).getPost_contents());
                        if(responseResult.get(i).getFile_save_names()!=null&&!responseResult.get(i).getFile_save_names().equals("")){
                            Glide.with(getApplicationContext())
                                    .load(Config.cloudfront_addr+responseResult.get(i).getFile_save_names())
                                    .into(iv_media);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<post_Response>> call, @NonNull Throwable t) {
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
            }
        });
    }


    // 리소스 아이디 선언
    private void initiallize() {
        // 툴바
        toolbar = findViewById(R.id.toolbar_applyNFT);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_assetName = findViewById(R.id.et_assetName_applyNFT);
        iv_user_image = findViewById(R.id.iv_user_image_applyNFT);
        tv_usernick = findViewById(R.id.tv_user_nick_applyNFT);
        tv_userid = findViewById(R.id.tv_user_id_applyNFT);
        tv_time = findViewById(R.id.tv_time_applyNFT);
        tv_contents = findViewById(R.id.tv_content_applyNFT);
        iv_media = findViewById(R.id.iv_media_applyNFT);
        ll_item_layout = findViewById(R.id.ll_item_layout_applyNFT);
        bt_apply = findViewById(R.id.bt_apply_applyNFT);
    }


    private void clickListeners() {
        bt_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //post 미리보기 비트맵 변환
                Bitmap bitmap = Bitmap.createBitmap(ll_item_layout.getWidth(),ll_item_layout.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                ll_item_layout.draw(canvas);
                System.out.println("비트맵"+bitmap);
                //이미지 파일 변환
                File file = bitmapConvertFile(bitmap,""+System.currentTimeMillis());
                System.out.println("파일"+file);
                //uri 생성
                Uri uri = Uri.fromFile(file);


                //멀티파트 파트 생성
                List<MultipartBody.Part> parts = new ArrayList<>(); //파일 정보를 담는다
                //parts 에 파일 정보들을 저장 시킵니다. 파트네임은 임시로 설정이 되고, uri값을 통해서 실제 파일을 담는다
                parts.add(prepareFilePart("image", uri)); //partName 으로 구분하여 이미지를 등록한다. 그리고 파일객체에 값을 넣어준다.
                System.out.println("니모닉"+UserInfo.mnemonic+et_assetName.getText().toString()+uri+post_id+parts.get(0).body());

                //파일 전송
                ApiInterface api = ApiClient.getApiClient(getApplicationContext()).create(ApiInterface.class);
                Call<String> call = api.nftCreation(UserInfo.mnemonic,et_assetName.getText().toString(),"",UserInfo.user_id,post_id,parts);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if(response.body().equals("success")){
                                //임시 저장 파일 삭제
                                ViewGroup view = (ViewGroup) v.findViewById(android.R.id.content);
                                new SnackAndToast().createToast(getApplicationContext(),"NFT가 생성되었습니다.");
                            }else{
                                Log.e("nft 생성 실패","response fail");
                            }
                            file.delete();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("nft 생성 실패", t.getMessage());
                        file.delete();
                        ViewGroup view = (ViewGroup) v.findViewById(android.R.id.content);
                        new SnackAndToast().createToast(getApplicationContext(),"NFT 생성에 실패하었습니다.");

                    }
                });
                //블록체인 처리가 늦어 timeout되므로 결과와 무관하게 activity 이동
                Toast.makeText(getApplicationContext(), "NFT신청 완료. 몇초 뒤 NFT 목록을 확인하세요.",Toast.LENGTH_SHORT).show();
                Intent mIntent = new Intent(getApplicationContext(),MM_Profile.class);
                startActivity(mIntent);
            }
        });
    }


    // 비트맵을 파일로 변환하는 메소드
    private File bitmapConvertFile(Bitmap bitmap, String filename)
    {
        //create a file to write bitmap data
        File f = new File(getApplicationContext().getCacheDir(), filename);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
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

    public static Date getDate(String from) throws ParseException {
// "yyyy-MM-dd HH:mm:ss"
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(from);
        return date;
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

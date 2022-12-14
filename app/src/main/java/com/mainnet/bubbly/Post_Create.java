package com.mainnet.bubbly;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.controller.DialogRecyclerAdapter;
import com.mainnet.bubbly.kim_util_test.Kim_ApiClient;
import com.mainnet.bubbly.kim_util_test.Kim_ApiInterface;
import com.mainnet.bubbly.kim_util_test.Kim_JoinedCom_Response;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.FileUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Post_Create extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();


    private ArrayList<Uri> imageList;
    private ArrayList<Uri> videoList;
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

    String post_id, post_content, post_file, post_mention;
    public static TextView com;
    public static String category_com_id;
    public static String category_com_name;

    private LinearLayout linearLayoutTopicPosting; // ?????? ????????? ?????? ???
    private RecyclerView dialogRecyclerView;
    public static Dialog dialog; // ????????? Dialog ??????
    String[] text;
    public static String[] ids;

    CircleImageView cv_profile;

    String post_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_create);

        initiallize();
        touchEvent();
        imageList = new ArrayList<>();
        videoList = new ArrayList<>();
        post_type = "0"; // ????????? = 1 , ????????? = 2


        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        post_content = intent.getStringExtra("post_content");
        post_file = intent.getStringExtra("post_file");
        // ???????????? ???????????? ?????????, ?????? ??????????????? ???????????? ?????? ???????????? ???
        category_com_id = intent.getStringExtra("com_id");
        category_com_name = intent.getStringExtra("com_name");

        if(category_com_id == null){
            category_com_id = "0";
            category_com_name = "??? ??????";
        }

        com.setText(category_com_name);

        user_id = preferences.getString("user_id", ""); // ???????????? user_id???


        if (post_content != null) {
            et_content.setText(post_content);
        }


// ?????? ??? ??? ??????
//        System.out.println("post_file" + post_file);
//        if (post_file != null) {
//            imageList.add(Uri.parse(Config.cloudfront_addr + post_file));
//            r_thumb.setVisibility(View.GONE);
//            r_img.setVisibility(View.VISIBLE);
//            Glide.with(Post_Create.this)
//                    .load(Config.cloudfront_addr + post_file)
//                    .into(my_image);
//        }


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
        videoView = findViewById(R.id.posting_create_myvideo); // ????????? ?????? ???, ?????? ???????????? ???
        thumbnail = findViewById(R.id.posting_create_myvideoThumbnail);
        playicon = findViewById(R.id.posting_create_playicon);
        playicon.setVisibility(View.GONE);

        thumbdelete = findViewById(R.id.posting_create_myvideoThumbnail_delete);
        imgdelete = findViewById(R.id.posting_create_myimage_delete);

        r_img = findViewById(R.id.posting_create_img);
        r_thumb = findViewById(R.id.posting_create_thumb);

        preferences = getSharedPreferences("novarand", MODE_PRIVATE);

        com = findViewById(R.id.posting_create_category);

        cv_profile = findViewById(R.id.posting_create_profile);

        // ???????????? ?????????
        if (UserInfo.profile_file_name != null && !UserInfo.profile_file_name.equals("")) {
            Glide.with(getApplicationContext())
                    .load(UserInfo.profile_file_name)
                    .circleCrop()
                    .into(cv_profile);
        }else{
            Glide.with(getApplicationContext())
                    .load(R.drawable.blank_profile)
                    .circleCrop()
                    .into(cv_profile);
        }
    }

    private void touchEvent() {
        add_image.setOnClickListener(v -> {
            Dexter.withContext(Post_Create.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
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

            Dexter.withContext(Post_Create.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
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
            if (post_id == null) {
                createPost(); // ????????? ??????
            } else {
//                updatePost(); // ????????? ??????
            }
        });

        thumbnail.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "????????? ???????????? ????????????", Toast.LENGTH_SHORT).show();
        });

        my_image.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "????????? ????????????", Toast.LENGTH_SHORT).show();
        });

        imgdelete.setOnClickListener(v -> {
            imageList.clear();
            my_image.setImageBitmap(null);
            r_img.setVisibility(View.GONE);
        });

        thumbdelete.setOnClickListener(v -> {
            videoList.clear();
            thumbnail.setImageBitmap(null);
            r_thumb.setVisibility(View.GONE);
        });

        com.setOnClickListener(v -> {
            Log.d("???????????????", "Categories Test ???????????? ????????????");
            GetJoinedComList();
        });

    }

    private void GetJoinedComList() {
        Kim_ApiInterface api = Kim_ApiClient.getApiClient(Post_Create.this).create(Kim_ApiInterface.class);
        Call<List<Kim_JoinedCom_Response>> call = api.selectCommunityListUsingUserId(user_id);
        call.enqueue(new Callback<List<Kim_JoinedCom_Response>>() {
            @Override
            public void onResponse(@NonNull Call<List<Kim_JoinedCom_Response>> call, @NonNull Response<List<Kim_JoinedCom_Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Kim_JoinedCom_Response> responseResult = response.body();

                    Log.d("???????????????", "????????? ?????????:" + responseResult.size());

                    text = new String[responseResult.size() + 1];
                    ids = new String[responseResult.size() + 1];
                    text[0] = "??? ??????";
                    ids[0] = "0";
                    for (int i = 0; i < responseResult.size(); i++) {
                        text[i + 1] = responseResult.get(i).getCommunity_name();
                        ids[i + 1] = responseResult.get(i).getCommunity_id();

                        Log.d("???????????????", "????????? ?????? ????????????:" + responseResult.get(i).getCommunity_id());
                    }
                }

                showAlertDialogTopic();

            }

            @Override
            public void onFailure(@NonNull Call<List<Kim_JoinedCom_Response>> call, @NonNull Throwable t) {
                Log.e("????????? ???????????? ????????? ??????", t.getMessage());
            }
        });

    }

    private void showAlertDialogTopic() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        dialog = new Dialog(this);

        display.getRealSize(size);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        LayoutInflater inf = getLayoutInflater();
        View dialogView = inf.inflate(R.layout.dialog_layout, null);
        // Dialog layout ??????

        Log.d("???????????????", "Categories Test : " + text);

        lp.copyFrom(dialog.getWindow().getAttributes());
        int width = size.x;
        lp.width = width * 100 / 100; // ????????? ????????? 80% (????????? ?????? ?????????)
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // ????????? ?????? ?????? ????????????
        dialog.setContentView(dialogView); // Dialog??? ???????????? layout ??????
        dialog.setCanceledOnTouchOutside(true); // ?????? touch ??? Dialog ??????
        dialog.getWindow().setAttributes(lp); // ????????? ??????, ?????? ??? Dialog??? ??????
        ArrayList<String> arrayList = new ArrayList<>(); // recyclerView??? ????????? Array
        arrayList.addAll(Arrays.asList(text)); // Array??? ????????? ????????? Topic ??????
        /*
        ?????? 4?????? ????????? RecyclerView??? ???????????? ?????? View, Adapter?????? ????????????.
        1. RecyclerView id ??????
        2. ?????????????????? ????????? ??????????????? LinearLayoutManager ??????
           2???????????? GridLayoutManager ??? ?????? Layout??? ??????
        3. adapter??? topic Array ????????? ??????????????? ??????
        4. adapter ??????
        */
        dialogRecyclerView = dialogView.findViewById(R.id.dialogRecyclerView);
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DialogRecyclerAdapter adapter = new DialogRecyclerAdapter(arrayList);
        dialogRecyclerView.setAdapter(adapter);

        if(dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
//            dialog.setCancelable(false);
        }

        dialog.show(); // Dialog ??????
    }


    public void createPost() {
        if (et_content.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "????????? ??????????????????", Toast.LENGTH_SHORT).show();
        } else {
            List<MultipartBody.Part> parts = new ArrayList<>(); //?????? ????????? ?????????
            //arraylist?????? null??? ???????????? ?????? ????????? ????????????.
            if (imageList.size() != 0) {
                Log.i("??????????????????", "??????????????????~");
                post_type = "1";
                for (int i = 0; i < imageList.size(); i++) {
                    //parts ??? ?????? ???????????? ?????? ????????????. ??????????????? ????????? ????????? ??????, uri?????? ????????? ?????? ????????? ?????????
                    parts.add(prepareFilePart("image" + i, imageList.get(i))); //partName ?????? ???????????? ???????????? ????????????. ????????? ??????????????? ?????? ????????????.
                }
            }
            if (videoList.size() != 0) {
                post_type = "2";
                Log.i("??????????????????", "??????????????????!");
                for (int i = 0; i < videoList.size(); i++) {
                    //parts ??? ?????? ???????????? ?????? ????????????. ??????????????? ????????? ????????? ??????, uri?????? ????????? ?????? ????????? ?????????
                    parts.add(prepareFilePartVideo("video" + i, videoList.get(i))); //partName ?????? ???????????? ???????????? ????????????. ????????? ??????????????? ?????? ????????????.
                }
            }
            RequestBody size = createPartFromString("" + parts.size());

            user_id = preferences.getString("user_id", ""); // ???????????? user_id???
            ApiInterface createPost_api = ApiClient.getApiClient(Post_Create.this).create(ApiInterface.class);
            Call<String> call = createPost_api.createPost(user_id, et_content.getText().toString(), size, parts, "n", category_com_id, "1", post_type);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if(response.body().equals("success")){
                            Log.e("????????? ??????", response.body().toString());
                            setResult(RESULT_OK);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.e("????????? ?????? ??????", t.getMessage());
                }
            });
        }
    }


//    public void updatePost() {
//        List<MultipartBody.Part> parts = new ArrayList<>(); //?????? ????????? ?????????
//        //arraylist?????? null??? ???????????? ?????? ????????? ????????????.
//        if (imageList != null) {
//            for (int i = 0; i < imageList.size(); i++) {
//                //parts ??? ?????? ???????????? ?????? ????????????. ??????????????? ????????? ????????? ??????, uri?????? ????????? ?????? ????????? ?????????
//                parts.add(prepareFilePart("image" + i, imageList.get(i))); //partName ?????? ???????????? ???????????? ????????????. ????????? ??????????????? ?????? ????????????.
//            }
//        }
//        RequestBody size = createPartFromString("" + parts.size());
//        ApiInterface updatePost_api = ApiClient.getApiClient(Post_Create.this).create(ApiInterface.class);
//        Call<String> call = updatePost_api.updatePost(post_id, et_content.getText().toString(), size, parts, post_mention, post_mention);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    Log.e("????????? ??????", response.body().toString());
//                    Intent mIntent = new Intent(getApplicationContext(), MM_Home.class);
//                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    startActivity(mIntent);
//                    finish();
//                    Toast.makeText(getApplicationContext(), "????????? ?????? ??????!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                Log.e("????????? ?????? ??????", t.getMessage());
//            }
//        });
//    }


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

                        Glide.with(Post_Create.this)
                                .load(uri)
                                .into(my_image);

                        Log.e("uri", String.valueOf(uri));
                        imageList.add(uri);


                        Log.i("my_image : ", "");
                        Log.i("video_thumbnail : ", "");
                    }
                }
            });

    // ????????? ????????????
    ActivityResultLauncher<Intent> launcher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        videoList.clear();
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
                        Glide.with(Post_Create.this)
                                .load(createThumbnail(Post_Create.this, uri.toString()))
                                .into(thumbnail);
//                        thumbnail.setImageBitmap(createThumbnail(Add_Posting_Create.this, uri.toString()));
                        Log.i("my_image : ", "");
                        Log.i("video_thumbnail : ", "");
                        videoList.add(uri);
                    }
                }
            });

    // ????????? ????????? ??????
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


    // ?????? ????????? ???????????? ????????? (????????????, ????????? ????????? Uri)
    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // use the FileUtils to get the actual file by uri uri??? ????????? ?????? ????????? ????????????.
        File file = FileUtils.getFile(this, fileUri);
        // create RequestBody instance from file ????????????????????? ??????????????? ?????????.
        RequestBody requestFile = RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_IMAGE), file);
        // MultipartBody.Part is used to send also the actual file name //
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    // ?????? ????????? ???????????? ????????? (????????????, ????????? ????????? Uri)
    @NonNull
    private MultipartBody.Part prepareFilePartVideo(String partName, Uri fileUri) {
//        File videoFile = new File(videoList.get(0).toString());
//        RequestBody videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
//        MultipartBody.Part vFile = MultipartBody.Part.createFormData("video", videoFile.getName(), videoBody);
//        return vFile;
        // use the FileUtils to get the actual file by uri uri??? ????????? ?????? ????????? ????????????.
        File file = FileUtils.getFile(this, fileUri);
        // create RequestBody instance from file ????????????????????? ??????????????? ?????????.
        RequestBody requestFile = RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_VIDEO), file);
        // MultipartBody.Part is used to send also the actual file name //
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    //???????????? ?????? ?????? ????????? ????????????//
    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_TEXT), descriptionString);
    }

}
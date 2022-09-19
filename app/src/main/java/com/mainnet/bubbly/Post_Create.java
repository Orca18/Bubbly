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

    private LinearLayout linearLayoutTopicPosting; // 클릭 이벤트 받는 곳
    private RecyclerView dialogRecyclerView;
    public static Dialog dialog; // 출력할 Dialog 객체
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
        post_type = "0"; // 이미지 = 1 , 동영상 = 2


        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        post_content = intent.getStringExtra("post_content");
        post_file = intent.getStringExtra("post_file");
        // 커뮤니티 아이디가 있으면, 해당 커뮤니티를 기준으로 글을 올린다는 뜻
        category_com_id = intent.getStringExtra("com_id");
        category_com_name = intent.getStringExtra("com_name");

        if(category_com_id == null){
            category_com_id = "0";
            category_com_name = "내 피드";
        }

        com.setText(category_com_name);

        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값


        if (post_content != null) {
            et_content.setText(post_content);
        }


// 수정 할 때 사용
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
        videoView = findViewById(R.id.posting_create_myvideo); // 비디오 첨부 시, 자동 재생되는 곳
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

        // 프로파일 이미지
        if (UserInfo.profile_file_name != null && !UserInfo.profile_file_name.equals("")) {
            Glide.with(getApplicationContext())
                    .load(UserInfo.profile_file_name)
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
                createPost(); // 게시글 작성
            } else {
//                updatePost(); // 게시글 수정
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
            videoList.clear();
            thumbnail.setImageBitmap(null);
            r_thumb.setVisibility(View.GONE);
        });

        com.setOnClickListener(v -> {
            Log.d("디버그태그", "Categories Test 카테고리 가져오기");
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

                    Log.d("디버그태그", "테스트 사이즈:" + responseResult.size());

                    text = new String[responseResult.size() + 1];
                    ids = new String[responseResult.size() + 1];
                    text[0] = "내 피드";
                    ids[0] = "0";
                    for (int i = 0; i < responseResult.size(); i++) {
                        text[i + 1] = responseResult.get(i).getCommunity_name();
                        ids[i + 1] = responseResult.get(i).getCommunity_id();

                        Log.d("디버그태그", "테스트 커뮤 아이디스:" + responseResult.get(i).getCommunity_id());
                    }
                }

                showAlertDialogTopic();

            }

            @Override
            public void onFailure(@NonNull Call<List<Kim_JoinedCom_Response>> call, @NonNull Throwable t) {
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
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
        // Dialog layout 선언

        Log.d("디버그태그", "Categories Test : " + text);

        lp.copyFrom(dialog.getWindow().getAttributes());
        int width = size.x;
        lp.width = width * 100 / 100; // 사용자 화면의 80% (예제를 위해 남겨둠)
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 높이는 내용 전체 높이만큼
        dialog.setContentView(dialogView); // Dialog에 선언했던 layout 적용
        dialog.setCanceledOnTouchOutside(true); // 외부 touch 시 Dialog 종료
        dialog.getWindow().setAttributes(lp); // 지정한 너비, 높이 값 Dialog에 적용
        ArrayList<String> arrayList = new ArrayList<>(); // recyclerView에 들어갈 Array
        arrayList.addAll(Arrays.asList(text)); // Array에 사전에 정의한 Topic 넣기
        /*
        다음 4줄의 코드는 RecyclerView를 정의하기 위한 View, Adapter선언 코드이다.
        1. RecyclerView id 등록
        2. 수직방향으로 보여줄 예정이므로 LinearLayoutManager 등록
           2차원이면 GridLayoutManager 등 다른 Layout을 선택
        3. adapter에 topic Array 넘겨서 출력되게끔 전달
        4. adapter 적용
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

        dialog.show(); // Dialog 출력
    }


    public void createPost() {


        if (et_content.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
        } else {
            List<MultipartBody.Part> parts = new ArrayList<>(); //파일 정보를 담는다
            //arraylist값이 null이 아니라면 넣는 작업을 진행한다.
            if (imageList.size() != 0) {
                Log.i("이미지동영상", "이미지입니다~");
                post_type = "1";
                for (int i = 0; i < imageList.size(); i++) {
                    //parts 에 파일 정보들을 저장 시킵니다. 파트네임은 임시로 설정이 되고, uri값을 통해서 실제 파일을 담는다
                    parts.add(prepareFilePart("image" + i, imageList.get(i))); //partName 으로 구분하여 이미지를 등록한다. 그리고 파일객체에 값을 넣어준다.
                }
            }
            if (videoList.size() != 0) {
                post_type = "2";
                Log.i("이미지동영상", "동영상입니다!");
                for (int i = 0; i < videoList.size(); i++) {
                    //parts 에 파일 정보들을 저장 시킵니다. 파트네임은 임시로 설정이 되고, uri값을 통해서 실제 파일을 담는다
                    parts.add(prepareFilePartVideo("video" + i, videoList.get(i))); //partName 으로 구분하여 이미지를 등록한다. 그리고 파일객체에 값을 넣어준다.
                }
            }
            RequestBody size = createPartFromString("" + parts.size());

            user_id = preferences.getString("user_id", ""); // 로그인한 user_id값
            ApiInterface createPost_api = ApiClient.getApiClient(Post_Create.this).create(ApiInterface.class);
            Call<String> call = createPost_api.createPost(user_id, et_content.getText().toString(), size, parts, "n", category_com_id, "1", post_type);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if(response.body().equals("success")){
                            Log.e("게시글 수정", response.body().toString());
                            setResult(RESULT_OK);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "게시글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.e("게시글 생성 에러", t.getMessage());
                }
            });
        }


    }


    public void updatePost() {
        List<MultipartBody.Part> parts = new ArrayList<>(); //파일 정보를 담는다
        //arraylist값이 null이 아니라면 넣는 작업을 진행한다.
        if (imageList != null) {
            for (int i = 0; i < imageList.size(); i++) {
                //parts 에 파일 정보들을 저장 시킵니다. 파트네임은 임시로 설정이 되고, uri값을 통해서 실제 파일을 담는다
                parts.add(prepareFilePart("image" + i, imageList.get(i))); //partName 으로 구분하여 이미지를 등록한다. 그리고 파일객체에 값을 넣어준다.
            }
        }
        RequestBody size = createPartFromString("" + parts.size());
        ApiInterface updatePost_api = ApiClient.getApiClient(Post_Create.this).create(ApiInterface.class);
        Call<String> call = updatePost_api.updatePost(post_id, et_content.getText().toString(), size, parts, post_mention, post_mention);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("게시글 수정", response.body().toString());
                    Intent mIntent = new Intent(getApplicationContext(), MM_Home.class);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(mIntent);
                    finish();
                    Toast.makeText(getApplicationContext(), "게시글 수정 완료!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
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

    // 동영상 가져오기
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

    // 동영상 썸네일 제작
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


    // 파일 파트를 준비하는 매서드 (파트이름, 그리고 파일의 Uri)
    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // use the FileUtils to get the actual file by uri uri를 통해서 실제 파일을 받아온다.
        File file = FileUtils.getFile(this, fileUri);
        // create RequestBody instance from file 리퀘스트바디를 파일로부터 만든다.
        RequestBody requestFile = RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_IMAGE), file);
        // MultipartBody.Part is used to send also the actual file name //
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    // 파일 파트를 준비하는 매서드 (파트이름, 그리고 파일의 Uri)
    @NonNull
    private MultipartBody.Part prepareFilePartVideo(String partName, Uri fileUri) {
//        File videoFile = new File(videoList.get(0).toString());
//        RequestBody videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
//        MultipartBody.Part vFile = MultipartBody.Part.createFormData("video", videoFile.getName(), videoBody);
//        return vFile;
        // use the FileUtils to get the actual file by uri uri를 통해서 실제 파일을 받아온다.
        File file = FileUtils.getFile(this, fileUri);
        // create RequestBody instance from file 리퀘스트바디를 파일로부터 만든다.
        RequestBody requestFile = RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_VIDEO), file);
        // MultipartBody.Part is used to send also the actual file name //
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    //문자열로 부터 파트 바디를 생성한다//
    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_TEXT), descriptionString);
    }

}
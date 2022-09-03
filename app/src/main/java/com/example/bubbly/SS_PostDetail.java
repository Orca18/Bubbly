package com.example.bubbly;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bubbly.config.Config;
import com.example.bubbly.controller.Reply_Adapter;
import com.example.bubbly.kim_util_test.BottomSheetFragment;
import com.example.bubbly.kim_util_test.BottomSheetFragment_owner;
import com.example.bubbly.kim_util_test.Kim_DateUtil_Cre;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.post_Response;
import com.example.bubbly.retrofit.reply_Response;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HEAD;

public class SS_PostDetail extends AppCompatActivity {

    SharedPreferences preferences;
    public static String user_id, post_id;

    ImageView iv_media, iv_options;
    CircleImageView iv_user_image;
    TextView tv_user_nick, tv_user_id, tv_content, tv_time, tv_like_count, tv_reply_count, tv_retweet_count;
    EditText et_reply;
    LinearLayout bt_reply_add;
    Toolbar toolbar;

    RecyclerView recyclerView;
    Reply_Adapter reply_adapter;
    ArrayList<reply_Response> replyList;
    LinearLayoutManager linearLayoutManager;
    private Parcelable recyclerViewState;

    String owner_id;
    String media_link;
    SimpleExoPlayerView vd_media;

    ImageView iv_like, iv_reply, iv_retweet, iv_share;
    String like_yn;
    Boolean like_check;

    InputMethodManager imm;
    String login_id;

    public static Context mContext;
    public static Activity mActivity;

    String deep_parm;

    int likes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_details);


        initialize();

        mContext = getApplicationContext();


        preferences = getSharedPreferences("novarand", MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값
        like_check = false;
        like_yn = "n";

        bt_reply_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createComment();
            }
        });

        selectCommentUsingPostId(); // 게시글 아이디로 댓글 조회
        selectPostUsingPostId(); // 게시글 아이디로 조회

        listeners();
        listenerLike();

    } // onCreate 닫는곳

    private void listenerLike() {
        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("클릭시 상황", "y/n:"+like_yn+",true/false"+like_check);

                // 기본적으로는 n 이고, 좋아요 누른 상태였으면 y 로 바뀜
                if (like_yn.equals("n")) {
                    // 처음 클릭(홀수)은 false => true
                    if (like_check.equals(false)) {
                        like_check = true; // +1 like
                        DislikeToLike();
                        Log.d("클릭시 상황2", "y/n:"+like_yn+",true/false"+like_check);
                    } else { // 두번째 클릭(짝수)은 true => false
                        like_check = false; // -1 dislike
                        LikeToDislike();
                        Log.d("클릭시 상황2", "y/n:"+like_yn+",true/false"+like_check);

                    }
                } else { // 디테일 왔을 때, 좋아요인 상태 y
                    // 처음 클릭(홀수)은 true => false
                    if (like_check.equals(false)) {
                        like_check = true; // -1 like
                        DislikeToLike();
                        Log.d("클릭시 상황3", "y/n:"+like_yn+",true/false"+like_check);

                    } else { // 두번째 클릭(짝수)은 false => true
                        like_check = false; // +1 dislike
                        LikeToDislike();
                        Log.d("클릭시 상황4", "y/n:"+like_yn+",true/false"+like_check);


                    }
                }

            }
        });

    }

    private void LikeToDislike() {
        ApiInterface dislike_api = ApiClient.getApiClient(SS_PostDetail.this).create(ApiInterface.class);
        Call<String> call = dislike_api.dislike(post_id, user_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    iv_like.setImageResource(R.drawable.ic_outline_favorite_border_24);
                    likes = likes-1;
                    tv_like_count.setText("" + likes);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("좋아요 추가 에러", t.getMessage());
            }
        });
    }

    private void DislikeToLike() {
        ApiInterface like_api = ApiClient.getApiClient(SS_PostDetail.this).create(ApiInterface.class);
        Call<String> call = like_api.like(post_id, user_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    iv_like.setImageResource(R.drawable.ic_baseline_favorite_24);
                    likes = likes + 1;
                    tv_like_count.setText("" + likes);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("좋아요 추가 에러", t.getMessage());
            }
        });
    }

    private void initialize() {
        toolbar = findViewById(R.id.post_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
        login_id = intent.getStringExtra("login_id");

        if (intent.getData() != null) {
            post_id = intent.getDataString().replace("bubbly1://3.39.84.115/", "");
            Log.d("디버그태그", "리플레이스: " + post_id);
        }

        iv_user_image = findViewById(R.id.iv_user_image);
        iv_media = findViewById(R.id.iv_media);
        vd_media = findViewById(R.id.vd_media);

        tv_user_nick = findViewById(R.id.tv_user_nick);
        tv_user_id = findViewById(R.id.tv_user_id);
        tv_content = findViewById(R.id.tv_content);
        tv_time = findViewById(R.id.tv_time);
        tv_like_count = findViewById(R.id.post_details_like_counts);
        tv_reply_count = findViewById(R.id.post_details_reply_count);
        tv_retweet_count = findViewById(R.id.tv_retweet_count);

        et_reply = findViewById(R.id.et_reply);
        bt_reply_add = findViewById(R.id.bt_reply_add);

        recyclerView = findViewById(R.id.post_details_recyclerview);

        iv_options = findViewById(R.id.post_details_options);

        iv_like = findViewById(R.id.iv_like_icon);
        iv_reply = findViewById(R.id.iv_reply_icon);
        iv_retweet = findViewById(R.id.iv_retweet_icon);
        iv_share = findViewById(R.id.iv_share_icon);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    private void listeners() {
        final BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(getApplicationContext());
        final BottomSheetFragment_owner bottomSheetFragment_owner = new BottomSheetFragment_owner(getApplicationContext());


        if (!user_id.equals(owner_id)) {
            iv_options.setVisibility(View.GONE);
        }

        iv_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (user_id.equals(owner_id)) {
                    bottomSheetFragment_owner.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                } else {
//                    bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                }
            }
        });

        iv_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ImageView_FullScreen.class);
                intent.putExtra("img_url", Config.cloudfront_addr + media_link);
                startActivity(intent);
            }
        });

        tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(tv_content.getText());
                Toast.makeText(getApplicationContext(), "복사", Toast.LENGTH_SHORT).show();
            }
        });

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                // tODO 링크 넣기 String으로 받아서 넣기
                String sendMessage = Config.api_server_addr + "/share/deep_post?id=" + post_id;
                intent.putExtra(Intent.EXTRA_TEXT, sendMessage);

                Intent shareIntent = Intent.createChooser(intent, "share");
                startActivity(shareIntent);
            }
        });
    }

    public void selectCommentUsingPostId() { // 게시글 아이디로 댓글 조회
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //위치 유지
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //위치 유지
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        replyList = new ArrayList<>();
        reply_adapter = new Reply_Adapter(getApplicationContext(), this.replyList, getApplicationContext());
        recyclerView.setAdapter(reply_adapter);
        reply_adapter.notifyDataSetChanged();

        ApiInterface selectCommentUsingPostId_api = ApiClient.getApiClient(SS_PostDetail.this).create(ApiInterface.class);
        Call<List<reply_Response>> call = selectCommentUsingPostId_api.selectCommentUsingPostId(post_id);
        call.enqueue(new Callback<List<reply_Response>>() {
            @Override
            public void onResponse(@NonNull Call<List<reply_Response>> call, @NonNull Response<List<reply_Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
//                    progressBar.setVisibility(View.GONE);
                    List<reply_Response> responseResult = response.body();
                    for (int i = 0; i < responseResult.size(); i++) {
                        replyList.add(new reply_Response(responseResult.get(i).getPost_id(),
                                responseResult.get(i).getComment_writer_id(),
                                responseResult.get(i).getComment_depth(),
                                responseResult.get(i).getComment_contents(),
                                responseResult.get(i).getNick_name(),
                                responseResult.get(i).getProfile_file_name(),
                                responseResult.get(i).getMentioned_user_list(),
                                responseResult.get(i).getCre_datetime_comment(),
                                responseResult.get(i).getComment_id()));
                    }
                    reply_adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<reply_Response>> call, @NonNull Throwable t) {
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
            }
        });
    }

    public void createComment() { // 댓글 생성
        ApiInterface createComment_api = ApiClient.getApiClient(SS_PostDetail.this).create(ApiInterface.class);
        Call<String> call = createComment_api.createComment(post_id, "1", user_id, et_reply.getText().toString(), "!");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("createComment 성공", response.body().toString());
                    selectCommentUsingPostId();
                    et_reply.setText("");

                    imm.hideSoftInputFromWindow(et_reply.getWindowToken(), 0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("createComment 에러", t.getMessage());
            }
        });
    }

    public void selectPostUsingPostId() { // 게시글 아이디로 조회
        ApiInterface selectPostUsingPostId_api = ApiClient.getApiClient(SS_PostDetail.this).create(ApiInterface.class);
        Call<List<post_Response>> call = selectPostUsingPostId_api.selectPostUsingPostId(post_id, user_id);
        call.enqueue(new Callback<List<post_Response>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<List<post_Response>> call, @NonNull Response<List<post_Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<post_Response> responseResultpost = response.body();


                    owner_id = responseResultpost.get(0).getPost_writer_id();
                    tv_user_id.setText(login_id);
                    tv_user_nick.setText(responseResultpost.get(0).getNick_name());
                    tv_content.setText(responseResultpost.get(0).getPost_contents());
                    likes = Integer.parseInt(responseResultpost.get(0).getLike_count());
                    tv_like_count.setText("" + likes);

                    if (responseResultpost.get(0).getLike_yn().equals("y")) { // 좋아요를 누른 상태 일 경우
                        iv_like.setImageResource(R.drawable.ic_baseline_favorite_24);
                        like_check = true;
                        like_yn = "y";
                        Log.d("게시글 좋아요 여부", "yn"+like_yn+like_check);
                    }
                    Log.d("게시글 좋아요 여부2", "yn"+like_yn+like_check);


                    try {
                        tv_time.setText(Kim_DateUtil_Cre.creTime(responseResultpost.get(0).getCre_datetime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Glide.with(SS_PostDetail.this)
                            .load(Config.cloudfront_addr + responseResultpost.get(0).getFile_save_names())
                            .into(iv_media);

                    String videoURL = Config.cloudfront_addr + responseResultpost.get(0).getFile_save_names();

                    if (responseResultpost.get(0).getPost_type().equals("2")) {  // 동영상
                        vd_media.setVisibility(View.VISIBLE);
                        // bandwisthmeter : 기본 대역폭 가져오기
                        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                        // 기본 막대를 사용하는 동영상
                        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                        // 트랙셀렉터 추가
                        ExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
                        // url 로 부터 Uri 파싱
                        Uri videouri = Uri.parse(videoURL);
                        // 엑소플레이어뷰
                        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                        // 미디어 소스 생성
                        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                        // 미디어 소스 생성
                        MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
                        // 엑소플레이어 넣기
                        vd_media.setPlayer((SimpleExoPlayer) exoPlayer);
                        vd_media.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                        // 미리 준비
                        exoPlayer.prepare(mediaSource);
                        // 준비 완료시 재생 여부
                        exoPlayer.setPlayWhenReady(false);
                    } else { // 이미지 또는 텍스트
                        Glide.with(SS_PostDetail.this)
                                .load(Config.cloudfront_addr + responseResultpost.get(0).getFile_save_names())
                                .into(iv_media);
                    }

                    if(responseResultpost.get(0).getProfile_file_name()==null){
                        Log.d("디버그태그", "null 이다");
                        Glide.with(mContext)
                                .load(R.drawable.blank_profile)
                                .into(iv_user_image);
                    } else {
                        Glide.with(SS_PostDetail.this)
                                .load(Config.cloudfront_addr + responseResultpost.get(0).getProfile_file_name())
                                .into(iv_user_image);
                    }




                    SetDate(responseResultpost.get(0).getCre_datetime());

                    media_link = responseResultpost.get(0).getFile_save_names();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<post_Response>> call, @NonNull Throwable t) {
                Log.e("에러", t.getMessage());
            }
        });
    }


    private void SetDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = formatter.parse(dateStr);
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy년 MM월 dd일 ㆍ HH:mm");
            String to = transFormat.format(date);
            tv_time.setText(to);
            Log.d("디버그태그", to + "뭐야");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    // TODO 멘션


    ////////////////////////////////////////////////////


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

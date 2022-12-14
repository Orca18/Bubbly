package com.mainnet.bubbly;

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
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.controller.Reply_Adapter;
import com.mainnet.bubbly.kim_util_test.BottomSheetFragment;
import com.mainnet.bubbly.kim_util_test.BottomSheetFragment_owner;
import com.mainnet.bubbly.kim_util_test.BottomSheetFragment_owner_exnft;
import com.mainnet.bubbly.kim_util_test.Kim_ApiClient;
import com.mainnet.bubbly.kim_util_test.Kim_ApiInterface;
import com.mainnet.bubbly.kim_util_test.Kim_Com_Info_Response;
import com.mainnet.bubbly.kim_util_test.Kim_DateUtil_Cre;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.post_Response;
import com.mainnet.bubbly.retrofit.reply_Response;
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

public class SS_PostDetail extends AppCompatActivity {

    public static String action_type;

    SharedPreferences preferences;
    public static String user_id, post_id;

    ImageView iv_media, iv_options;
    CircleImageView iv_user_image;
    TextView tv_user_nick, tv_user_id, tv_content, tv_time, tv_like_count, tv_retweet_count;
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
    String videoURL;

    public static TextView tv_reply_count;
    public static int reply_count;

    String com_id;
    TextView com_name;
    String nft_yn;
    TextView nft_yn_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_details);

        action_type = "update";

        initialize();

        mContext = getApplicationContext();


        preferences = getSharedPreferences("novarand", MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // ???????????? user_id???
        like_check = false;
        like_yn = "n";

        bt_reply_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createComment();
            }
        });

        // TODO ??????????????????
//        vd_media.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent fullvd = new Intent(getApplicationContext(), Video_FullScreen.class);
//                fullvd.putExtra("url", videoURL);
//                startActivity(fullvd);
//            }
//        });

        selectCommentUsingPostId(); // ????????? ???????????? ?????? ??????
        selectPostUsingPostId(); // ????????? ???????????? ??????

        listeners();
        listenerLike();

    } // onCreate ?????????

    private void GetComName() {
        Kim_ApiInterface api2 = Kim_ApiClient.getApiClient(mContext).create(Kim_ApiInterface.class);
        Call<List<Kim_Com_Info_Response>> call2 = api2.selectCommunityUsingCommunityId(com_id);
        call2.enqueue(new Callback<List<Kim_Com_Info_Response>>() {
            @Override
            public void onResponse(Call<List<Kim_Com_Info_Response>> call2, Response<List<Kim_Com_Info_Response>> response) {


                com_name.setText(response.body().get(0).getCommunity_name());

            }

            @Override
            public void onFailure(Call<List<Kim_Com_Info_Response>> call, Throwable t) {

            }
        });
    }

    private void listenerLike() {
        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("????????? ??????", "y/n:" + like_yn + ",true/false" + like_check);

                // ?????????????????? n ??????, ????????? ?????? ??????????????? y ??? ??????
                if (like_yn.equals("n")) {
                    // ?????? ??????(??????)??? false => true
                    if (like_check.equals(false)) {
                        like_check = true; // +1 like
                        DislikeToLike();
                        Log.d("????????? ??????2", "y/n:" + like_yn + ",true/false" + like_check);
                    } else { // ????????? ??????(??????)??? true => false
                        like_check = false; // -1 dislike
                        LikeToDislike();
                        Log.d("????????? ??????2", "y/n:" + like_yn + ",true/false" + like_check);

                    }
                } else { // ????????? ?????? ???, ???????????? ?????? y
                    // ?????? ??????(??????)??? true => false
                    if (like_check.equals(false)) {
                        like_check = true; // -1 like
                        DislikeToLike();
                        Log.d("????????? ??????3", "y/n:" + like_yn + ",true/false" + like_check);

                    } else { // ????????? ??????(??????)??? false => true
                        like_check = false; // +1 dislike
                        LikeToDislike();
                        Log.d("????????? ??????4", "y/n:" + like_yn + ",true/false" + like_check);


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
                    likes = likes - 1;
                    tv_like_count.setText("" + likes);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("????????? ?????? ??????", t.getMessage());
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
                Log.e("????????? ?????? ??????", t.getMessage());
            }
        });
    }

    private void initialize() {
        toolbar = findViewById(R.id.post_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // ???????????? ??????, ???????????? true??? ?????? ???????????? ??????
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");
//        login_id = intent.getStringExtra("login_id");

        iv_user_image = findViewById(R.id.iv_user_image);
        iv_media = findViewById(R.id.iv_media);
        vd_media = findViewById(R.id.vd_media);
        com_name = findViewById(R.id.post_detail_comname);
        nft_yn_text = findViewById(R.id.post_detail_nft_yn);

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
        final BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(getApplicationContext(), owner_id, user_id, tv_content.getText().toString());
        final BottomSheetFragment_owner bottomSheetFragment_owner = new BottomSheetFragment_owner(getApplicationContext());
        final BottomSheetFragment_owner_exnft bottomSheetFragment_owner_exnft = new BottomSheetFragment_owner_exnft(getApplicationContext());





        iv_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id.equals(owner_id) && nft_yn.equals("n")) { // ?????? nft ?????? ??????
                    bottomSheetFragment_owner.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                }
                else if (user_id.equals(owner_id) && nft_yn.equals("y")) { // ?????? nft ?????? ????????????, ?????? ???????????????
                    bottomSheetFragment_owner_exnft.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                }
                else {
                    bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
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
                Toast.makeText(getApplicationContext(), "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            }
        });

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                // tODO ?????? ?????? String?????? ????????? ??????
                String sendMessage = Config.api_server_addr + "/share/?data=post_" + post_id;
                intent.putExtra(Intent.EXTRA_TEXT, sendMessage);

                Intent shareIntent = Intent.createChooser(intent, "share");
                startActivity(shareIntent);
            }
        });
    }

    public void selectCommentUsingPostId() { // ????????? ???????????? ?????? ??????
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //?????? ??????
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        //?????? ??????
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


                    reply_count = responseResult.size();
                    Log.d("???????????????", "Replycount:" + reply_count);
                    tv_reply_count.setText("" + reply_count);

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<reply_Response>> call, @NonNull Throwable t) {
                Log.e("????????? ???????????? ????????? ??????", t.getMessage());
            }
        });
    }

    public void createComment() { // ?????? ??????
        ApiInterface createComment_api = ApiClient.getApiClient(SS_PostDetail.this).create(ApiInterface.class);
        Call<String> call = createComment_api.createComment(post_id, "1", user_id, et_reply.getText().toString(), "!");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("createComment ??????", response.body().toString());
                    selectCommentUsingPostId();
                    et_reply.setText("");

                    reply_count = reply_count + 1;
                    tv_reply_count.setText("" + reply_count);
                    imm.hideSoftInputFromWindow(et_reply.getWindowToken(), 0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("createComment ??????", t.getMessage());
            }
        });
    }

    public void selectPostUsingPostId() { // ????????? ???????????? ??????
        ApiInterface selectPostUsingPostId_api = ApiClient.getApiClient(SS_PostDetail.this).create(ApiInterface.class);
        Call<List<post_Response>> call = selectPostUsingPostId_api.selectPostUsingPostId(post_id, user_id);
        call.enqueue(new Callback<List<post_Response>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NonNull Call<List<post_Response>> call, @NonNull Response<List<post_Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<post_Response> responseResultpost = response.body();


                    owner_id = responseResultpost.get(0).getPost_writer_id();
                    tv_user_id.setText(responseResultpost.get(0).getLogin_id());
                    tv_user_nick.setText(responseResultpost.get(0).getNick_name());
                    tv_content.setText(responseResultpost.get(0).getPost_contents());
                    likes = Integer.parseInt(responseResultpost.get(0).getLike_count());
                    tv_like_count.setText("" + likes);

                    com_id = responseResultpost.get(0).getCommunity_id();
                    nft_yn = responseResultpost.get(0).getNft_post_yn();

                    if (nft_yn.equals("y")) {
                        nft_yn_text.setVisibility(View.VISIBLE);
                    } else {
                        nft_yn_text.setVisibility(View.INVISIBLE);
                    }

                    if (responseResultpost.get(0).getLike_yn().equals("y")) { // ???????????? ?????? ?????? ??? ??????
                        iv_like.setImageResource(R.drawable.ic_baseline_favorite_24);
                        like_check = true;
                        like_yn = "y";
                        Log.d("????????? ????????? ??????", "yn" + like_yn + like_check);
                    }
                    Log.d("????????? ????????? ??????2", "yn" + like_yn + like_check);


                    try {
                        tv_time.setText(Kim_DateUtil_Cre.creTime(responseResultpost.get(0).getCre_datetime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Glide.with(SS_PostDetail.this)
                            .load(Config.cloudfront_addr + responseResultpost.get(0).getFile_save_names())
                            .into(iv_media);

                    videoURL = Config.cloudfront_addr + responseResultpost.get(0).getFile_save_names();

                    if (responseResultpost.get(0).getPost_type().equals("2")) {  // ?????????
                        vd_media.setVisibility(View.VISIBLE);
                        // bandwisthmeter : ?????? ????????? ????????????
                        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                        // ?????? ????????? ???????????? ?????????
                        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                        // ??????????????? ??????
                        ExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
                        // url ??? ?????? Uri ??????
                        Uri videouri = Uri.parse(videoURL);
                        // ?????????????????????
                        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                        // ????????? ?????? ??????
                        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                        // ????????? ?????? ??????
                        MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
                        // ?????????????????? ??????
                        vd_media.setPlayer((SimpleExoPlayer) exoPlayer);
                        vd_media.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                        // ?????? ??????
                        exoPlayer.prepare(mediaSource);
                        // ?????? ????????? ?????? ??????
                        exoPlayer.setPlayWhenReady(false);
                        iv_media.setVisibility(View.GONE);
                    } else { // ????????? ?????? ?????????
                        Glide.with(SS_PostDetail.this)
                                .load(Config.cloudfront_addr + responseResultpost.get(0).getFile_save_names())
                                .into(iv_media);
                    }

                    if (responseResultpost.get(0).getProfile_file_name() == null) {
                        Log.d("???????????????", "null ??????");
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

                    if (!com_id.equals("0")) {
                        GetComName();
                    } else {
                        com_name.setVisibility(View.INVISIBLE);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<post_Response>> call, @NonNull Throwable t) {
                Log.e("??????", t.getMessage());
            }
        });
    }


    private void SetDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = formatter.parse(dateStr);
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy??? MM??? dd??? ??? HH:mm");
            String to = transFormat.format(date);
            tv_time.setText(to);
            Log.d("???????????????", to + "??????");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    // TODO ??????


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

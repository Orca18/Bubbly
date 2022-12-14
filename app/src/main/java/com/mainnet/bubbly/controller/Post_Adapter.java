package com.mainnet.bubbly.controller;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.widget.PopupMenu;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

import static com.mainnet.bubbly.Kim_Bottom.Bottom1_Fragment.click_position;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mainnet.bubbly.ImageView_FullScreen;
import com.mainnet.bubbly.Post_ApplyNFT_A;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.SS_PostDetail;
import com.mainnet.bubbly.SS_Profile;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.kim_util_test.Kim_ApiClient;
import com.mainnet.bubbly.kim_util_test.Kim_ApiInterface;
import com.mainnet.bubbly.kim_util_test.Kim_Com_Info_Response;
import com.mainnet.bubbly.kim_util_test.Kim_DateUtil;
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

public class Post_Adapter extends RecyclerView.Adapter<Post_Adapter.PostViewHolder> {

    private Context context;
    private Context mContext;
    public static ArrayList<post_Response> lists;
    //    private ItemClickListener itemClickListener;
    SharedPreferences preferences;
    String user_id;
    String post_id;

    Activity activiy;
    Kim_ApiInterface api = Kim_ApiClient.getApiClient(mContext).create(Kim_ApiInterface.class);


    public Post_Adapter(Context context, ArrayList<post_Response> lists, Context mContext, Activity activity) {
        this.context = context;
        this.lists = lists;
//        this.itemClickListener = itemClickListener;
        this.mContext = mContext; // ????????? Context ??? ???????????? ?????? ?????? ??????
        this.activiy = activity; // ????????? Context ??? ???????????? ?????? ?????? ??????

    }


    @Override
    public void onViewRecycled(@NonNull PostViewHolder holder) {
        super.onViewRecycled(holder);
        Log.d("onViewRecycled", "onViewRecycled ??????~~");
    }

    @NonNull
    @Override
    public Post_Adapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    public static Date getDate(String from) throws ParseException {
        // "yyyy-MM-dd HH:mm:ss"
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(from);
        return date;
    }


    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            for (Object payload : payloads) {

                if (SS_PostDetail.action_type != null) {
                    Log.d("???????????????", "???????????????" + SS_PostDetail.action_type);
                    // ???????????? ????????? ???????????????
                    if (SS_PostDetail.action_type.equals("delete")) {
                        lists.remove(position);
                        holder.ll_item_layout.setVisibility(View.GONE);
                        SS_PostDetail.action_type = "update";
                    } else {
                        post_Response post_response = lists.get(position);

                        preferences = context.getSharedPreferences("novarand", MODE_PRIVATE);
                        user_id = preferences.getString("user_id", ""); // ???????????? user_id???
                        // ?????? / ???????????? / ?????? ???
                        // 1) ????????? ???????????? ?????????, ???????????? ????????? ????????? ????????? ????????????
                        ApiInterface selectPostUsingPostId_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
                        Call<List<post_Response>> call2 = selectPostUsingPostId_api.selectPostUsingPostId(post_response.getPost_id(), user_id);
                        call2.enqueue(new Callback<List<post_Response>>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onResponse(@NonNull Call<List<post_Response>> call2, @NonNull Response<List<post_Response>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<post_Response> responseResultpost = response.body();
                                    if (responseResultpost.get(0).getLike_yn().equals("y")) { // ???????????? ?????? ?????? ??? ??????
                                        holder.iv_like_icon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                    } else {
                                        holder.iv_like_icon.setImageResource(R.drawable.ic_outline_favorite_border_24);
                                    }

                                    // 2)
                                    holder.tv_like_count.setText(responseResultpost.get(0).getLike_count());

                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<post_Response>> call, @NonNull Throwable t) {
                                Log.e("??????", t.getMessage());
                            }
                        });


                        // ===================================================================================================================
                        // 3) ????????? ???????????? ????????? ???????????? ?????? ??? ??????
                        ApiInterface selectCommentUsingPostId_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
                        Call<List<reply_Response>> call = selectCommentUsingPostId_api.selectCommentUsingPostId(post_response.getPost_id());
                        call.enqueue(new Callback<List<reply_Response>>() {
                            @Override
                            public void onResponse(@NonNull Call<List<reply_Response>> call, @NonNull Response<List<reply_Response>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<reply_Response> responseResult = response.body();
                                    holder.tv_reply_count.setText(String.valueOf(responseResult.size()));
                                }

                            }

                            @Override
                            public void onFailure(@NonNull Call<List<reply_Response>> call, @NonNull Throwable t) {
                                Log.e("????????? ???????????? ????????? ??????", t.getMessage());
                            }
                        });
                    }

                }


            }
        }

    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Log.d("holder::::", "holder::::" + holder);

        post_Response post_response = lists.get(position);
        preferences = context.getSharedPreferences("novarand", MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // ???????????? user_id???

        holder.tv_user_nick.setText(post_response.getNick_name());
        holder.tv_content.setText(post_response.getPost_contents());
        holder.tv_like_count.setText(post_response.getLike_count());

        String nft_yn = post_response.getNft_post_yn();
        if(nft_yn.equals("n")){
            holder.tv_nft.setVisibility(View.GONE);
        }
        Log.i("?????? ??????", "??????:" + post_response.getPost_type());

//        Glide.with(mContext)
//                .load((Bitmap) null)
//                .fitCenter()
//                .into(holder.iv_media);

        String type = post_response.getPost_type();

        String media_url = "";


        if (media_url == null) {
//
        } else {
            Log.d("???????????????", "????????????:" + media_url);
            // TODO ????????? ????????? View
            media_url = Config.cloudfront_addr + post_response.getFile_save_names();
            Log.d("???????????????", "try ???:" + type);
            if (type.equals("1")) {
                Log.d("???????????????", "??????????????????1:" + type);
                Glide.with(mContext)
                        .load(media_url)
                        .fitCenter()
                        .into(holder.iv_media);
            }
            if (type.equals("2")) {
                // bandwisthmeter : ?????? ????????? ????????????
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                // ?????? ????????? ???????????? ?????????
                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                // ??????????????? ??????
                ExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                // url ??? ?????? Uri ??????
                Uri videouri = Uri.parse(media_url);
                // ?????????????????????
                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                // ????????? ?????? ??????
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                // ????????? ?????? ??????
                MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
                // ?????????????????? ??????
                holder.vd_media.setPlayer((SimpleExoPlayer) exoPlayer);
                holder.vd_media.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                // ?????? ??????
                exoPlayer.prepare(mediaSource);
                // ?????? ????????? ?????? ??????
                exoPlayer.setPlayWhenReady(false);
                Log.d("???????????????", "??????????????????2:" + type);
            } else {
                Log.d("???????????????", "??????????????????0:" + type);
            }

        }

        // TODO ????????????
//        holder.vd_media.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(context, Video_FullScreen.class);
//                context.startActivity(mIntent);
//            }
//        });


        holder.tv_user_id.setText(post_response.getLogin_id());
        holder.tv_com_name.setText(post_response.getCommunity_id());


        holder.iv_retweet_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "????????? ??????", Toast.LENGTH_SHORT).show();
            }
        });

        holder.iv_share_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                // tODO ?????? ?????? String?????? ????????? ??????
                String sendMessage = Config.api_server_addr + "/share/?data=post_" + post_response.getPost_id();
                intent.putExtra(Intent.EXTRA_TEXT, sendMessage);

                Intent shareIntent = Intent.createChooser(intent, "share");
                context.startActivity(shareIntent);
            }
        });


        if (post_response.getProfile_file_name() == null) {
            Log.d("???????????????", "null ??????");
            Glide.with(mContext)
                    .load(R.drawable.blank_profile)
                    .into(holder.iv_user_image);
        } else {
            Log.d("???????????????", "null ?????????");
            Glide.with(mContext)
                    .load(Config.cloudfront_addr + post_response.getProfile_file_name())
                    .into(holder.iv_user_image);
        }


        String a = null;
        try {
            Log.d("???????????????", "???????????????:" + post_response.getCre_datetime());
            a = Kim_DateUtil.beforeTime(getDate(post_response.getCre_datetime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // SNS ?????? ??????
        holder.tv_time.setText(a);


//        // ???????????? ???????????? ?????? ???, '?????????'?????? invisible
//        if (!user_id.equals(post_response.getPost_writer_id())) {
//            holder.iv_options.setVisibility(View.GONE);
//        }

        holder.iv_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(holder.iv_options.getContext(), holder.itemView);

                if (user_id.equals(post_response.getPost_writer_id()) && nft_yn.equals("n")) {
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
//                                case R.id.action_a:
//                                    Toast.makeText(context, "?????? ??????", Toast.LENGTH_SHORT).show();
//                                    return true;

                                case R.id.action_b:
                                    ApiInterface deletePost_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
                                    Call<String> call = deletePost_api.deletePost(post_response.getPost_id());
                                    call.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                //Log.e("delete", String.valueOf(position));
                                                lists.remove(position);
                                                notifyItemRemoved(position);
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                            Log.e("??????", t.getMessage());
                                        }
                                    });
                                    return true;

                                case R.id.action_c:
                                    //nft??????
                                    Intent mIntent = new Intent(context.getApplicationContext(), Post_ApplyNFT_A.class);
                                    mIntent.putExtra("post_id", post_response.getPost_id());
                                    context.startActivity(mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    Toast.makeText(context, "?????? ??????", Toast.LENGTH_SHORT).show();
                                    return true;

                                default:
                                    return false;
                            }

                        }
                    });
                    popup.inflate(R.menu.main_list_menu);
                    popup.setGravity(Gravity.RIGHT | Gravity.END);

                    popup.show();
                }

                else if (user_id.equals(post_response.getPost_writer_id()) && nft_yn.equals("y")){ // nft ?????? ?????? ??????

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
//                            case R.id.action_a:
//                                Toast.makeText(context, "?????? ??????", Toast.LENGTH_SHORT).show();
//                                return true;

                                case R.id.action_b:
                                    ApiInterface deletePost_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
                                    Call<String> call = deletePost_api.deletePost(post_response.getPost_id());
                                    call.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                //Log.e("delete", String.valueOf(position));
                                                lists.remove(position);
                                                notifyItemRemoved(position);
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                            Log.e("??????", t.getMessage());
                                        }
                                    });
                                    return true;

                                case R.id.action_c:
                                    Toast.makeText(context, "?????? ??????", Toast.LENGTH_SHORT).show();
                                    return true;

                                default:
                                    return false;
                            }

                        }
                    });
                    popup.inflate(R.menu.main_list_menu_exnft);
                    popup.setGravity(Gravity.RIGHT | Gravity.END);

                    popup.show();
                }

                // ???????????? ???????????? ?????? ???, '?????????'?????? invisible
                else {
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.action_a2:
                                    // Todo ?????? ??????
                                    Call<String> call = api.report("0", post_response.getPost_writer_id(), user_id, post_response.getPost_contents());
                                    call.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                Toast.makeText(context, "?????? ??????", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                            Log.e("????????? ?????? ??????", t.getMessage());
                                        }
                                    });
                                    return true;

                                default:
                                    return false;
                            }

                        }
                    });
                    popup.inflate(R.menu.main_liist_menu2);
                    popup.setGravity(Gravity.RIGHT | Gravity.END);

                    popup.show();
                }

            }
        });

//        holder.iv_options.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (user_id.equals(post_response.getPost_writer_id())) {
//                    BottomSheetFragment_owner_ForAdapter bottomSheetFragment_owner = new BottomSheetFragment_owner_ForAdapter(context, post_id, position);
//                    bottomSheetFragment_owner.show(((AppCompatActivity)activiy).getSupportFragmentManager(), bottomSheetFragment_owner.getTag());
//                } else {
//                    BottomSheetFragment_ForAdapter bottomSheetFragment = new BottomSheetFragment_ForAdapter(context, post_id);
//                    bottomSheetFragment.show(((AppCompatActivity)activiy).getSupportFragmentManager(), bottomSheetFragment.getTag());
//
//                }
//
//            }
//        });

        // ????????? ???????????? ????????? ????????? ?????? ?????? ??????
//        holder.iv_options.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                MenuItem delete = menu.add(Menu.NONE, R.id.delete, 1, "????????? ??????");
//                MenuItem modify = menu.add(Menu.NONE, R.id.modify, 2, "????????? ??????");
//                delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.delete:
//                                ApiInterface deletePost_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
//                                Call<String> call = deletePost_api.deletePost(post_response.getPost_id());
//                                call.enqueue(new Callback<String>()
//                                {
//                                    @Override
//                                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
//                                    {
//                                        if (response.isSuccessful() && response.body() != null)
//                                        {
//                                            //Log.e("delete", String.valueOf(position));
//                                            lists.remove(position);
//                                            notifyItemRemoved(position);
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
//                                    {
//                                        Log.e("??????", t.getMessage());
//                                    }
//                                });
//                                return true;
//                        }
//                        return false;
//                    }
//                });
        // ?????? ??????
//                modify.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.modify:
//                                Intent intent = new Intent(context, Add_Posting_Create.class);
//                                intent.putExtra("post_id",post_response.getPost_id());
//                                intent.putExtra("post_content",post_response.getPost_contents());
//                                intent.putExtra("post_file",post_response.getFile_save_names());
//                                intent.putExtra("post_mention",post_response.getMentioned_user_list());
//                                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                                return true;
//                        }
//                        return false;
//                    }
//                });
//            }
//        });


        if (post_response.getLike_yn().equals("y")) { // ???????????? ?????? ?????? ??? ??????
            holder.iv_like_icon.setImageResource(R.drawable.ic_baseline_favorite_24);
        }

        holder.ll_item_layout.setOnClickListener(new View.OnClickListener() { // ????????? ????????? ????????? ???????????????
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SS_PostDetail.class);
                intent.putExtra("post_id", post_response.getPost_id());
                intent.putExtra("login_id", post_response.getLogin_id());
                intent.putExtra("position", position);
                click_position = position;
                Log.d("???????????????", "Login_+id: " + post_response.getLogin_id());
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        holder.iv_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageView_FullScreen.class);
                intent.putExtra("img_url", Config.cloudfront_addr + post_response.getFile_save_names());
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        holder.iv_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SS_Profile.class);
                intent.putExtra("user_id", post_response.getPost_writer_id());
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        holder.layout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("????????? ?????? ??????", String.valueOf(holder.like_check));
                if (post_response.getLike_yn().equals("n")) {
                    // ??? ?????? ?????? ?????? ??? ??????, ????????? ???????????????
                    if (holder.like_check.equals(false)) {
                        // ??? ?????? ?????? (???????????? ???????????? ?????????)
                        // ????????? ????????? ????????? ????????? ??????????????????
                        int like_count = Integer.parseInt(post_response.getLike_count()) + 1;
                        Log.i("????????????", "?????? ????????? ??? : " + like_count);
                        holder.tv_like_count.setText("" + like_count);
                        holder.like_check = true;
                        ApiInterface like_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
                        Call<String> call = like_api.like(post_response.getPost_id(), user_id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Log.e("????????? ?????? ?????????", response.body().toString());
                                    holder.iv_like_icon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Log.e("????????? ?????? ??????", t.getMessage());
                            }
                        });

                    } else {
                        // ??? if ?????? ????????? ????????? ???????????????
                        int like_count = Integer.parseInt(post_response.getLike_count());
                        Log.i("????????????", "?????? ????????? ??? : " + like_count);
                        holder.tv_like_count.setText("" + like_count);
                        holder.like_check = false;
                        ApiInterface dislike_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
                        Call<String> call = dislike_api.dislike(post_response.getPost_id(), user_id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Log.e("????????? ?????? ?????????", response.body().toString());
                                    holder.iv_like_icon.setImageResource(R.drawable.ic_outline_favorite_border_24);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Log.e("????????? ?????? ??????", t.getMessage());
                            }
                        });
                    }
                } else {
                    // ??? ?????? ?????? ?????? ??? ??????, ????????? ???????????????
                    if (holder.like_check.equals(false)) {
                        // ??? ?????? ?????? (???????????? ???????????? ?????????)
                        // ????????? ?????? ???????????????
                        int like_count = Integer.parseInt(post_response.getLike_count()) - 1;
                        Log.i("????????????", "?????? ????????? ??? : " + like_count);
                        holder.tv_like_count.setText("" + like_count);
                        holder.like_check = true;
                        ApiInterface dislike_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
                        Call<String> call = dislike_api.dislike(post_response.getPost_id(), user_id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Log.e("????????? ?????? ?????????", response.body().toString());
                                    holder.iv_like_icon.setImageResource(R.drawable.ic_outline_favorite_border_24);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Log.e("????????? ?????? ??????", t.getMessage());
                            }
                        });


                    } else {
                        // ??? if ?????? ?????? ???????????????. ?????? ?????? ????????? ????????????
                        int like_count = Integer.parseInt(post_response.getLike_count());
                        Log.i("????????????", "?????? ????????? ??? : " + like_count);
                        holder.tv_like_count.setText("" + like_count);
                        holder.like_check = false;
                        ApiInterface like_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
                        Call<String> call = like_api.like(post_response.getPost_id(), user_id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Log.e("????????? ?????? ?????????", response.body().toString());
                                    holder.iv_like_icon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Log.e("????????? ?????? ??????", t.getMessage());
                            }
                        });
                    }
                }

            }
        });


        ApiInterface selectCommentUsingPostId_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
        Call<List<reply_Response>> call = selectCommentUsingPostId_api.selectCommentUsingPostId(post_response.getPost_id());
        call.enqueue(new Callback<List<reply_Response>>() {
            @Override
            public void onResponse(@NonNull Call<List<reply_Response>> call, @NonNull Response<List<reply_Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<reply_Response> responseResult = response.body();
                    holder.tv_reply_count.setText(String.valueOf(responseResult.size()));
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<reply_Response>> call, @NonNull Throwable t) {
                Log.e("????????? ???????????? ????????? ??????", t.getMessage());
            }
        });


        // TODO ???????????? ?????? ???????????? ?????????, ?????? ?????? ????????? ????????? ??????
        Kim_ApiInterface api2 = Kim_ApiClient.getApiClient(mContext).create(Kim_ApiInterface.class);
        Call<List<Kim_Com_Info_Response>> call2 = api2.selectCommunityUsingCommunityId(post_response.getCommunity_id());
        call2.enqueue(new Callback<List<Kim_Com_Info_Response>>() {
            @Override
            public void onResponse(Call<List<Kim_Com_Info_Response>> call2, Response<List<Kim_Com_Info_Response>> response) {

                if (post_response.getCommunity_id().equals("0")) {
                    holder.tv_com_name.setVisibility(View.GONE);
                } else {
                    holder.tv_com_name.setVisibility(View.VISIBLE);
                    holder.tv_com_name.setText(response.body().get(0).getCommunity_name());
                }
            }

            @Override
            public void onFailure(Call<List<Kim_Com_Info_Response>> call, Throwable t) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        Boolean like_check = false;
        public LinearLayout ll_item_layout, layout_like, ll_all;

        ImageView iv_media, iv_options, iv_like_icon, iv_reply_icon, iv_retweet_icon, iv_share_icon;
        TextView tv_user_nick, tv_content, tv_like_count, tv_reply_count, tv_retweet_count, tv_time;
        TextView tv_user_id, tv_com_name;
        SimpleExoPlayerView vd_media;

        CircleImageView iv_user_image;

        TextView tv_nft;

        public PostViewHolder(@NonNull View view) {
            super(view);
            ll_all = view.findViewById(R.id.ll_item_layout_all);
            ll_item_layout = view.findViewById(R.id.ll_item_layout);
            layout_like = view.findViewById(R.id.layout_like);

            iv_user_image = view.findViewById(R.id.iv_user_image);
            iv_media = view.findViewById(R.id.iv_media);
            iv_options = view.findViewById(R.id.iv_options);
            iv_like_icon = view.findViewById(R.id.iv_like_icon);
            iv_reply_icon = view.findViewById(R.id.iv_reply_icon);
            iv_retweet_icon = view.findViewById(R.id.iv_retweet_icon);
            iv_share_icon = view.findViewById(R.id.iv_share_icon);

            tv_user_nick = view.findViewById(R.id.tv_user_nick);
            tv_content = view.findViewById(R.id.tv_content);
            tv_like_count = view.findViewById(R.id.tv_like_count);
            tv_reply_count = view.findViewById(R.id.tv_reply_count);
            tv_retweet_count = view.findViewById(R.id.tv_retweet_count);
            tv_time = view.findViewById(R.id.tv_time);

            tv_user_id = view.findViewById(R.id.feed_basic_userID);
            tv_com_name = view.findViewById(R.id.tv_com_name);

            vd_media = view.findViewById(R.id.vd_media);

            tv_nft = view.findViewById(R.id.item_post_nft_yn);

//            this.itemClickListener = itemClickListener;
//            linearLayout.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View view)
//        {
//            itemClickListener.onItemClick(view, getAdapterPosition());
//        }
    }

//    public interface ItemClickListener
//    {
//        void onItemClick(View view, int position);
//    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
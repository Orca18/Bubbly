package com.example.bubbly.controller;

import android.app.Activity;
import android.net.Uri;
import android.view.Gravity;
import android.widget.PopupMenu;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bubbly.Post_Create;
import com.example.bubbly.ImageView_FullScreen;
import com.example.bubbly.Post_ApplyNFT_A;
import com.example.bubbly.R;
import com.example.bubbly.SS_PostDetail;
import com.example.bubbly.SS_Profile;
import com.example.bubbly.kim_util_test.BottomSheetFragment;
import com.example.bubbly.kim_util_test.BottomSheetFragment_ForAdapter;
import com.example.bubbly.kim_util_test.BottomSheetFragment_owner;
import com.example.bubbly.kim_util_test.BottomSheetFragment_owner_ForAdapter;
import com.example.bubbly.kim_util_test.Kim_ApiClient;
import com.example.bubbly.kim_util_test.Kim_ApiInterface;
import com.example.bubbly.kim_util_test.Kim_Com_Info_Response;
import com.example.bubbly.kim_util_test.Kim_DateUtil;
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

public class Post_Adapter extends RecyclerView.Adapter<Post_Adapter.PostViewHolder> {

    private Context context;
    private Context mContext;
    public static ArrayList<post_Response> lists;
    //    private ItemClickListener itemClickListener;
    SharedPreferences preferences;
    String user_id;
    String post_id;

    Activity activiy;

    public Post_Adapter(Context context, ArrayList<post_Response> lists, Context mContext, Activity activity) {
        this.context = context;
        this.lists = lists;
//        this.itemClickListener = itemClickListener;
        this.mContext = mContext; // 이미지 Context 를 활용해서 넣기 위해 추가
        this.activiy = activity; // 이미지 Context 를 활용해서 넣기 위해 추가

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
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") int position) {

        post_Response post_response = lists.get(position);
        preferences = context.getSharedPreferences("novarand", MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

        holder.tv_user_nick.setText(post_response.getNick_name());
        holder.tv_content.setText(post_response.getPost_contents());
        holder.tv_like_count.setText(post_response.getLike_count());
        holder.tv_time.setText(post_response.getCre_datetime());


        Log.i("파일 타입", "과연:" + post_response.getPost_type());

            Glide.with(mContext)
                   .load("https://d2gf68dbj51k8e.cloudfront.net/" + post_response.getFile_save_names())
                   .fitCenter()
                   .into(holder.iv_media);

        String type = post_response.getPost_type();

        String videoURL = "https://d2gf68dbj51k8e.cloudfront.net/" + post_response.getFile_save_names();
        try {
            Log.d("디버그태그", "try 전:"+type);
            if(type.equals("2")){
                // bandwisthmeter : 기본 대역폭 가져오기
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                // 기본 막대를 사용하는 동영상
                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                // 트랙셀렉터 추가
                ExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                // url 로 부터 Uri 파싱
                Uri videouri = Uri.parse(videoURL);
                // 엑소플레이어뷰
                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                // 미디어 소스 생성
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                // 미디어 소스 생성
                MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
                // 엑소플레이어 넣기
                holder.vd_media.setPlayer((SimpleExoPlayer) exoPlayer);
                holder.vd_media.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                // 미리 준비
                exoPlayer.prepare(mediaSource);
                // 준비 완료시 재생 여부
                exoPlayer.setPlayWhenReady(false);
                Log.d("디버그태그", "엑소플레이어2:"+type);
            } if (type.equals("1")) {
                holder.vd_media.setVisibility(View.GONE);
                Log.d("디버그태그", "엑소플레이어1:"+type);
                Glide.with(mContext)
                        .load("https://d2gf68dbj51k8e.cloudfront.net/" + post_response.getFile_save_names())
                        .fitCenter()
                        .into(holder.iv_media);
            } else {
                Log.d("디버그태그", "엑소플레이어0:"+type);
            }

        } catch (Exception e) {
            Log.e("TAG", "Error : " + e.toString());
        }




        holder.tv_user_id.setText(post_response.getLogin_id());
//        holder.tv_com_name.setText(post_response.getCommunity_id());


        holder.iv_retweet_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "리트윗 구현", Toast.LENGTH_SHORT).show();
            }
        });

        holder.iv_share_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                // tODO 링크 넣기 String으로 받아서 넣기
                String sendMessage = "http://3.39.84.115/share/deep_community?id="+post_response.getPost_id();
//                String sendMessage = "10.0.2.2:3000/community?id="+com_id;
                intent.putExtra(Intent.EXTRA_TEXT, sendMessage);

                Intent shareIntent = Intent.createChooser(intent, "share");
                context.startActivity(shareIntent);
            }
        });


        if(post_response.getProfile_file_name().equals(null)){
            Log.d("디버그태그", "null 이다");
            Glide.with(mContext)
                    .load(R.drawable.blank_profile)
                    .into(holder.iv_user_image);
        } else {
            Log.d("디버그태그", "null 아니다");
            Glide.with(mContext)
                    .load("https://d2gf68dbj51k8e.cloudfront.net/" + post_response.getProfile_file_name())
                    .into(holder.iv_user_image);
        }



        String a = null;
        try {
            a = Kim_DateUtil.beforeTime(getDate(post_response.getCre_datetime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // SNS 형식 시간
        holder.tv_time.setText(a);

        holder.iv_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(holder.iv_options.getContext(), holder.itemView);

                if (user_id.equals(post_response.getPost_writer_id())) {
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
//                                case R.id.action_a:
//                                    Toast.makeText(context, "팝업 확인", Toast.LENGTH_SHORT).show();
//                                    return true;

                                case R.id.action_b:
                                    ApiInterface deletePost_api = ApiClient.getApiClient().create(ApiInterface.class);
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
                                            Log.e("에러", t.getMessage());
                                        }
                                    });
                                    return true;

                                case R.id.action_c:
                                    //nft신청
                                    Intent mIntent = new Intent(context.getApplicationContext(), Post_ApplyNFT_A.class);
                                    mIntent.putExtra("post_id", post_response.getPost_id());
                                    context.startActivity(mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    Toast.makeText(context, "팝업 확인", Toast.LENGTH_SHORT).show();
                                    return true;

                                default:
                                    return false;
                            }

                        }
                    });
                    popup.inflate(R.menu.main_liist_menu);
                    popup.setGravity(Gravity.RIGHT | Gravity.END);

                    popup.show();
                } else {
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.action_a2:
                                    Toast.makeText(context, "신고", Toast.LENGTH_SHORT).show();
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

        // 아래는 콘텍스트 메뉴를 이용한 수정 삭제 버튼
//        holder.iv_options.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                MenuItem delete = menu.add(Menu.NONE, R.id.delete, 1, "게시글 삭제");
//                MenuItem modify = menu.add(Menu.NONE, R.id.modify, 2, "게시글 수정");
//                delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.delete:
//                                ApiInterface deletePost_api = ApiClient.getApiClient().create(ApiInterface.class);
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
//                                        Log.e("에러", t.getMessage());
//                                    }
//                                });
//                                return true;
//                        }
//                        return false;
//                    }
//                });
        // 수정 관련
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


        if (post_response.getLike_yn().equals("y")) { // 좋아요를 누른 상태 일 경우
            holder.iv_like_icon.setImageResource(R.drawable.ic_baseline_favorite_24);
        }

        holder.ll_item_layout.setOnClickListener(new View.OnClickListener() { // 아이템 클릭시 포스트 상세보기로
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SS_PostDetail.class);
                intent.putExtra("post_id", post_response.getPost_id());
                intent.putExtra("login_id", post_response.getLogin_id());
                Log.d("디버그태그", "Login_+id: " + post_response.getLogin_id());
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        holder.iv_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageView_FullScreen.class);
                intent.putExtra("img_url", "https://d2gf68dbj51k8e.cloudfront.net/" + post_response.getFile_save_names());
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

                Log.e("좋아요 표시 여부", String.valueOf(holder.like_check));
                if (post_response.getLike_yn().equals("n")) {
                    // ↑ 피드 로드 됐을 때 기준, 좋아요 안눌렀어요
                    if (holder.like_check.equals(false)) {
                        // ↑ 기본 상태 (로드하고 누른적이 없어요)
                        // 그래서 좋아요 누르면 카운트 올려줄거에요
                        int like_count = Integer.parseInt(post_response.getLike_count()) + 1;
                        Log.i("정보태그", "변경 좋아요 수 : " + like_count);
                        holder.tv_like_count.setText("" + like_count);
                        holder.like_check = true;
                        // TODO 좋아요 추가 api
                        ApiInterface like_api = ApiClient.getApiClient().create(ApiInterface.class);
                        Call<String> call = like_api.like(post_response.getPost_id(), user_id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Log.e("좋아요 추가 데이터", response.body().toString());
                                    holder.iv_like_icon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Log.e("좋아요 추가 에러", t.getMessage());
                            }
                        });

                    } else {
                        // 위 if 에서 좋아요 누른거 취소할게요
                        int like_count = Integer.parseInt(post_response.getLike_count());
                        Log.i("정보태그", "변경 좋아요 수 : " + like_count);
                        holder.tv_like_count.setText("" + like_count);
                        holder.like_check = false;
                        // TODO 좋아요 감소 api
                        ApiInterface dislike_api = ApiClient.getApiClient().create(ApiInterface.class);
                        Call<String> call = dislike_api.dislike(post_response.getPost_id(), user_id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Log.e("좋아요 추가 데이터", response.body().toString());
                                    holder.iv_like_icon.setImageResource(R.drawable.ic_outline_favorite_border_24);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Log.e("좋아요 추가 에러", t.getMessage());
                            }
                        });
                    }
                } else {
                    // ↑ 피드 로드 됐을 때 기준, 좋아요 눌렀었어요
                    if (holder.like_check.equals(false)) {
                        // ↑ 기본 상태 (로드하고 누른적이 없어요)
                        // 그래서 취소 시킬거에요
                        int like_count = Integer.parseInt(post_response.getLike_count()) - 1;
                        Log.i("정보태그", "변경 좋아요 수 : " + like_count);
                        holder.tv_like_count.setText("" + like_count);
                        holder.like_check = true;
                        // TODO 좋아요 감소 api
                        ApiInterface dislike_api = ApiClient.getApiClient().create(ApiInterface.class);
                        Call<String> call = dislike_api.dislike(post_response.getPost_id(), user_id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Log.e("좋아요 추가 데이터", response.body().toString());
                                    holder.iv_like_icon.setImageResource(R.drawable.ic_outline_favorite_border_24);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Log.e("좋아요 추가 에러", t.getMessage());
                            }
                        });


                    } else {
                        // 위 if 에서 취소 해버렸어요. 근데 다시 좋아요 누를레요
                        int like_count = Integer.parseInt(post_response.getLike_count());
                        Log.i("정보태그", "변경 좋아요 수 : " + like_count);
                        holder.tv_like_count.setText("" + like_count);
                        holder.like_check = false;
                        // TODO 좋아요 추가 api
                        ApiInterface like_api = ApiClient.getApiClient().create(ApiInterface.class);
                        Call<String> call = like_api.like(post_response.getPost_id(), user_id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Log.e("좋아요 추가 데이터", response.body().toString());
                                    holder.iv_like_icon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Log.e("좋아요 추가 에러", t.getMessage());
                            }
                        });
                    }
                }

            }
        });


        ApiInterface selectCommentUsingPostId_api = ApiClient.getApiClient().create(ApiInterface.class);
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
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
            }
        });


        // TODO 커뮤니티 이름 가져오긴 하는데, 성능 저하 문제는 나중에 고려
        Kim_ApiInterface api2 = Kim_ApiClient.getApiClient().create(Kim_ApiInterface.class);
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
        public LinearLayout ll_item_layout, layout_like;

        ImageView iv_media, iv_options, iv_like_icon, iv_reply_icon, iv_retweet_icon, iv_share_icon;
        TextView tv_user_nick, tv_content, tv_like_count, tv_reply_count, tv_retweet_count, tv_time;
        TextView tv_user_id, tv_com_name;
        SimpleExoPlayerView vd_media;

        CircleImageView iv_user_image;

        public PostViewHolder(@NonNull View view) {
            super(view);
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

}

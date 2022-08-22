package com.example.bubbly.controller;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bubbly.ImageView_FullScreen;
import com.example.bubbly.R;
import com.example.bubbly.SS_PostDetail;
import com.example.bubbly.SS_Profile;
import com.example.bubbly.kim_util_test.CommonUtil;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.post_Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Post_Adapter extends RecyclerView.Adapter<Post_Adapter.PostViewHolder> {

    private Context context;
    private Context mContext;
    private ArrayList<post_Response> lists;
    //    private ItemClickListener itemClickListener;
    SharedPreferences preferences;
    String user_id;


    public Post_Adapter(Context context, ArrayList<post_Response> lists, Context mContext) {
        this.context = context;
        this.lists = lists;
//        this.itemClickListener = itemClickListener;
        this.mContext = mContext; // 이미지 Context 를 활용해서 넣기 위해 추가

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


        // TODO 유저 login_id & 커뮤니티 이름 뜨게 만들기
        holder.tv_user_id.setText(post_response.getPost_writer_id());
//        holder.tv_com_name.setText(post_response.getCommunity_id());
        // TODO if Community 아이디가 0 일 경우, tv_com_name 상태 VIEW.Gone으로 바꾸기



        Glide.with(mContext)
                .load("https://d2gf68dbj51k8e.cloudfront.net/"+post_response.getProfile_file_name())
                .into(holder.iv_user_image);

        Glide.with(mContext)
                .load("https://d2gf68dbj51k8e.cloudfront.net/" + post_response.getFile_save_names())
                .into(holder.iv_media);

        String a = null;
        try {
            a = CommonUtil.beforeTime(getDate(post_response.getCre_datetime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // SNS 형식 시간
        holder.tv_time.setText(a);



        holder.iv_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(holder.iv_options.getContext(), holder.itemView);

                if(user_id.equals(post_response.getPost_writer_id())){
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch(menuItem.getItemId()){
                                case R.id.action_a:
                                    Toast.makeText(context, "팝업 확인", Toast.LENGTH_SHORT).show();
                                    return true;

                                case R.id.action_b:
                                    ApiInterface deletePost_api = ApiClient.getApiClient().create(ApiInterface.class);
                                    Call<String> call = deletePost_api.deletePost(post_response.getPost_id());
                                    call.enqueue(new Callback<String>()
                                    {
                                        @Override
                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                                        {
                                            if (response.isSuccessful() && response.body() != null)
                                            {
                                                //Log.e("delete", String.valueOf(position));
                                                lists.remove(position);
                                                notifyItemRemoved(position);
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                                        {
                                            Log.e("에러", t.getMessage());
                                        }
                                    });
                                    return true;

                                case R.id.action_c:
                                    Toast.makeText(context, "팝업 확인", Toast.LENGTH_SHORT).show();
                                    return true;

                                default:
                                    return false;
                            }

                        }
                    });
                    popup.inflate(R.menu.main_liist_menu);
                    popup.setGravity(Gravity.RIGHT|Gravity.END);

                    popup.show();
                } else {
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch(menuItem.getItemId()){
                                case R.id.action_a2:
                                    Toast.makeText(context, "신고", Toast.LENGTH_SHORT).show();
                                    return true;

                                default:
                                    return false;
                            }

                        }
                    });
                    popup.inflate(R.menu.main_liist_menu2);
                    popup.setGravity(Gravity.RIGHT|Gravity.END);

                    popup.show();
                }

            }
        });



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


    }


    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        Boolean like_check = false;
        public LinearLayout ll_item_layout, layout_like;
//        ItemClickListener itemClickListener;

        ImageView iv_media, iv_options, iv_like_icon, iv_reply_icon, iv_retweet_icon, iv_share_icon;
        TextView tv_user_nick, tv_content, tv_like_count, tv_reply_count, tv_retweet_count, tv_time;
        TextView tv_user_id, tv_com_name;

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

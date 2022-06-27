package com.example.novarand_sns.controller;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.novarand_sns.Add_Posting_Create;
import com.example.novarand_sns.LL_Login;
import com.example.novarand_sns.MM_Home;
import com.example.novarand_sns.MM_Profile;
import com.example.novarand_sns.R;
import com.example.novarand_sns.SS_PostDetail;
import com.example.novarand_sns.retrofit.ApiClient;
import com.example.novarand_sns.retrofit.ApiInterface;
import com.example.novarand_sns.retrofit.post_Response;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

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

    public Post_Adapter(Context context, ArrayList<post_Response> lists, Context mContext)
    {
        this.context = context;
        this.lists = lists;
//        this.itemClickListener = itemClickListener;
        this.mContext = mContext; // 이미지 Context 를 활용해서 넣기 위해 추가

    }

    @NonNull
    @Override
    public Post_Adapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") int position)
    {

        post_Response post_response = lists.get(position);
        preferences = context.getSharedPreferences("novarand",MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

        holder.tv_user_nick.setText(post_response.getNick_name());
        holder.tv_content.setText(post_response.getPost_contents());
        holder.tv_like_count.setText(post_response.getLike_count());
        holder.tv_time.setText(post_response.getCre_datetime());


//        Log.e("getPost_id", post_response.getPost_id());
//        Log.e("getNick_name", post_response.getNick_name());
//        Log.e("getLike_yn()", post_response.getLike_yn());
//        Log.e("getLike_count()", post_response.getLike_count());
//        Log.e("getMentioned_user_list()", Arrays.toString(post_response.getMentioned_user_list()));

        Glide.with(mContext)
                .load("https://d2gf68dbj51k8e.cloudfront.net/e3b15554f15354b5bc31e3e535a59d70.jpeg")
                .circleCrop()
                .into(holder.iv_user_image);

        Glide.with(mContext)
                .load("https://d2gf68dbj51k8e.cloudfront.net/"+post_response.getFile_save_names())
                .into(holder.iv_media);

        if(user_id.equals(post_response.getPost_writer_id())){
            holder.iv_options.setVisibility(View.VISIBLE);
        }

        holder.iv_options.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuItem delete = menu.add(Menu.NONE, R.id.delete, 1, "게시글 삭제");
                MenuItem modify = menu.add(Menu.NONE, R.id.modify, 2, "게시글 수정");
                delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
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
                        }
                        return false;
                    }
                });

                modify.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.modify:
                                Intent intent = new Intent(context, Add_Posting_Create.class);
                                intent.putExtra("post_id",post_response.getPost_id());
                                intent.putExtra("post_content",post_response.getPost_contents());
                                intent.putExtra("post_file",post_response.getFile_save_names());
                                intent.putExtra("post_mention",post_response.getMentioned_user_list());
                                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                return true;
                        }
                        return false;
                    }
                });



            }
        });



        if(post_response.getLike_yn().equals("y")){ // 좋아요를 누른 상태 일 경우
            holder.iv_like_icon.setImageResource(R.drawable.ic_baseline_favorite_24);
        }

        holder.ll_item_layout.setOnClickListener(new View.OnClickListener() { // 아이템 클릭시 포스트 상세보기로
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SS_PostDetail.class);
                intent.putExtra("post_id",post_response.getPost_id());
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        holder.layout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("좋아요 표시 여부", String.valueOf(holder.like_check));
                if(post_response.getLike_yn().equals("n")){
                    // ↑ 피드 로드 됐을 때 기준, 좋아요 안눌렀어요
                    if(holder.like_check.equals(false)){
                        // ↑ 기본 상태 (로드하고 누른적이 없어요)
                        // 그래서 좋아요 누르면 카운트 올려줄거에요
                        int like_count = Integer.parseInt(post_response.getLike_count())+1;
                        Log.i("정보태그", "변경 좋아요 수 : "+ like_count);
                        holder.tv_like_count.setText(""+like_count);
                        holder.like_check = true;
                        // TODO 좋아요 추가 api
                        ApiInterface like_api = ApiClient.getApiClient().create(ApiInterface.class);
                        Call<String> call = like_api.like(post_response.getPost_id(), user_id);
                        call.enqueue(new Callback<String>()
                        {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                            {
                                if (response.isSuccessful() && response.body() != null)
                                {
                                    Log.e("좋아요 추가 데이터", response.body().toString());
                                    holder.iv_like_icon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                            {
                                Log.e("좋아요 추가 에러", t.getMessage());
                            }
                        });

                    } else {
                        // 위 if 에서 좋아요 누른거 취소할게요
                        int like_count = Integer.parseInt(post_response.getLike_count());
                        Log.i("정보태그", "변경 좋아요 수 : "+ like_count);
                        holder.tv_like_count.setText(""+like_count);
                        holder.like_check = false;
                        // TODO 좋아요 감소 api
                        ApiInterface dislike_api = ApiClient.getApiClient().create(ApiInterface.class);
                        Call<String> call = dislike_api.dislike(post_response.getPost_id(), user_id);
                        call.enqueue(new Callback<String>()
                        {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                            {
                                if (response.isSuccessful() && response.body() != null)
                                {
                                    Log.e("좋아요 추가 데이터", response.body().toString());
                                    holder.iv_like_icon.setImageResource(R.drawable.ic_outline_favorite_border_24);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                            {
                                Log.e("좋아요 추가 에러", t.getMessage());
                            }
                        });
                    }
                }else{
                    // ↑ 피드 로드 됐을 때 기준, 좋아요 눌렀었어요
                    if(holder.like_check.equals(false)){
                        // ↑ 기본 상태 (로드하고 누른적이 없어요)
                        // 그래서 취소 시킬거에요
                        int like_count = Integer.parseInt(post_response.getLike_count())-1;
                        Log.i("정보태그", "변경 좋아요 수 : "+ like_count);
                        holder.tv_like_count.setText(""+like_count);
                        holder.like_check = true;
                        // TODO 좋아요 감소 api
                        ApiInterface dislike_api = ApiClient.getApiClient().create(ApiInterface.class);
                        Call<String> call = dislike_api.dislike(post_response.getPost_id(), user_id);
                        call.enqueue(new Callback<String>()
                        {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                            {
                                if (response.isSuccessful() && response.body() != null)
                                {
                                    Log.e("좋아요 추가 데이터", response.body().toString());
                                    holder.iv_like_icon.setImageResource(R.drawable.ic_outline_favorite_border_24);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                            {
                                Log.e("좋아요 추가 에러", t.getMessage());
                            }
                        });


                    } else {
                        // 위 if 에서 취소 해버렸어요. 근데 다시 좋아요 누를레요
                        int like_count = Integer.parseInt(post_response.getLike_count());
                        Log.i("정보태그", "변경 좋아요 수 : "+ like_count);
                        holder.tv_like_count.setText(""+like_count);
                        holder.like_check = false;
                        // TODO 좋아요 추가 api
                        ApiInterface like_api = ApiClient.getApiClient().create(ApiInterface.class);
                        Call<String> call = like_api.like(post_response.getPost_id(), user_id);
                        call.enqueue(new Callback<String>()
                        {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                            {
                                if (response.isSuccessful() && response.body() != null)
                                {
                                    Log.e("좋아요 추가 데이터", response.body().toString());
                                    holder.iv_like_icon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                            {
                                Log.e("좋아요 추가 에러", t.getMessage());
                            }
                        });
                    }
                }

            }
        });



    }

    @Override
    public int getItemCount()
    {
        return lists.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder
    {
        Boolean like_check = false;
        public LinearLayout ll_item_layout,layout_like;
//        ItemClickListener itemClickListener;

        ImageView iv_user_image,iv_media,iv_options,iv_like_icon,iv_reply_icon,iv_retweet_icon,iv_share_icon;
        TextView tv_user_nick,tv_content,tv_like_count,tv_reply_count,tv_retweet_count,tv_time;

        public PostViewHolder(@NonNull View view)
        {
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

            iv_options.setVisibility(View.INVISIBLE);


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
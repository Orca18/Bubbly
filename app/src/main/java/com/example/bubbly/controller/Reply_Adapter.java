package com.example.bubbly.controller;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bubbly.R;
import com.example.bubbly.ReplyModify;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.reply_Response;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Reply_Adapter extends RecyclerView.Adapter<Reply_Adapter.ReplyViewHolder> {

    private Context context;
    private Context mContext;
    private ArrayList<reply_Response> lists;

    SharedPreferences preferences;
    String user_id;


    public Reply_Adapter(Context context, ArrayList<reply_Response> lists, Context mContext)
    {
        this.context = context;
        this.lists = lists;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public Reply_Adapter.ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, @SuppressLint("RecyclerView") int position)
    {

        reply_Response reply_response = lists.get(position);
        preferences = context.getSharedPreferences("novarand",MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

        holder.tv_user_id.setText(reply_response.getComment_writer_id());
        holder.tv_user_nick.setText(reply_response.getNick_name());
        holder.tv_content.setText(reply_response.getComment_contents());


        Glide.with(mContext)
                .load("https://d2gf68dbj51k8e.cloudfront.net/e3b15554f15354b5bc31e3e535a59d70.jpeg")
                .circleCrop()
                .into(holder.iv_user_image);

        if(user_id.equals(reply_response.getComment_writer_id())){
            holder.iv_option.setVisibility(View.VISIBLE);
        }


        holder.iv_option.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuItem delete = menu.add(Menu.NONE, R.id.delete, 1, "답글 삭제");
                MenuItem modify = menu.add(Menu.NONE, R.id.modify, 2, "답글 수정");
                delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                ApiInterface deleteComment_api = ApiClient.getApiClient().create(ApiInterface.class);
                                Call<String> call = deleteComment_api.deleteComment("13");
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
                                Intent intent = new Intent(context, ReplyModify.class);
                                intent.putExtra("comment_id","13");
                                intent.putExtra("comment_content",reply_response.getComment_contents());
                                intent.putExtra("comment_mention",reply_response.getMentioned_user_list());
                                intent.putExtra("post_id",reply_response.getPost_id());
                                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                return true;
                        }
                        return false;
                    }
                });



            }
        });

    }

    @Override
    public int getItemCount()
    {
        return lists.size();
    }

    public class ReplyViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout ll_item_layout,layout_like,layout_reply,layout_retweet;
        public TextView tv_user_nick,tv_user_id,tv_content, tv_time,tv_like_count,
                tv_reply_count,tv_retweet_count;
        public ImageView iv_user_image,iv_media,iv_like_icon,iv_reply_icon,iv_retweet_icon,
                iv_option,iv_share_icon;


        public ReplyViewHolder(@NonNull View view)
        {
            super(view);
            ll_item_layout = view.findViewById(R.id.ll_item_layout);
            layout_like = view.findViewById(R.id.layout_like);
            layout_reply = view.findViewById(R.id.layout_reply);
            layout_retweet = view.findViewById(R.id.layout_retweet);

            tv_user_nick = view.findViewById(R.id.tv_user_nick);
            tv_user_id = view.findViewById(R.id.tv_user_id);
            tv_content = view.findViewById(R.id.tv_content);
            tv_time = view.findViewById(R.id.tv_time);
            tv_like_count = view.findViewById(R.id.tv_like_count);
            tv_reply_count = view.findViewById(R.id.tv_reply_count);
            tv_retweet_count = view.findViewById(R.id.tv_retweet_count);

            iv_user_image = view.findViewById(R.id.iv_user_image);
            iv_media = view.findViewById(R.id.iv_media);
            iv_like_icon = view.findViewById(R.id.iv_like_icon);
            iv_reply_icon = view.findViewById(R.id.iv_reply_icon);
            iv_retweet_icon = view.findViewById(R.id.iv_retweet_icon);
            iv_option = view.findViewById(R.id.iv_option);
            iv_share_icon = view.findViewById(R.id.iv_share_icon);

            iv_option.setVisibility(View.INVISIBLE);


        }

    }

}


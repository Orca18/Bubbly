package com.mainnet.bubbly.controller;
import static android.content.Context.MODE_PRIVATE;

import static com.mainnet.bubbly.SS_PostDetail.reply_count;
import static com.mainnet.bubbly.SS_PostDetail.tv_reply_count;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.ReplyModify;
import com.mainnet.bubbly.SS_Profile;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.kim_util_test.Kim_DateUtil;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.reply_Response;
import com.mainnet.bubbly.retrofit.user_Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
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

        ApiInterface select_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
        Call<List<user_Response>> call_userInfo = select_api.selectUserInfo(user_id);
        call_userInfo.enqueue(new Callback<List<user_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<user_Response>> call, @NonNull Response<List<user_Response>> response)
            {
                System.out.println(response.body());
                //수신한 회원정보를 스태틱으로 저장한다.
                List<user_Response> responseResult = response.body();

                holder.tv_user_id.setText(responseResult.get(0).getLogin_id());
            }
            @Override
            public void onFailure(@NonNull Call<List<user_Response>> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });




        holder.tv_user_nick.setText(reply_response.getNick_name());
        holder.tv_content.setText(reply_response.getComment_contents());


        String a = null;
        try {
            Log.d("디버그태그", "시간테스트2:"+reply_response.getCre_datetime_comment());
            a = Kim_DateUtil.beforeTime_reply(getDate(reply_response.getCre_datetime_comment()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // SNS 형식 시간
        holder.tv_time.setText(a);




        Glide.with(mContext)
                .load(Config.cloudfront_addr+reply_response.getProfile_file_name())
                .circleCrop()
                .into(holder.iv_user_image);

        holder.iv_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SS_Profile.class);
                intent.putExtra("user_id", reply_response.getComment_writer_id());
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        if(user_id.equals(reply_response.getComment_writer_id())){
            holder.iv_option.setVisibility(View.VISIBLE);
        } else {
            holder.iv_option.setVisibility(View.GONE);
        }

        Log.d("디버그태그", "user_id"+user_id+"/"+reply_response.getComment_writer_id());

        holder.iv_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(holder.iv_option.getContext(), holder.itemView);

                if(user_id.equals(reply_response.getComment_writer_id())){
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch(menuItem.getItemId()){
                                case R.id.menu_reply_delete:
                                    ApiInterface deletePost_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
                                    // 코멘트 아이디를 통해서 삭제
                                    Call<String> call = deletePost_api.deleteComment(reply_response.getComment_id());
                                    call.enqueue(new Callback<String>()
                                    {
                                        @Override
                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                                        {
                                            if (response.isSuccessful() && response.body() != null)
                                            {
                                                lists.remove(position);
                                                notifyItemRemoved(position);

                                                reply_count = reply_count - 1;
                                                tv_reply_count.setText(""+reply_count);
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                                        {
                                            Log.e("에러", t.getMessage());
                                        }
                                    });
                                    return true;


                                default:
                                    return false;
                            }

                        }
                    });
                    popup.inflate(R.menu.reply_list_menu);
                    popup.setGravity(Gravity.RIGHT|Gravity.END);

                    popup.show();
                } else {
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch(menuItem.getItemId()){
                                case R.id.menu_reply2_report:
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
                                ApiInterface deleteComment_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
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
    public static Date getDate(String from) throws ParseException {
        // "yyyy-MM-dd HH:mm:ss"
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(from);
        return date;
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
        public ImageView iv_media,iv_like_icon,iv_reply_icon,iv_retweet_icon,
                iv_option,iv_share_icon;
        CircleImageView iv_user_image;


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


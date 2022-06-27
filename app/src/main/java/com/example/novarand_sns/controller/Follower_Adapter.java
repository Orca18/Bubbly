package com.example.novarand_sns.controller;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.novarand_sns.R;
import com.example.novarand_sns.retrofit.ApiClient;
import com.example.novarand_sns.retrofit.ApiInterface;
import com.example.novarand_sns.retrofit.follower_Response;
import com.example.novarand_sns.retrofit.following_Response;
import com.example.novarand_sns.retrofit.reply_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Follower_Adapter extends RecyclerView.Adapter<Follower_Adapter.FollowerViewHolder> {

    private Context context;
    private Context mContext;
    private ArrayList<follower_Response> lists;

    SharedPreferences preferences;
    String user_id;


    public Follower_Adapter(Context context, ArrayList<follower_Response> lists, Context mContext)
    {
        this.context = context;
        this.lists = lists;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public Follower_Adapter.FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follower, parent, false);
        return new FollowerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowerViewHolder holder, int position)
    {

        follower_Response follower_response = lists.get(position);
        preferences = context.getSharedPreferences("novarand",MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

        Log.e("나를 팔로잉 한사람 id", follower_response.getFollower_id());
        holder.tv_user_nick.setText(follower_response.getNick_name());
        Glide.with(mContext)
                .load("https://d2gf68dbj51k8e.cloudfront.net/e3b15554f15354b5bc31e3e535a59d70.jpeg")
                .circleCrop()
                .into(holder.iv_user_image);

//        Log.e("체크1", follower_response.getFollower_id());


        ApiInterface selectFolloweeList_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<following_Response>> call = selectFolloweeList_api.selectFolloweeList(user_id);
        call.enqueue(new Callback<List<following_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<following_Response>> call, @NonNull Response<List<following_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<following_Response> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){
                        if(responseResult.get(i).getFollowee_id().equals(follower_response.getFollower_id())){
                            holder.bt_follow.setVisibility(View.INVISIBLE);
                            holder.bt_unfollow.setVisibility(View.VISIBLE);
                        }

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<following_Response>> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });


        holder.bt_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface createFollowing_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = createFollowing_api.createFollowing(follower_response.getFollower_id(),user_id);
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            Log.e("팔로우 추가 성공", response.body().toString());
                            holder.bt_unfollow.setVisibility(View.VISIBLE);
                            holder.bt_follow.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("팔로우 추가 에러", t.getMessage());
                    }
                });
            }
        });


        holder.bt_unfollow.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuItem delete = menu.add(Menu.NONE, R.id.unfollow, 1, follower_response.getNick_name()+"님 언팔로우하기");
                delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.unfollow:
                                ApiInterface deleteFollowing_api = ApiClient.getApiClient().create(ApiInterface.class);
                                Call<String> call = deleteFollowing_api.deleteFollowing(follower_response.getFollower_id(),user_id);
                                call.enqueue(new Callback<String>()
                                {
                                    @Override
                                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                                    {
                                        if (response.isSuccessful() && response.body() != null)
                                        {
                                            Log.e("팔로우 삭제 성공", response.body().toString());
                                            holder.bt_unfollow.setVisibility(View.INVISIBLE);
                                            holder.bt_follow.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                                    {
                                        Log.e("팔로우 삭제 에러", t.getMessage());
                                    }
                                });

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

    public class FollowerViewHolder extends RecyclerView.ViewHolder // implements View.OnCreateContextMenuListener
    {
        public ConstraintLayout cl_item_layout;
        public TextView tv_user_nick,tv_user_id,tv_follow_check;
        public ImageView iv_user_image;
        public Button bt_follow,bt_unfollow;


        public FollowerViewHolder(@NonNull View view)
        {
            super(view);
            cl_item_layout = view.findViewById(R.id.cl_item_layout);

            tv_user_nick = view.findViewById(R.id.tv_user_nick);
            tv_user_id = view.findViewById(R.id.tv_user_id);
            tv_follow_check = view.findViewById(R.id.tv_follow_check);

            iv_user_image = view.findViewById(R.id.iv_user_image);

            bt_follow = view.findViewById(R.id.bt_follow);
            bt_unfollow = view.findViewById(R.id.bt_unfollow);

//            bt_unfollow.setOnCreateContextMenuListener(this);

//            bt_follow.setVisibility(View.INVISIBLE);
            bt_unfollow.setVisibility(View.INVISIBLE);

        }

//        @Override
//        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            MenuItem delete = menu.add(Menu.NONE, R.id.action_delete, 1, "delete");
//            delete.setOnMenuItemClickListener(onMenuItemClickListener);
//        }
//
//        private final MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.action_delete:
//                        Toast.makeText(mContext.getApplicationContext(), "삭제",Toast.LENGTH_SHORT).show();
//                        return true;
//                }
//                return false;
//            }
//        };
    }

}


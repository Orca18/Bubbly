package com.example.novarand_sns.controller;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class Following_Adapter extends RecyclerView.Adapter<Following_Adapter.FollowingViewHolder> {

    private Context context;
    private Context mContext;
    private ArrayList<following_Response> lists;

    SharedPreferences preferences;
    String user_id;


    public Following_Adapter(Context context, ArrayList<following_Response> lists, Context mContext)
    {
        this.context = context;
        this.lists = lists;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public Following_Adapter.FollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_following, parent, false);
        return new FollowingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowingViewHolder holder, int position)
    {

        following_Response following_response = lists.get(position);
        preferences = context.getSharedPreferences("novarand",MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

        holder.tv_user_nick.setText(following_response.getNick_name());

        Glide.with(mContext)
                .load("https://d2gf68dbj51k8e.cloudfront.net/e3b15554f15354b5bc31e3e535a59d70.jpeg")
                .circleCrop()
                .into(holder.iv_user_image);

        ApiInterface selectFollowerList_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<follower_Response>> call = selectFollowerList_api.selectFollowerList(user_id);
        call.enqueue(new Callback<List<follower_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<follower_Response>> call, @NonNull Response<List<follower_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    List<follower_Response> responseResult = response.body();
                    for(int i=0; i<responseResult.size(); i++){
                        if(responseResult.get(i).getFollower_id().equals(following_response.getFollowee_id())){
                           holder.tv_follow_check.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<follower_Response>> call, @NonNull Throwable t)
            {
                Log.e("에러", t.getMessage());
            }
        });




    }

    @Override
    public int getItemCount()
    {
        return lists.size();
    }

    public class FollowingViewHolder extends RecyclerView.ViewHolder
    {
        public ConstraintLayout cl_item_layout;
        public TextView tv_user_nick,tv_user_id,tv_follow_check;
        public ImageView iv_user_image;
        public Button bt_unfollowing;


        public FollowingViewHolder(@NonNull View view)
        {
            super(view);
            cl_item_layout = view.findViewById(R.id.cl_item_layout);

            tv_user_nick = view.findViewById(R.id.tv_user_nick);
            tv_user_id = view.findViewById(R.id.tv_user_id);
            tv_follow_check = view.findViewById(R.id.tv_follow_check);

            iv_user_image = view.findViewById(R.id.iv_user_image);

            bt_unfollowing = view.findViewById(R.id.bt_unfollowing);
            tv_follow_check.setVisibility(View.INVISIBLE);




        }

    }

}

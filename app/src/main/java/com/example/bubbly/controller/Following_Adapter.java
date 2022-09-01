package com.example.bubbly.controller;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bubbly.R;
import com.example.bubbly.SS_Profile;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.follower_Response;
import com.example.bubbly.retrofit.following_Response;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Following_Adapter extends RecyclerView.Adapter<Following_Adapter.FollowingViewHolder> {

    private Context mContext;
    private ArrayList<following_Response> lists;
    private Following_Adapter_Callback mCallback;

    String user_id = UserInfo.user_id;


    public Following_Adapter(Context context, ArrayList<following_Response> lists, Following_Adapter_Callback callback)
    {
        this.lists = lists;
        this.mContext = context;
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public Following_Adapter.FollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_following, parent, false);
        return new FollowingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowingViewHolder holder, int position)
    {

        following_Response following_response = lists.get(position);
        holder.tv_user_nick.setText(following_response.getNick_name());
        //사용자 login id
        holder.tv_user_id.setText("@"+following_response.getLogin_id());
        System.out.println("filename"+following_response.getProfile_file_name());
        if(following_response.getProfile_file_name()!=null&&!following_response.getProfile_file_name().equals("")){
            Glide.with(mContext)
                    .load("https://d2gf68dbj51k8e.cloudfront.net/"+following_response.getProfile_file_name())
                    .circleCrop()
                    .into(holder.iv_user_image);
        }else{
            //아무 처리도 하지 않는다. default 프로필 이미지 나타남.
        }
        holder.iv_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, SS_Profile.class);
                mIntent.putExtra("user_id",following_response.getFollowee_id());
                mContext.startActivity(mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


//        ApiInterface selectFollowerList_api = ApiClient.getApiClient().create(ApiInterface.class);
//        Call<List<follower_Response>> call = selectFollowerList_api.selectFollowerList(user_id);
//        call.enqueue(new Callback<List<follower_Response>>()
//        {
//            @Override
//            public void onResponse(@NonNull Call<List<follower_Response>> call, @NonNull Response<List<follower_Response>> response)
//            {
//                if (response.isSuccessful() && response.body() != null)
//                {
//                    List<follower_Response> responseResult = response.body();
//                    for(int i=0; i<responseResult.size(); i++){
//                        if(responseResult.get(i).getFollower_id().equals(following_response.getFollowee_id())){
//                           holder.tv_follow_check.setVisibility(View.VISIBLE);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<follower_Response>> call, @NonNull Throwable t)
//            {
//                Log.e("에러", t.getMessage());
//            }
//        });

        holder.bt_unfollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiInterface deleteFollowing_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = deleteFollowing_api.deleteFollowing(following_response.getFollowee_id(),user_id);
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            mCallback.unfollow(holder.getAdapterPosition());
                            Log.e("팔로우 삭제 성공", response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("팔로우 삭제 에러", t.getMessage());
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
        }

    }

}

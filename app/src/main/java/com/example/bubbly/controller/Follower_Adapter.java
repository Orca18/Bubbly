package com.example.bubbly.controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
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

public class Follower_Adapter extends RecyclerView.Adapter<Follower_Adapter.FollowerViewHolder> {

    private Context mContext;
    private ArrayList<follower_Response> lists;

    //SharedPreferences preferences;
    String user_id = UserInfo.user_id;


    public Follower_Adapter(Context context, ArrayList<follower_Response> lists)
    {
        this.lists = lists;
        this.mContext = context;
    }

    @NonNull
    @Override
    public Follower_Adapter.FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_follower, parent, false);
        return new FollowerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowerViewHolder holder, int position)
    {
        //binding data
        follower_Response follower_response = lists.get(position);
        Log.e("나를 팔로잉 한사람 id", follower_response.getFollower_id());
        holder.tv_user_nick.setText(follower_response.getNick_name());
        //사용자 로그인 id 나타남
        holder.tv_user_id.setText("@"+follower_response.getLogin_id());
        if(follower_response.getProfile_file_name()!=null&&!follower_response.getProfile_file_name().equals("")){
            Glide.with(mContext)
                    .load("https://d2gf68dbj51k8e.cloudfront.net/"+follower_response.getProfile_file_name())
                    .circleCrop()
                    .into(holder.iv_user_image);
        }else{
            //아무 처리도 하지 않는다. default 프로필 이미지 나타남.
        }

        holder.iv_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, SS_Profile.class);
                mIntent.putExtra("user_id",follower_response.getFollower_id());
                mContext.startActivity(mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        //이미 팔로우한 사람을 중복 팔로우하지 않게 기존 팔로우 목록을 가져와서 저장한다.
        //followee id가 follower목록에 존재하면 맞팔로우버튼을 숨긴다.
        ApiInterface selectFolloweeList_api = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<following_Response>> call = selectFolloweeList_api.selectFolloweeList(user_id);
        call.enqueue(new Callback<List<following_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<following_Response>> call, @NonNull Response<List<following_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    //내 팔로잉 목록에서 나를 팔로우한 사람이 존재하면 맞팔로우 버튼이 보이지 않게 한다.
                    List<following_Response> responseResult = response.body();
                    boolean isAlreadyFollow = false;
                    for(int i=0; i<responseResult.size(); i++){
                        String element = responseResult.get(i).getFollowee_id();
                        if (element.equals(follower_response.getFollower_id())) {
                            isAlreadyFollow = true;
                            break;
                        } else {
                            isAlreadyFollow = false;
                        }
                    }
                    if(isAlreadyFollow){
                        holder.bt_follow.setVisibility(View.INVISIBLE);
                    }else{
                        holder.bt_follow.setVisibility(View.VISIBLE);
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
                            notifyDataSetChanged();
                            Log.e("팔로우 추가 성공", response.body().toString());
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
        public Button bt_follow;


        public FollowerViewHolder(@NonNull View view)
        {
            super(view);
            cl_item_layout = view.findViewById(R.id.cl_item_layout);
            tv_user_nick = view.findViewById(R.id.tv_user_nick);
            tv_user_id = view.findViewById(R.id.tv_user_id);
            tv_follow_check = view.findViewById(R.id.tv_follow_check);
            iv_user_image = view.findViewById(R.id.iv_user_image);
            bt_follow = view.findViewById(R.id.bt_follow);
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


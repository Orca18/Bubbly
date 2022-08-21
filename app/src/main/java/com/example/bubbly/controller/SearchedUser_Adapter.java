package com.example.bubbly.controller;

import android.content.Context;
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
import com.example.bubbly.model.SearchedUser_Item;
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

public class SearchedUser_Adapter extends RecyclerView.Adapter<SearchedUser_Adapter.ViewHolder> {

    private Context mContext;
    private ArrayList<SearchedUser_Item> lists;

    //SharedPreferences preferences;
    String user_id = UserInfo.user_id;


    public SearchedUser_Adapter(ArrayList<SearchedUser_Item> lists,Context context)
    {
        this.lists = lists;
        this.mContext = context;
    }

    @NonNull
    @Override
    public SearchedUser_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_result_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        //binding data
        SearchedUser_Item user_response = lists.get(position);
        holder.tv_user_nick.setText(user_response.getNick_name());
        holder.tv_user_intro.setText(user_response.getSelf_intro());
        //사용자 로그인 id 나타남
        holder.tv_user_id.setText("@"+user_response.getLogin_id());
        if(user_response.getProfile_file_name()!=null&&!user_response.getProfile_file_name().equals("")){
            Glide.with(mContext)
                    .load("https://d2gf68dbj51k8e.cloudfront.net/"+user_response.getProfile_file_name())
                    .circleCrop()
                    .into(holder.iv_user_image);
        }else{
            //아무 처리도 하지 않는다. default 프로필 이미지 나타남.
        }
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
                    //내 팔로잉 목록에서 나를 팔로우한 사람이 존재하면 언팔로우 버튼이 보이고, 팔로우 버튼이 사라진다.
                    List<following_Response> responseResult = response.body();
                    boolean isAlreadyFollow = false;
                    for(int i=0; i<responseResult.size(); i++){
                        String element = responseResult.get(i).getFollowee_id();
                        if (element.equals(user_response.getUser_id())) {
                            isAlreadyFollow = true;
                            break;
                        } else {
                            isAlreadyFollow = false;
                        }
                    }
                    if(isAlreadyFollow){
                        holder.bt_follow.setVisibility(View.INVISIBLE);
                        holder.bt_unfollow.setVisibility(View.VISIBLE);
                    }else{
                        holder.bt_follow.setVisibility(View.VISIBLE);
                        holder.bt_unfollow.setVisibility(View.INVISIBLE);
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
                Call<String> call = createFollowing_api.createFollowing(user_response.getUser_id(),user_id);
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

        holder.bt_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface deleteFollowing_api = ApiClient.getApiClient().create(ApiInterface.class);
                Call<String> call = deleteFollowing_api.deleteFollowing(user_response.getUser_id(),user_id);
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            notifyDataSetChanged();
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

    public class ViewHolder extends RecyclerView.ViewHolder // implements View.OnCreateContextMenuListener
    {
        public TextView tv_user_nick,tv_user_id,tv_user_intro;
        public ImageView iv_user_image;
        public Button bt_follow,bt_unfollow;


        public ViewHolder(@NonNull View view)
        {
            super(view);
            tv_user_nick = view.findViewById(R.id.tv_user_nick_searchResult);
            tv_user_id = view.findViewById(R.id.tv_user_id_searchResult);
            tv_user_intro = view.findViewById(R.id.tv_selfIntro_searchResult);
            iv_user_image = view.findViewById(R.id.iv_user_image_searchResult);
            bt_follow = view.findViewById(R.id.bt_following_searchResult);
            bt_unfollow = view.findViewById(R.id.bt_unfollow_searchResult);
        }

    }

}


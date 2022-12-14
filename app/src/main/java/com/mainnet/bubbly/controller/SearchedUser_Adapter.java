package com.mainnet.bubbly.controller;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.SS_Profile;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.model.SearchedUser_Item;
import com.mainnet.bubbly.model.UserInfo;
import com.mainnet.bubbly.retrofit.ApiClient;
import com.mainnet.bubbly.retrofit.ApiInterface;
import com.mainnet.bubbly.retrofit.following_Response;

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
        if(user_response.getSelf_intro()!=null&!user_response.getSelf_intro().equals("")&!user_response.getSelf_intro().equals("null")){
            holder.tv_user_intro.setText(user_response.getSelf_intro());
        }else{
            //?????? ??????????????? ?????? ?????? ?????? ????????? ???????????? ??????.
            holder.tv_user_intro.setVisibility(View.GONE);
        }

        //????????? ????????? id ?????????
        holder.tv_user_id.setText("@"+user_response.getLogin_id());

        if(user_response.getProfile_file_name()!=null&&!user_response.getProfile_file_name().equals("")&&!user_response.getProfile_file_name().equals("null")){
            Glide.with(mContext)
                    .load(Config.cloudfront_addr+user_response.getProfile_file_name())
                    .circleCrop()
                    .into(holder.iv_user_image);
        }else{
            //????????? ????????? ????????? ?????????. default ?????????
        }

        holder.iv_user_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, SS_Profile.class);
                mIntent.putExtra("user_id",user_response.getUser_id());
                mContext.startActivity(mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

         //?????? ???????????? ????????? ?????? ??????????????? ?????? ?????? ????????? ????????? ???????????? ????????????.
        //followee id??? follower????????? ???????????? ????????????????????? ?????????.
        ApiInterface selectFolloweeList_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
        Call<List<following_Response>> call = selectFolloweeList_api.selectFolloweeList(user_id);
        call.enqueue(new Callback<List<following_Response>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<following_Response>> call, @NonNull Response<List<following_Response>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    //??? ????????? ???????????? ?????? ???????????? ????????? ???????????? ???????????? ????????? ?????????, ????????? ????????? ????????????.
                    List<following_Response> responseResult = response.body();
                    boolean isAlreadyFollow = false;
                    for(int i=0; i<responseResult.size(); i++){

                        String element = responseResult.get(i).getFollowee_id();
                        System.out.println(element+"?????? ??????-??? ????????????"+responseResult.get(i).getFollowee_id());
                        if (element.equals(user_response.getUser_id())) {
                            isAlreadyFollow = true;
                            break;
                        } else {
                            isAlreadyFollow = false;
                        }
                    }
                    System.out.println(isAlreadyFollow);
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
                Log.e("??????", t.getMessage());
            }
        });


        holder.bt_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface createFollowing_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
                Call<String> call = createFollowing_api.createFollowing(user_response.getUser_id(),user_id);
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            notifyDataSetChanged();
                            Log.e("????????? ?????? ??????", response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("????????? ?????? ??????", t.getMessage());
                    }
                });
            }
        });

        holder.bt_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface deleteFollowing_api = ApiClient.getApiClient(mContext).create(ApiInterface.class);
                Call<String> call = deleteFollowing_api.deleteFollowing(user_response.getUser_id(),user_id);
                call.enqueue(new Callback<String>()
                {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            notifyDataSetChanged();
                            Log.e("????????? ?????? ??????", response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t)
                    {
                        Log.e("????????? ?????? ??????", t.getMessage());
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


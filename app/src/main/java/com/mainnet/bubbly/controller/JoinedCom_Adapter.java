package com.mainnet.bubbly.controller;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mainnet.bubbly.Community_MainPage;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.kim_util_test.Kim_ApiClient;
import com.mainnet.bubbly.kim_util_test.Kim_ApiInterface;
import com.mainnet.bubbly.kim_util_test.Kim_Com_Members_Response;
import com.mainnet.bubbly.kim_util_test.Kim_JoinedCom_Response;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinedCom_Adapter extends RecyclerView.Adapter<JoinedCom_Adapter.Joined_ViewHolder> {

    Context mContext;
    ArrayList<Kim_JoinedCom_Response> lists;

    SharedPreferences preferences;
    String user_id;




    public JoinedCom_Adapter(Context mContext, ArrayList<Kim_JoinedCom_Response> lists) {
        this.mContext = mContext;
        this.lists = lists;
    }


    @NonNull
    @Override
    public Joined_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        //TODO 리사이클러뷰 아이템 레이아웃
        v = LayoutInflater.from(mContext).inflate(R.layout.item_joined_com_list, parent, false);
        Joined_ViewHolder vHolder = new Joined_ViewHolder(v);

        return vHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull Joined_ViewHolder holder, int position) {

        Kim_JoinedCom_Response response = lists.get(position);
        preferences = mContext.getSharedPreferences("novarand", MODE_PRIVATE);
        user_id = preferences.getString("user_id", ""); // 로그인한 user_id값

        holder.name.setText(response.getCommunity_name());
//        holder.members.setText(response.getCommunity_membercount());
        // TODO 멤버수 받아오기 ↑

          holder.description.setText(response.getCommunity_desc());


        Glide.with(mContext)
                .load(Config.cloudfront_addr + response.getProfile_file_name())
                .centerCrop()
                .into(holder.profile);

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, Community_MainPage.class);
                mIntent.putExtra("com_id", response.getCommunity_id());
                mContext.startActivity(mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        // 1. 레트로핏 빌드 & 인터페이스 지정?
        Kim_ApiInterface take = Kim_ApiClient.getApiClient(mContext).create(Kim_ApiInterface.class);
        // 2. Response = 인터페이스내함수 // user_id 보내서 원하는 response 기다림
        Call<List<Kim_Com_Members_Response>> call = take.selectCommunityParticipantList(response.getCommunity_id());
        // 3. 선언한 call 을 게시글용 DTO
        call.enqueue(new Callback<List<Kim_Com_Members_Response>>() {
            @Override
            public void onResponse(@NonNull Call<List<Kim_Com_Members_Response>> call, @NonNull Response<List<Kim_Com_Members_Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 리스트 새로 만들어서
                    List<Kim_Com_Members_Response> responseResult = response.body();
                    holder.members.setText(String.valueOf(responseResult.size()));
                }
            }

            @Override
            public void onFailure(Call<List<Kim_Com_Members_Response>> call, Throwable t) {
                Log.e("게시물 아이디로 게시물 조회", t.getMessage());
            }

        });




    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public static class Joined_ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll;
        ImageView profile;
        TextView name, members, description;
        Button btn;


        public Joined_ViewHolder(@NonNull View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.joined_com_ll);
            profile = itemView.findViewById(R.id.joined_com_profile);
            name = itemView.findViewById(R.id.joined_com_name);
            members = itemView.findViewById(R.id.joined_com_members);
            btn = itemView.findViewById(R.id.joined_com_btn);
            description = itemView.findViewById(R.id.joined_com_desc);

        }
    }

    public void setFilter(ArrayList<Kim_JoinedCom_Response> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

}


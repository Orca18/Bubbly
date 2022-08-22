package com.example.bubbly.controller;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.bubbly.Community_MainPage;
import com.example.bubbly.R;
import com.example.bubbly.kim_util_test.Kim_JoinedCom_Response;


import java.util.ArrayList;

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
                .load("https://d2gf68dbj51k8e.cloudfront.net/" + response.getProfile_file_name())
                .centerCrop()
                .into(holder.profile);

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, Community_MainPage.class);
                mIntent.putExtra("com_id", response.getCommunity_id());
                mIntent.putExtra("join_yn", "y");
                mContext.startActivity(mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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


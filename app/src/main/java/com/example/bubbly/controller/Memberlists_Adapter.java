package com.example.bubbly.controller;
import android.content.Intent;
import android.widget.Toast;
import android.content.Context;
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
import com.example.bubbly.R;
import com.example.bubbly.SS_Profile;
import com.example.bubbly.kim_util_test.Kim_Com_Members_Response;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Memberlists_Adapter extends RecyclerView.Adapter<Memberlists_Adapter.ExampleViewHolder> {

    Context mContext;
    List<Kim_Com_Members_Response> mData;

    public Memberlists_Adapter(Context mContext, List<Kim_Com_Members_Response> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }



    @NonNull
    @Override
    public Memberlists_Adapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        //TODO 리사이클러뷰 아이템 레이아웃
        v = LayoutInflater.from(mContext).inflate(R.layout.item_com_member_list, parent, false);
        Memberlists_Adapter.ExampleViewHolder vHolder = new Memberlists_Adapter.ExampleViewHolder(v);

        return vHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull Memberlists_Adapter.ExampleViewHolder holder, int position) {

        Kim_Com_Members_Response currentItem = this.mData.get(position);

        Glide.with(mContext)
                .load("https://d2gf68dbj51k8e.cloudfront.net/" + currentItem.getProfile_file_name())
                .centerCrop()
                .into(holder.cv_profile);
        
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext.getApplicationContext(),"해당 유저의 프로필로 이동\nSS 프로필 user_id 번들",Toast.LENGTH_SHORT).show();
                Intent mIntent = new Intent(mContext, SS_Profile.class);
                mContext.startActivity(mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        holder.tv_name.setText(currentItem.getNick_name());
        holder.tv_id.setText(currentItem.getUser_id());
        
        holder.iv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext.getApplicationContext(),"팔로우 버튼 or 쪽지 넣을지 고민중\n(어차피 프로필 가서 해도 됨)",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        
        LinearLayout ll;
        CircleImageView cv_profile;
        TextView tv_name, tv_id;
        ImageView iv_follow;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ll = itemView.findViewById(R.id.item_com_member_ll);
            cv_profile = itemView.findViewById(R.id.item_com_member_profile);
            tv_name = itemView.findViewById(R.id.item_com_member_name);
            tv_id = itemView.findViewById(R.id.item_com_member_id);
            iv_follow = itemView.findViewById(R.id.item_com_member_add);

        }
    }

    public void setFilter(List<Kim_Com_Members_Response> filterdNames) {
        this.mData = filterdNames;
        notifyDataSetChanged();
    }

}

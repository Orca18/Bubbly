package com.example.bubbly.controller;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bubbly.Community_Info;
import com.example.bubbly.R;
import com.example.bubbly.SS_Profile;
import com.example.bubbly.model.Joined_Com_Item;


import java.util.List;

public class JoinedCom_Adapter extends RecyclerView.Adapter<JoinedCom_Adapter.Joined_ViewHolder> {

    Context mContext;
    List<Joined_Com_Item> mData;
    //ExampleItem(String imageResource, String text1, String text2, String text3, String text4)

    public JoinedCom_Adapter(Context mContext, List<Joined_Com_Item> mData) {
        this.mContext = mContext;
        this.mData = mData;
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

        Joined_Com_Item currentItem = this.mData.get(position);

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, Community_Info.class);
                mIntent.putExtra("comm_id","1");
                mContext.startActivity(mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
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

        }
    }

    public void setFilter(List<Joined_Com_Item> filterdNames) {
        this.mData = filterdNames;
        notifyDataSetChanged();
    }

}


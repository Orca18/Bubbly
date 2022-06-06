package com.example.novarand_sns.controller;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.novarand_sns.R;
import com.example.novarand_sns.model.Reply_Item;

import java.util.List;


public class Reply_Adapter extends RecyclerView.Adapter<Reply_Adapter.ExampleViewHolder> {

    Context mContext;
    List<Reply_Item> mData;
    //ExampleItem(String imageResource, String text1, String text2, String text3, String text4)

    public Reply_Adapter(Context mContext, List<Reply_Item> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }



    @NonNull
    @Override
    public Reply_Adapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        //TODO 리사이클러뷰 아이템 레이아웃
        v = LayoutInflater.from(mContext).inflate(R.layout.item_reply, parent, false);
        Reply_Adapter.ExampleViewHolder vHolder = new Reply_Adapter.ExampleViewHolder(v);

        return vHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull Reply_Adapter.ExampleViewHolder holder, int position) {

        Reply_Item currentItem = this.mData.get(position);

        //글라이드 이미지 세팅  TODO https://github.com/bumptech/glide
//        Glide.with(holder.itemView.getContext()).load(currentItem.getImageResource()).centerCrop().into(holder.img);



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

    	ImageView img;
        Button btn;

        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
            //TODO 선언
            //btn = itemView.findViewById(R.id.아이템_버튼);
            //img = itemView.findViewById(R.id.아이템_이미지뷰);
        }
    }

    public void setFilter(List<Reply_Item> filterdNames) {
        this.mData = filterdNames;
        notifyDataSetChanged();
    }

}


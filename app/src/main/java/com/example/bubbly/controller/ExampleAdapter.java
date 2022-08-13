package com.example.bubbly.controller;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bubbly.R;
import com.example.bubbly.model.ExampleItem;


import java.util.List;

public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {

    Context mContext;
    List<ExampleItem> mData;
    //ExampleItem(String imageResource, String text1, String text2, String text3, String text4)

    public ExampleAdapter(Context mContext, List<ExampleItem> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }



    @NonNull
    @Override
    public ExampleAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        //TODO 리사이클러뷰 아이템 레이아웃
        v = LayoutInflater.from(mContext).inflate(R.layout.item_joined_com_list, parent, false);
        ExampleAdapter.ExampleViewHolder vHolder = new ExampleAdapter.ExampleViewHolder(v);

        return vHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ExampleAdapter.ExampleViewHolder holder, int position) {

        ExampleItem currentItem = this.mData.get(position);


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

    public void setFilter(List<ExampleItem> filterdNames) {
        this.mData = filterdNames;
        notifyDataSetChanged();
    }

}


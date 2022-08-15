package com.example.bubbly.controller;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bubbly.R;
import com.example.bubbly.model.Fragment_Tab1_Item;

import java.util.List;

public class Profile_Tab1_Adapter extends RecyclerView.Adapter<Profile_Tab1_Adapter.Profile_Tab1_ViewHolder> {

    Context mContext;
    List<Fragment_Tab1_Item> mData;
    //ExampleItem(String imageResource, String text1, String text2, String text3, String text4)

    public Profile_Tab1_Adapter(Context mContext, List<Fragment_Tab1_Item> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }



    @NonNull
    @Override
    public Profile_Tab1_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        //TODO 리사이클러뷰 아이템 레이아웃
        v = LayoutInflater.from(mContext).inflate(R.layout.item_test, parent, false);
        Profile_Tab1_ViewHolder vHolder = new Profile_Tab1_ViewHolder(v);

        return vHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull Profile_Tab1_ViewHolder holder, int position) {

        Fragment_Tab1_Item currentItem = this.mData.get(position);
        holder.test.setText(currentItem.getTest1());

        //글라이드 이미지 세팅  TODO https://github.com/bumptech/glide
//        Glide.with(holder.itemView.getContext()).load(currentItem.getImageResource()).centerCrop().into(holder.img);

        //액티비티 전환
//        holder.btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //TODO 인텐트 만들어주기
//                //Intent intent = new Intent(mContext, 액티비티.class);
//                //어답터에서 클릭 이용할 때, 아래 해줘야됨!
//                //mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class Profile_Tab1_ViewHolder extends RecyclerView.ViewHolder {

        TextView test;

        public Profile_Tab1_ViewHolder(@NonNull View itemView) {
            super(itemView);
            //TODO 선언
            //btn = itemView.findViewById(R.id.아이템_버튼);
            //img = itemView.findViewById(R.id.아이템_이미지뷰);
            test = itemView.findViewById(R.id.test_text_view);
        }
    }

    public void setFilter(List<Fragment_Tab1_Item> filterdNames) {
        this.mData = filterdNames;
        notifyDataSetChanged();
    }

}


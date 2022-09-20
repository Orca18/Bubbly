package com.mainnet.bubbly.controller;
import android.util.Log;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mainnet.bubbly.Post_Create;
import com.mainnet.bubbly.R;

import java.util.ArrayList;

public class DialogRecyclerAdapter extends RecyclerView.Adapter<DialogRecyclerAdapter.ViewHolder> {
    private ArrayList<String> mData = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewRecyclerItem); // 각 item View에 대해
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String topic = mData.get(getAdapterPosition());
                    Post_Create.com.setText(topic);
                    Log.d("디버그태그", "포지션 체크"+getAdapterPosition());
                    Post_Create.category_com_id = Post_Create.ids[getAdapterPosition()];
                    Log.d("디버그태그", "카테고리 번호:"+Post_Create.category_com_id);
                    if (topic.equals("카테고리 없음")) { // 카테고리 없음 선택 시
                        Post_Create.com.setTextColor(Color.LTGRAY);
                        // 회색 처리
                    } else {
                        Post_Create.com.setTextColor(Color.BLACK);
                        // 그 이외엔 검은색
                    }
                    Post_Create.dialog.dismiss(); // dialog 종료
                }
            });
        }
    }
    public DialogRecyclerAdapter(ArrayList<String> list) {
        mData = list; // 입력받은 list를 저장
    }

    @Override
    public DialogRecyclerAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        Context context = parent.getContext(); // parent로부터 content 받음
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recyclerview_topic_item, parent, false);
        // 각 item의 View는 이전에 정의했던 item layout을 불러옴
        DialogRecyclerAdapter.ViewHolder vh = new DialogRecyclerAdapter.ViewHolder(view);
        return vh; // ViewHolder 반환
    }

    @Override
    public void onBindViewHolder(DialogRecyclerAdapter.ViewHolder holder, int position) {
        String text = mData.get(position); // 어떤 포지션의 텍스트인지 조회
        holder.textView.setText(text); // 해당 포지션의 View item에 텍스트 입힘
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
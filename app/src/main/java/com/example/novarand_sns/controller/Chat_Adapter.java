package com.example.novarand_sns.controller;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.novarand_sns.R;
import com.example.novarand_sns.model.Chat_Item;

import java.util.List;


public class Chat_Adapter extends RecyclerView.Adapter<Chat_Adapter.ChatViewHolder> {

    Context mContext;
    List<Chat_Item> mData;

    public Chat_Adapter(Context mContext, List<Chat_Item> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }



    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        v = LayoutInflater.from(mContext).inflate(R.layout.item_chat_me, parent, false);
        ChatViewHolder vHolder = new ChatViewHolder(v);

        return vHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        Chat_Item currentItem = this.mData.get(position);
        holder.chattest.setText(currentItem.chatTEST());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
    	TextView chattest;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chattest = itemView.findViewById(R.id.chat_chat_test);
        }
    }

    public void setFilter(List<Chat_Item> filterdNames) {
        this.mData = filterdNames;
        notifyDataSetChanged();
    }

}

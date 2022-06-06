package com.example.novarand_sns.controller;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.novarand_sns.ChattingRoom;
import com.example.novarand_sns.R;
import com.example.novarand_sns.SS_PostDetail;
import com.example.novarand_sns.model.Notices_Item;

import java.util.List;


public class Notices_Adapter extends RecyclerView.Adapter<Notices_Adapter.NoticesViewHolder> {

    Context mContext;
    List<Notices_Item> mData;

    public Notices_Adapter(Context mContext, List<Notices_Item> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }



    @NonNull
    @Override
    public NoticesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        v = LayoutInflater.from(mContext).inflate(R.layout.item_notice, parent, false);
        NoticesViewHolder vHolder = new NoticesViewHolder(v);

        return vHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull NoticesViewHolder holder, int position) {

        Notices_Item currentItem = this.mData.get(position);
        holder.from.setText(currentItem.testFrom());

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 인텐트 만들어주기
                Intent intent = new Intent(mContext, SS_PostDetail.class);
                //어답터에서 클릭 이용할 때, 아래 해줘야됨!
                mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class NoticesViewHolder extends RecyclerView.ViewHolder {

        TextView from;
        ImageView delete;
        LinearLayout ll;

        public NoticesViewHolder(@NonNull View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.item_notice_ll);
            from = itemView.findViewById(R.id.item_notice_from);
            delete = itemView.findViewById(R.id.item_notice_delete);
        }
    }

    public void setFilter(List<Notices_Item> filterdNames) {
        this.mData = filterdNames;
        notifyDataSetChanged();
    }



}

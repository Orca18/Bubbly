package com.example.novarand_sns.controller;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.novarand_sns.R;
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

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class NoticesViewHolder extends RecyclerView.ViewHolder {

        TextView from;

        public NoticesViewHolder(@NonNull View itemView) {
            super(itemView);
            from = itemView.findViewById(R.id.item_notice_from);
        }
    }

    public void setFilter(List<Notices_Item> filterdNames) {
        this.mData = filterdNames;
        notifyDataSetChanged();
    }

}

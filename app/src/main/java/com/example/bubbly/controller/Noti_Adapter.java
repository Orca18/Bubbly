package com.example.bubbly.controller;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bubbly.R;
import com.example.bubbly.SS_PostDetail;
import com.example.bubbly.model.Noti_Item;

import java.util.List;


public class Noti_Adapter extends RecyclerView.Adapter<Noti_Adapter.NoticesViewHolder> {

    Context mContext;
    List<Noti_Item> mData;

    public Noti_Adapter(Context mContext, List<Noti_Item> mData) {
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

        Noti_Item currentItem = this.mData.get(position);
        holder.from.setText(currentItem.getTestText());

        String notiID = mData.get(position).getTestText().toString();

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notiChecked(notiID);

                Intent intent = new Intent(mContext, SS_PostDetail.class);
                //어답터에서 클릭 이용할 때, 아래 해줘야됨!
                mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 해당 알림을 삭제했다는걸 서버에 보냄
                notidelete(notiID);

                mData.remove(currentItem);
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), mData.size());
            }
        });
        

    }

    // 알림 클릭함
    private void notiChecked(String notiID) {

    }

    // 알림 삭제
    private void notidelete(String notiID) {
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

    public void setFilter(List<Noti_Item> filterdNames) {
        this.mData = filterdNames;
        notifyDataSetChanged();
    }



}

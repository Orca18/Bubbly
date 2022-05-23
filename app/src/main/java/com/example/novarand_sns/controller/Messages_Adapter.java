package com.example.novarand_sns.controller;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.novarand_sns.R;
import com.example.novarand_sns.model.Messages_Item;
import com.example.novarand_sns.model.Posts_Item;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class Messages_Adapter extends RecyclerView.Adapter<Messages_Adapter.MessagesViewHolder> {

    Context mContext;
    List<Messages_Item> mData;

    public Messages_Adapter(Context mContext, List<Messages_Item> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }



    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        v = LayoutInflater.from(mContext).inflate(R.layout.item_message_basic, parent, false);
        MessagesViewHolder vHolder = new MessagesViewHolder(v);

        return vHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {

        // 1. 프사 링크 - 2. 유저명 - 3. 아이디 - 4. 내용 5. 미디어 6. 좋아요수 7. 답글수 8.  리트윗수 9. 게시글 링크 10. 게시 시간
        Messages_Item currentItem = this.mData.get(position);

        Glide.with(holder.itemView.getContext()).load(currentItem.getChatProfile()).centerCrop().into(holder.chatprofile);

        holder.name.setText(currentItem.getChatName());
        holder.content.setText(currentItem.getChatContent());
        holder.time.setText(currentItem.getChattime());

        //액티비티 전환
        holder.item_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 인텐트 만들어주기
                //Intent intent = new Intent(mContext, 액티비티.class);
                //어답터에서 클릭 이용할 때, 아래 해줘야됨!
                //mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                Toast.makeText(view.getContext(),"일단 이동은 안함",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder {
    
        LinearLayout item_message;
        CircleImageView chatprofile;
        TextView name, content, time;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            item_message = itemView.findViewById(R.id.item_message);
            chatprofile = itemView.findViewById(R.id.item_message_profile);
            name = itemView.findViewById(R.id.item_message_name);
            content = itemView.findViewById(R.id.item_message_content);
            time = itemView.findViewById(R.id.item_message_time);

        }
    }

    public void setFilter(List<Messages_Item> filterdNames) {
        this.mData = filterdNames;
        notifyDataSetChanged();
    }

}

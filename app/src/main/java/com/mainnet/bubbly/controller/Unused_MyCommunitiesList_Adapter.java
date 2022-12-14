package com.mainnet.bubbly.controller;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.model.MyCommunitiesList_Item;

import java.util.List;


public class Unused_MyCommunitiesList_Adapter extends RecyclerView.Adapter<Unused_MyCommunitiesList_Adapter.PostsViewHolder> {

    Context mContext;
    List<MyCommunitiesList_Item> mData;

    public Unused_MyCommunitiesList_Adapter(Context mContext, List<MyCommunitiesList_Item> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }



    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        v = LayoutInflater.from(mContext).inflate(R.layout.item_communitylist, parent, false);
        PostsViewHolder vHolder = new PostsViewHolder(v);

        return vHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {

        // 1. 프사 링크 - 2. 유저명 - 3. 아이디 - 4. 내용 5. 미디어 6. 좋아요수 7. 답글수 8.  리트윗수 9. 게시글 링크 10. 게시 시간
        MyCommunitiesList_Item currentItem = this.mData.get(position);

        Glide.with(holder.itemView.getContext()).load(currentItem.getProfileImageURL()).centerCrop().into(holder.userProfileIMG);
        holder.post_Username.setText(currentItem.getUserName());
        holder.post_UserID.setText(currentItem.getUserId());
        holder.post_Content.setText(currentItem.getPostContent());
        Glide.with(holder.itemView.getContext()).load(currentItem.getPostMedia()).fitCenter().apply(RequestOptions.bitmapTransform(new RoundedCorners(14))).into(holder.contentIMG);
        holder.post_likecount.setText(""+currentItem.getPostLikeCount());
        holder.post_replycount.setText(""+currentItem.getPostReplyCount());
        holder.post_rebucount.setText(""+currentItem.getPostRebuCount());
        String shareURL = currentItem.getPostURL();
        holder.post_Time.setText(currentItem.getPostTime());

        //액티비티 전환
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 인텐트 만들어주기
                //Intent intent = new Intent(mContext, 액티비티.class);
                //어답터에서 클릭 이용할 때, 아래 해줘야됨!
                //mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                Toast.makeText(view.getContext(),"일단은 공유 버튼만 구현 : "+shareURL,Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
    	ImageView userProfileIMG, contentIMG;
    	TextView post_Username, post_UserID, post_Content, post_Time;
    	TextView post_likecount, post_replycount, post_rebucount;

    	ImageView like, reply, retweet;
    	ImageView share;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileIMG = itemView.findViewById(R.id.item_commu_profile);
            post_Username = itemView.findViewById(R.id.item_commu_username);
            post_UserID = itemView.findViewById(R.id.item_commu_userID);
            post_Content = itemView.findViewById(R.id.item_commu_content);
            post_likecount = itemView.findViewById(R.id.item_commu_likecount);
            post_replycount = itemView.findViewById(R.id.item_commu_reply_count);
            post_rebucount = itemView.findViewById(R.id.item_commu_retweet_count);
            post_Time = itemView.findViewById(R.id.item_commu_time);
            contentIMG = itemView.findViewById(R.id.item_commu_media);
            // 버튼
            like = itemView.findViewById(R.id.item_commu_like_icon);
            reply = itemView.findViewById(R.id.item_commu_reply_icon);
            retweet = itemView.findViewById(R.id.item_commu_retweet_icon);
            share = itemView.findViewById(R.id.item_commu_share_icon);
        }
    }

    public void setFilter(List<MyCommunitiesList_Item> filterdNames) {
        this.mData = filterdNames;
        notifyDataSetChanged();
    }

}

package com.example.novarand_sns.controller;
import android.util.Log;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.novarand_sns.R;
import com.example.novarand_sns.SS_PostDetail;
import com.example.novarand_sns.SS_Profile;
import com.example.novarand_sns.model.Feed_Item;

import java.util.List;


public class Feed_Adapter extends RecyclerView.Adapter<Feed_Adapter.PostsViewHolder> implements View.OnCreateContextMenuListener {

    Context mContext;
    List<Feed_Item> mData;

    public Feed_Adapter(Context mContext, List<Feed_Item> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        v = LayoutInflater.from(mContext).inflate(R.layout.item_feed_basic, parent, false);
        PostsViewHolder vHolder = new PostsViewHolder(v);

        return vHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {

        // 1. 프사 링크 - 2. 유저명 - 3. 아이디 - 4. 내용 5. 미디어 6. 좋아요수 7. 답글수 8.  리트윗수 9. 게시글 링크 10. 게시 시간
        Feed_Item currentItem = this.mData.get(position);

        Glide.with(holder.itemView.getContext()).load(currentItem.getProfileImageURL()).centerCrop().into(holder.userProfileIMG);
        holder.post_Username.setText(currentItem.getUserName());
        holder.post_UserID.setText(currentItem.getUserId());
        holder.post_Content.setText(currentItem.getPostContent());
        Glide.with(holder.itemView.getContext()).load(currentItem.getPostMedia()).fitCenter().apply(RequestOptions.bitmapTransform(new RoundedCorners(14))).into(holder.contentIMG);
        holder.post_likecount.setText("" + currentItem.getPostLikeCount());
        holder.post_replycount.setText("" + currentItem.getPostReplyCount());
        holder.post_rebucount.setText("" + currentItem.getPostRebuCount());
        String shareURL = currentItem.getPostURL();
        holder.post_Time.setText(currentItem.getPostTime());
        if(currentItem.getLikebool().equals(true)){
            holder.like.setImageResource(R.drawable.ic_baseline_favorite_24);
        }

        holder.userProfileIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(mContext, SS_Profile.class);
                mContext.startActivity(mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        //액티비티 전환
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 인텐트 만들어주기
                //Intent intent = new Intent(mContext, 액티비티.class);
                //어답터에서 클릭 이용할 때, 아래 해줘야됨!
                //mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                Toast.makeText(view.getContext(), "게시물 링크 공유 기능 " + shareURL, Toast.LENGTH_SHORT).show();
            }
        });

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SS_PostDetail.class);
                mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        holder.touchlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("정보태그", "좋아요표시여부:"+holder.좋아요표시여부);



                if(currentItem.getLikebool().equals(false)){
                    // ↑ 피드 로드 됐을 때 기준, 좋아요 안눌렀어요
                    if(holder.좋아요표시여부.equals(false)){
                        // ↑ 기본 상태 (로드하고 누른적이 없어요)
                        // 그래서 좋아요 누르면 카운트 올려줄거에요
                        int pluscount = currentItem.getPostLikeCount() + 1;
                        Log.i("정보태그", "변경 좋아요 수 : "+ pluscount);
                        holder.post_likecount.setText(""+pluscount);
                        holder.좋아요표시여부 = true;
                        // TODO HTTP 추가 요청 보내기
                        holder.like.setImageResource(R.drawable.ic_baseline_favorite_24);
                    } else {
                        // 위 if 에서 좋아요 누른거 취소할게요
                        int minuscount = currentItem.getPostLikeCount();
                        Log.i("정보태그", "변경 좋아요 수 : "+ minuscount);
                        holder.post_likecount.setText(""+minuscount);
                        holder.좋아요표시여부 = false;
                        // TODO HTTP 감소 요청 보내기
                        holder.like.setImageResource(R.drawable.ic_outline_favorite_border_24);
                    }
                }else{
                    // ↑ 피드 로드 됐을 때 기준, 좋아요 눌렀었어요
                    if(holder.좋아요표시여부.equals(false)){
                        // ↑ 기본 상태 (로드하고 누른적이 없어요)
                        // 그래서 취소 시킬거에요
                        int pluscount = currentItem.getPostLikeCount() - 1;
                        Log.i("정보태그", "변경 좋아요 수 : "+ pluscount);
                        holder.post_likecount.setText(""+pluscount);
                        holder.좋아요표시여부 = true;
                        // TODO HTTP 감소 요청 보내기
                        holder.like.setImageResource(R.drawable.ic_outline_favorite_border_24);
                    } else {
                        // 위 if 에서 취소 해버렸어요. 근데 다시 좋아요 누를레요
                        int minuscount = currentItem.getPostLikeCount();
                        Log.i("정보태그", "변경 좋아요 수 : "+ minuscount);
                        holder.post_likecount.setText(""+minuscount);
                        holder.좋아요표시여부 = false;
                        // TODO HTTP 추가 요청 보내기
                        holder.like.setImageResource(R.drawable.ic_baseline_favorite_24);
                    }
                }






            }
        });


        // 삭제/신고
        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem delete = menu.add(Menu.NONE, R.id.action_delete, 1, "delete");
        delete.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    private final MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    Toast.makeText(mContext.getApplicationContext(), "삭제",Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    };

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileIMG, contentIMG;
        TextView post_Username, post_UserID, post_Content, post_Time;
        TextView post_likecount, post_replycount, post_rebucount;

        LinearLayout touchlike, touchreply, touchbubble, touchshare;

        ImageView like, reply, retweet;
        ImageView share, option;

        LinearLayout ll;

        Boolean 좋아요표시여부 = false;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileIMG = itemView.findViewById(R.id.feed_basic_profile);
            post_Username = itemView.findViewById(R.id.feed_basic_username);
            post_UserID = itemView.findViewById(R.id.feed_basic_userID);
            post_Content = itemView.findViewById(R.id.feed_basic_content);
            post_likecount = itemView.findViewById(R.id.feed_basic_likecount);
            post_replycount = itemView.findViewById(R.id.feed_basic_reply_count);
            post_rebucount = itemView.findViewById(R.id.feed_basic_retweet_count);
            post_Time = itemView.findViewById(R.id.feed_basic_time);
            contentIMG = itemView.findViewById(R.id.feed_basic_media);
            option = itemView.findViewById(R.id.feed_basic_options);
            // 버튼 아이콘
            like = itemView.findViewById(R.id.feed_basic_like_icon);
            reply = itemView.findViewById(R.id.feed_basic_reply_icon);
            retweet = itemView.findViewById(R.id.feed_basic_retweet_icon);
            share = itemView.findViewById(R.id.feed_basic_share_icon);

            ll = itemView.findViewById(R.id.item_feed_basic_ll);

            // 작동 버튼
            touchlike = itemView.findViewById(R.id.feed_basic_like);

        }

    }

    public void setFilter(List<Feed_Item> filterdNames) {
        this.mData = filterdNames;
        notifyDataSetChanged();
    }

}

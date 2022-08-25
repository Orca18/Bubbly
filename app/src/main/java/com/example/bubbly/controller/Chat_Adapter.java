package com.example.bubbly.controller;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bubbly.R;
import com.example.bubbly.model.Chat_Item;
import com.example.bubbly.model.UserInfo;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * 채팅 메시지를 보여주는 아답터
 * */
public class Chat_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String cloudFrontAddr = "https://d2gf68dbj51k8e.cloudfront.net/";
    Context mContext;
    List<Chat_Item> chatItemList;
    // 액티비티에서 채팅데이터를 가져올 때마다 변경해준다.
    int pageNo;

    // 페이징 갯수
    int pagingSize = 10;

    public Chat_Adapter(Context mContext, List<Chat_Item> chatItemList) {
        this.mContext = mContext;
        this.chatItemList = chatItemList;
    }

    // 내가 작성한 텍스트 메시지를 보여주는 뷰홀더
    public class MyMsgViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout_my_chatBox;
        TextView textView_my_nickName;
        TextView editText_MyMsg;
        TextView textViewMyTime;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public MyMsgViewHolder(View itemView) {
            super(itemView);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);

            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            editText_MyMsg = itemView.findViewById(R.id.editText_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }

    // 상대방이 작성한 텍스트 메시지를 보여주는 뷰홀더
    public class OpponentMsgViewHolderViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        TextView textView_oppo_nickName;
        TextView editText_OpponentMsg;
        TextView textView_oppo_time;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public OpponentMsgViewHolderViewHolder(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);

            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            editText_OpponentMsg = itemView.findViewById(R.id.editText_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }

    // 내가 작성한 메시지와 날짜를 보여주는 뷰홀더
    public class MyMsgViewHolderWithDay extends RecyclerView.ViewHolder {
        ConstraintLayout layout_my_chatBox;
        TextView textView_date;
        TextView textView_my_nickName;
        TextView editText_MyMsg;
        TextView textViewMyTime;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public MyMsgViewHolderWithDay(View itemView) {
            super(itemView);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            editText_MyMsg = itemView.findViewById(R.id.editText_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }


    // 상대방이 작성한 메시지와 날짜를 보여주는 뷰홀더
    public class OpponentMsgViewHolderViewHolderWithDay extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        TextView textView_date;
        TextView textView_oppo_nickName;
        TextView editText_OpponentMsg;
        TextView textView_oppo_time;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public OpponentMsgViewHolderViewHolderWithDay(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            editText_OpponentMsg = itemView.findViewById(R.id.editText_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }

    /** 이미지 뷰홀더*/
    // 내가 작성한 이미지를 보여주는 뷰홀더
    public class MyMsgViewHolderWithPhoto extends RecyclerView.ViewHolder {
        ConstraintLayout layout_my_chatBox;
        TextView textView_my_nickName;
        ImageView img_myMsg;
        TextView textViewMyTime;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public MyMsgViewHolderWithPhoto(View itemView) {
            super(itemView);
            //layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);
            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            img_myMsg = itemView.findViewById(R.id.img_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }

    // 상대방이 작성한 이미지를 보여주는 뷰홀더
    public class OpponentMsgViewHolderViewHolderWithPhoto extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        TextView textView_oppo_nickName;
        ImageView img_OpponentMsg;
        TextView textView_oppo_time;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public OpponentMsgViewHolderViewHolderWithPhoto(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);
            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            img_OpponentMsg = itemView.findViewById(R.id.img_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }

    // 내가 작성한 이미지와 날짜를 보여주는 뷰홀더
    public class MyMsgViewHolderWithDayAndPhoto extends RecyclerView.ViewHolder {
        ConstraintLayout layout_my_chatBox;
        TextView textView_date;
        TextView textView_my_nickName;
        ImageView img_myMsg;
        TextView textViewMyTime;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public MyMsgViewHolderWithDayAndPhoto(View itemView) {
            super(itemView);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            img_myMsg = itemView.findViewById(R.id.img_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }


    // 상대방이 작성한 이미지와 날짜를 보여주는 뷰홀더
    public class OpponentMsgViewHolderViewHolderWithDayAndPhoto extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        TextView textView_date;
        TextView textView_oppo_nickName;
        ImageView img_OpponentMsg;
        TextView textView_oppo_time;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public OpponentMsgViewHolderViewHolderWithDayAndPhoto(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            img_OpponentMsg = itemView.findViewById(R.id.img_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }

    /** 동영싱 뷰홀더*/
    // 내가 작성한 동영상을 보여주는 뷰홀더
    public class MyMsgViewHolderWithVideo extends RecyclerView.ViewHolder {
        ConstraintLayout layout_my_chatBox;
        TextView textView_my_nickName;
        VideoView video_myMsg;
        TextView textViewMyTime;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public MyMsgViewHolderWithVideo(View itemView) {
            super(itemView);
            //layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);
            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            video_myMsg = itemView.findViewById(R.id.video_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }

    // 상대방이 작성한 동영상을 보여주는 뷰홀더
    public class OpponentMsgViewHolderViewHolderWithVideo extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        TextView textView_oppo_nickName;
        VideoView video_OpponentMsg;
        TextView textView_oppo_time;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public OpponentMsgViewHolderViewHolderWithVideo(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);
            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            video_OpponentMsg = itemView.findViewById(R.id.video_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }

    // 내가 작성한 동영상과 날짜를 보여주는 뷰홀더
    public class MyMsgViewHolderWithDayAndVideo extends RecyclerView.ViewHolder {
        ConstraintLayout layout_my_chatBox;
        TextView textView_date;
        TextView textView_my_nickName;
        VideoView video_myMsg;
        TextView textViewMyTime;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public MyMsgViewHolderWithDayAndVideo(View itemView) {
            super(itemView);
            layout_my_chatBox = itemView.findViewById(R.id.layout_my_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_my_nickName = itemView.findViewById(R.id.textView_my_nickName);
            video_myMsg = itemView.findViewById(R.id.video_MyMsg);
            textViewMyTime = itemView.findViewById(R.id.textViewMyTime);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }


    // 상대방이 작성한 동영상과 날짜를 보여주는 뷰홀더
    public class OpponentMsgViewHolderViewHolderWithDayAndVideo extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout layout_opponent_chatBox;
        TextView textView_date;
        TextView textView_oppo_nickName;
        VideoView video_OpponentMsg;
        TextView textView_oppo_time;
        CircleImageView user_profile;
        TextView not_read_user_count;

        public OpponentMsgViewHolderViewHolderWithDayAndVideo(View itemView) {
            super(itemView);

            layout_opponent_chatBox = itemView.findViewById(R.id.layout_opponent_chatBox);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_oppo_nickName = itemView.findViewById(R.id.textView_oppo_nickName);
            video_OpponentMsg = itemView.findViewById(R.id.video_OpponentMsg);
            textView_oppo_time = itemView.findViewById(R.id.textView_oppo_time);
            user_profile = itemView.findViewById(R.id.user_profile);
            not_read_user_count = itemView.findViewById(R.id.not_read_user_count);
        }
    }

    // 참여자가 입장하고 나갈 때 출력되는 메시지를 보여주는 viewHolder
    public class EnterAndExitViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textView_enter_or_exit;

        public EnterAndExitViewHolder(View itemView) {
            super(itemView);

            textView_enter_or_exit = itemView.findViewById(R.id.textView_enter_or_exit);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 텍스트를 보여주는 뷰홀더
        View itemViewMy = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_me, parent, false);
        View itemViewOppo = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_oppo, parent, false);
        View itemViewMyWithDate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_me_with_date, parent, false);
        View itemViewOppoWithDate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_oppo_with_date, parent, false);
        View itemEnterOrExit = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enter_and_exit, parent, false);

        // 이미지를 보여주는 뷰홀더
        View itemViewMyWithPhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_me_with_photo, parent, false);
        View itemViewOppoWithPhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_oppo_with_photo, parent, false);
        View itemViewMyWithDateAndPhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_me_with_date_and_photo, parent, false);
        View itemViewOppoWithDateAndPhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_oppo_with_date_and_photo, parent, false);

        // 동영상을 보여주는 뷰홀더
        View itemViewMyWithVideo = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_me_with_video, parent, false);
        View itemViewOppoWithVideo = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_oppo_with_video, parent, false);
        View itemViewMyWithDateAndVideo = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_me_with_date_and_video, parent, false);
        View itemViewOppoWithDateAndVideo = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_chatbox_oppo_with_date_and_video, parent, false);

        if(viewType == 0){ // 내가 작성한 채팅
            Chat_Adapter.MyMsgViewHolder vh = new Chat_Adapter.MyMsgViewHolder(itemViewMy);
            return vh;
        } else if(viewType == 1){ // 상대방이 작성한 채팅
            Chat_Adapter.OpponentMsgViewHolderViewHolder vh2 = new Chat_Adapter.OpponentMsgViewHolderViewHolder(itemViewOppo);
            return vh2;
        } else if(viewType == 2){ // 내가 작성했고 날짜 보여줘야 할 때
            Chat_Adapter.MyMsgViewHolderWithDay vh3 = new Chat_Adapter.MyMsgViewHolderWithDay(itemViewMyWithDate);
            return vh3;
        } else if(viewType == 3) { // 상대방이 작성했고 날짜 보여줘야 할 때
            Chat_Adapter.OpponentMsgViewHolderViewHolderWithDay vh4 = new Chat_Adapter.OpponentMsgViewHolderViewHolderWithDay(itemViewOppoWithDate);
            return vh4;
        } else if(viewType == 4){ // 내가 작성한 이미지
            Chat_Adapter.MyMsgViewHolderWithPhoto vh5 = new Chat_Adapter.MyMsgViewHolderWithPhoto(itemViewMyWithPhoto);
            return vh5;
        } else if(viewType == 5){ // 상대방이 작성한 이미지
            Chat_Adapter.OpponentMsgViewHolderViewHolderWithPhoto vh6 = new Chat_Adapter.OpponentMsgViewHolderViewHolderWithPhoto(itemViewOppoWithPhoto);
            return vh6;
        } else if(viewType == 6){ // 내가 작성한 이미지와 날짜 보여줘야 할 때
            Chat_Adapter.MyMsgViewHolderWithDayAndPhoto vh7 = new Chat_Adapter.MyMsgViewHolderWithDayAndPhoto(itemViewMyWithDateAndPhoto);
            return vh7;
        } else if(viewType == 7){ // 상대방이 작성한 이미지와 날짜 보여줘야 할 때
            Chat_Adapter.OpponentMsgViewHolderViewHolderWithDayAndPhoto vh8 = new Chat_Adapter.OpponentMsgViewHolderViewHolderWithDayAndPhoto(itemViewOppoWithDateAndPhoto);
            return vh8;
        } else if(viewType == 8){ // 내가 작성한 동영상
            Chat_Adapter.MyMsgViewHolderWithVideo vh9 = new Chat_Adapter.MyMsgViewHolderWithVideo(itemViewMyWithVideo);
            return vh9;
        } else if(viewType == 9){ // 상대방이 작성한 동영상
            Chat_Adapter.OpponentMsgViewHolderViewHolderWithVideo vh10 = new Chat_Adapter.OpponentMsgViewHolderViewHolderWithVideo(itemViewOppoWithVideo);
            return vh10;
        } else if(viewType == 10){ // 내가 작성한 동영상과 날짜 보여줘야 할 때
            Chat_Adapter.MyMsgViewHolderWithDayAndVideo vh11 = new Chat_Adapter.MyMsgViewHolderWithDayAndVideo(itemViewMyWithDateAndVideo);
            return vh11;
        } else if(viewType == 11) { // 상대방이 작성한 동영상과 날짜 보여줘야 할 때
            Chat_Adapter.OpponentMsgViewHolderViewHolderWithDayAndVideo vh12 = new Chat_Adapter.OpponentMsgViewHolderViewHolderWithDayAndVideo(itemViewOppoWithDateAndVideo);
            return vh12;
        } else { // 들어오거나 나깠을 때
            Chat_Adapter.EnterAndExitViewHolder vh13 = new Chat_Adapter.EnterAndExitViewHolder(itemEnterOrExit);
            return vh13;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat_Item currentItem = this.chatItemList.get(position);

        Log.d("프로필: ",currentItem.getProfileImageURL() != null ? currentItem.getProfileImageURL() : "null");

        // 내가 작성한 텍스트 메시지일 경우
        if(holder instanceof MyMsgViewHolder){
            /** 프로필, 메시지, 시간 세팅 */

            // 프로필 id가 null이라면
            /*if(currentItem.getProfileImageURL().equals("null")) {
                ((MyMsgViewHolder)holder).user_profile.setImageDrawable(mContext.getDrawable(R.drawable.profile));
            } else{
                Glide.with(mContext).load(cloudFrontAddr + currentItem.getProfileImageURL()).into(((MyMsgViewHolder)holder).user_profile);
            }*/

            ((MyMsgViewHolder)holder).editText_MyMsg.setText(currentItem.getChatText());
            ((MyMsgViewHolder)holder).textViewMyTime.setText(currentItem.getChatTime());

            // 리사이클러뷰는 뷰홀더를 재활용하기 때문에 처음에 무조건 GONE을 해주고 데이터가 있는경우에만 VISIBLE을 해줘야 한다.
            // 이렇게 하지 않으면 안읽은사람 = 0인데도 기존 뷰홀더가 사용하던 값을 가지고 올 수 있다.
            ((MyMsgViewHolder)holder).not_read_user_count.setVisibility(View.GONE);

            // 사용자 프로필 보이지 않게!
            ((MyMsgViewHolder)holder).user_profile.setVisibility(View.GONE);

            // 읽지 않은 사용자 설정
            if(currentItem.getNotReadUserCount() <= 0){
                ((MyMsgViewHolder)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((MyMsgViewHolder)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((MyMsgViewHolder)holder).not_read_user_count.setText("" + currentItem.getNotReadUserCount());
            }
        } else if(holder instanceof OpponentMsgViewHolderViewHolder){ //상대방이 작성한 메시지인 경우
            // 프로필파일이 없다면!
            if(currentItem.getProfileImageURL().equals("null")) {
                ((OpponentMsgViewHolderViewHolder)holder).user_profile.setImageDrawable(mContext.getDrawable(R.drawable.profile));
            } else{
                Glide.with(mContext).load(cloudFrontAddr + currentItem.getProfileImageURL()).into(((OpponentMsgViewHolderViewHolder)holder).user_profile);
            }
            
            ((OpponentMsgViewHolderViewHolder)holder).textView_oppo_nickName.setText(currentItem.getChatUserNickName());
            ((OpponentMsgViewHolderViewHolder)holder).editText_OpponentMsg.setText(currentItem.getChatText());
            ((OpponentMsgViewHolderViewHolder)holder).textView_oppo_time.setText(currentItem.getChatTime());

            ((OpponentMsgViewHolderViewHolder)holder).not_read_user_count.setVisibility(View.GONE);

            // 읽지 않은 사용자 설정
            if(currentItem.getNotReadUserCount() <= 0){
                ((OpponentMsgViewHolderViewHolder)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((OpponentMsgViewHolderViewHolder)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((OpponentMsgViewHolderViewHolder)holder).not_read_user_count.setText("" + currentItem.getNotReadUserCount());
            }
        } else if(holder instanceof MyMsgViewHolderWithDay){ //내가 작성했고 날짜를 보여줘야 하는 경우
            ((MyMsgViewHolderWithDay)holder).editText_MyMsg.setText(currentItem.getChatText());
            ((MyMsgViewHolderWithDay)holder).textViewMyTime.setText(currentItem.getChatTime());
            // 날짜! yyyy년 MM월 dd일 (요일)로 보여짐!
            ((MyMsgViewHolderWithDay)holder).textView_date.setText(currentItem.getChatDate());

            ((MyMsgViewHolderWithDay)holder).not_read_user_count.setVisibility(View.GONE);

            // 사용자 프로필 보이지 않게!
            ((MyMsgViewHolderWithDay)holder).user_profile.setVisibility(View.GONE);
            
            // 읽지 않은 사용자 설정
            if(currentItem.getNotReadUserCount() <= 0){
                ((MyMsgViewHolderWithDay)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((MyMsgViewHolderWithDay)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((MyMsgViewHolderWithDay)holder).not_read_user_count.setText("" + currentItem.getNotReadUserCount());
            }
        } else if(holder instanceof OpponentMsgViewHolderViewHolderWithDay){ //상대방이 작성했고 날짜를 보여줘야 하는 경우
            if(currentItem.getProfileImageURL().equals("null")) {
                ((OpponentMsgViewHolderViewHolderWithDay)holder).user_profile.setImageDrawable(mContext.getDrawable(R.drawable.profile));
            } else{
                Glide.with(mContext).load(cloudFrontAddr + currentItem.getProfileImageURL()).into(((OpponentMsgViewHolderViewHolderWithDay)holder).user_profile);
            }
            ((OpponentMsgViewHolderViewHolderWithDay)holder).textView_oppo_nickName.setText(currentItem.getChatUserNickName());
            ((OpponentMsgViewHolderViewHolderWithDay)holder).editText_OpponentMsg.setText(currentItem.getChatText());
            ((OpponentMsgViewHolderViewHolderWithDay)holder).textView_oppo_time.setText(currentItem.getChatTime());
            ((OpponentMsgViewHolderViewHolderWithDay)holder).textView_date.setText(currentItem.getChatDate());

            ((OpponentMsgViewHolderViewHolderWithDay)holder).not_read_user_count.setVisibility(View.GONE);

            // 읽지 않은 사용자 설정
            if(currentItem.getNotReadUserCount() <= 0){
                ((OpponentMsgViewHolderViewHolderWithDay)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((OpponentMsgViewHolderViewHolderWithDay)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((OpponentMsgViewHolderViewHolderWithDay)holder).not_read_user_count.setText("" + currentItem.getNotReadUserCount());
            }
        } if(holder instanceof MyMsgViewHolderWithPhoto){ // 내가 작성한 이미지인 경우
            // 내가 전송한 이미지를 출력하는 경우 비트맵에 있는 데이터를 가져온다.
            Glide.with(mContext).load(currentItem.getChatFileUrl()).into(((MyMsgViewHolderWithPhoto)holder).img_myMsg);

            ((MyMsgViewHolderWithPhoto)holder).textViewMyTime.setText(currentItem.getChatTime());

            // 리사이클러뷰는 뷰홀더를 재활용하기 때문에 처음에 무조건 GONE을 해주고 데이터가 있는경우에만 VISIBLE을 해줘야 한다.
            // 이렇게 하지 않으면 안읽은사람 = 0인데도 기존 뷰홀더가 사용하던 값을 가지고 올 수 있다.
            ((MyMsgViewHolderWithPhoto)holder).not_read_user_count.setVisibility(View.GONE);

            // 사용자 프로필 보이지 않게!
            ((MyMsgViewHolderWithPhoto)holder).user_profile.setVisibility(View.GONE);


            // 읽지 않은 사용자 설정
            if(currentItem.getNotReadUserCount() <= 0){
                ((MyMsgViewHolderWithPhoto)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((MyMsgViewHolderWithPhoto)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((MyMsgViewHolderWithPhoto)holder).not_read_user_count.setText("" + currentItem.getNotReadUserCount());
            }

            /*
            // 클릭 시 다운로드 화면으로 이동
            ((MyMsgViewHolderWithPhoto)holder).img_myMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GlideLoaderActivity.class);

                    intent.putExtra("url", currentItem.getChatFileUrl();
                    mContext.startActivity(intent);
                }
            });
            */
        } else if(holder instanceof OpponentMsgViewHolderViewHolderWithPhoto){ //상대방이 작성한 이미지인 경우
            if(currentItem.getProfileImageURL().equals("null")) {
                ((OpponentMsgViewHolderViewHolderWithPhoto)holder).user_profile.setImageDrawable(mContext.getDrawable(R.drawable.profile));
            } else{
                Glide.with(mContext).load(cloudFrontAddr + currentItem.getProfileImageURL()).into(((OpponentMsgViewHolderViewHolderWithPhoto)holder).user_profile);
            }

            ((OpponentMsgViewHolderViewHolderWithPhoto)holder).textView_oppo_nickName.setText(currentItem.getChatUserNickName());
            Glide.with(mContext).load(currentItem.getChatFileUrl()).into(((OpponentMsgViewHolderViewHolderWithPhoto)holder).img_OpponentMsg);
            ((OpponentMsgViewHolderViewHolderWithPhoto)holder).textView_oppo_time.setText(currentItem.getChatTime());

            ((OpponentMsgViewHolderViewHolderWithPhoto)holder).not_read_user_count.setVisibility(View.GONE);

            // 읽지 않은 사용자 설정
            if(currentItem.getNotReadUserCount() <= 0){
                ((OpponentMsgViewHolderViewHolderWithPhoto)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((OpponentMsgViewHolderViewHolderWithPhoto)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((OpponentMsgViewHolderViewHolderWithPhoto)holder).not_read_user_count.setText("" + currentItem.getNotReadUserCount());
            }

            /*// 클릭 시 다운로드 화면으로 이동
            ((OpponentMsgViewHolderViewHolderWithPhoto)holder).img_OpponentMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GlideLoaderActivity.class);

                    intent.putExtra("url", currentItem.getChatFileUrl());
                    mContext.startActivity(intent);
                }
            });*/
        } else if(holder instanceof MyMsgViewHolderWithDayAndPhoto){ //내가 작성한 이미지이고 날짜를 보여줘야 하는 경우
            Glide.with(mContext).load(currentItem.getChatFileUrl()).into(((MyMsgViewHolderWithDayAndPhoto)holder).img_myMsg);

            ((MyMsgViewHolderWithDayAndPhoto)holder).textViewMyTime.setText(currentItem.getChatTime());
            ((MyMsgViewHolderWithDayAndPhoto)holder).textView_date.setText(currentItem.getChatDate());

            ((MyMsgViewHolderWithDayAndPhoto)holder).not_read_user_count.setVisibility(View.GONE);

            // 사용자 프로필 보이지 않게!
            ((MyMsgViewHolderWithDayAndPhoto)holder).user_profile.setVisibility(View.GONE);

            // 읽지 않은 사용자 설정
            if(currentItem.getNotReadUserCount() <= 0){
                ((MyMsgViewHolderWithDayAndPhoto)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((MyMsgViewHolderWithDayAndPhoto)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((MyMsgViewHolderWithDayAndPhoto)holder).not_read_user_count.setText("" + currentItem.getNotReadUserCount());
            }

            /*// 클릭 시 다운로드 화면으로 이동
            ((OpponentMsgViewHolderViewHolderWithPhoto)holder).img_OpponentMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GlideLoaderActivity.class);

                    intent.putExtra("url", currentItem.getChatFileUrl());
                    mContext.startActivity(intent);
                }
            });*/
        } else if(holder instanceof OpponentMsgViewHolderViewHolderWithDayAndPhoto){ //상대방이 작성한 이미지이고 날짜를 보여줘야 하는 경우
            if(currentItem.getProfileImageURL().equals("null")) {
                ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).user_profile.setImageDrawable(mContext.getDrawable(R.drawable.profile));
            } else{
                Glide.with(mContext).load(cloudFrontAddr + currentItem.getProfileImageURL()).into(((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).user_profile);
            }
            ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).textView_oppo_nickName.setText(currentItem.getChatUserNickName());
            Glide.with(mContext).load(currentItem.getChatFileUrl()).into(((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).img_OpponentMsg);
            ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).textView_oppo_time.setText(currentItem.getChatTime());
            ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).textView_date.setText(currentItem.getChatDate());

            ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).not_read_user_count.setVisibility(View.GONE);

            // 읽지 않은 사용자 설정
            if(currentItem.getNotReadUserCount() <= 0){
                ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((OpponentMsgViewHolderViewHolderWithDayAndPhoto)holder).not_read_user_count.setText("" + currentItem.getNotReadUserCount());
            }

            /*// 클릭 시 다운로드 화면으로 이동
            ((OpponentMsgViewHolderViewHolderWithPhoto)holder).img_OpponentMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GlideLoaderActivity.class);

                    intent.putExtra("url", currentItem.getChatFileUrl());
                    mContext.startActivity(intent);
                }
            });*/
        } if(holder instanceof MyMsgViewHolderWithVideo){ // 내가 작성한 동영상인 경우
            // VideoView에 동여상 uri 세팅
            ((MyMsgViewHolderWithVideo)holder).video_myMsg.setMediaController(new MediaController(mContext));

            Uri videoUri= Uri.parse(currentItem.getChatFileUrl());

            //VideoView가 보여줄 동영상의 경로 주소(Uri) 설정하기
            ((MyMsgViewHolderWithVideo)holder).video_myMsg.setVideoURI(videoUri);

            // 사용자 프로필 보이지 않게!
            ((MyMsgViewHolderWithVideo)holder).user_profile.setVisibility(View.GONE);

            //동영상을 읽어오는데 시간이 걸리므로..
            ((MyMsgViewHolderWithVideo)holder).video_myMsg.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    //비디오 시작
                    ((MyMsgViewHolderWithVideo)holder).video_myMsg.start();
                }
            });
            
            // 이렇게 하지 않으면 안읽은사람 = 0인데도 기존 뷰홀더가 사용하던 값을 가지고 올 수 있다.
            ((MyMsgViewHolderWithVideo)holder).not_read_user_count.setVisibility(View.GONE);

            // 읽지 않은 사용자 설정
            if(currentItem.getNotReadUserCount() <= 0){
                ((MyMsgViewHolderWithVideo)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((MyMsgViewHolderWithVideo)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((MyMsgViewHolderWithVideo)holder).not_read_user_count.setText("" + currentItem.getNotReadUserCount());
            }

            /*
            // 클릭 시 재생 및 화면으로 이동
            ((MyMsgViewHolderWithVideo)holder).video_myMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GlideLoaderActivity.class);

                    intent.putExtra("url", currentItem.getChatFileUrl());
                    mContext.startActivity(intent);
                }
            });
            */
        } else if(holder instanceof OpponentMsgViewHolderViewHolderWithVideo){ //상대방이 작성한 동영상인 경우
            if(currentItem.getProfileImageURL().equals("null")) {
                ((OpponentMsgViewHolderViewHolderWithVideo)holder).user_profile.setImageDrawable(mContext.getDrawable(R.drawable.profile));
            } else{
                Glide.with(mContext).load(cloudFrontAddr + currentItem.getProfileImageURL()).into(((OpponentMsgViewHolderViewHolderWithVideo)holder).user_profile);
            }

            // VideoView에 동여상 uri 세팅
            ((OpponentMsgViewHolderViewHolderWithVideo)holder).video_OpponentMsg.setMediaController(new MediaController(mContext));

            Uri videoUri= Uri.parse(currentItem.getChatFileUrl());

            //VideoView가 보여줄 동영상의 경로 주소(Uri) 설정하기
            ((OpponentMsgViewHolderViewHolderWithVideo)holder).video_OpponentMsg.setVideoURI(videoUri);

            //동영상을 읽어오는데 시간이 걸리므로..
            ((OpponentMsgViewHolderViewHolderWithVideo)holder).video_OpponentMsg.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    //비디오 시작
                    ((OpponentMsgViewHolderViewHolderWithVideo)holder).video_OpponentMsg.start();
                }
            });

            // 이렇게 하지 않으면 안읽은사람 = 0인데도 기존 뷰홀더가 사용하던 값을 가지고 올 수 있다.
            ((OpponentMsgViewHolderViewHolderWithVideo)holder).not_read_user_count.setVisibility(View.GONE);

            // 읽지 않은 사용자 설정
            if(currentItem.getNotReadUserCount() <= 0){
                ((OpponentMsgViewHolderViewHolderWithVideo)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((OpponentMsgViewHolderViewHolderWithVideo)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((OpponentMsgViewHolderViewHolderWithVideo)holder).not_read_user_count.setText("" + currentItem.getNotReadUserCount());
            }

            /*
            // 클릭 시 재생 및 화면으로 이동
            ((OpponentMsgViewHolderViewHolderWithVideo)holder).video_OpponentMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GlideLoaderActivity.class);

                    intent.putExtra("url", currentItem.getChatFileUrl());
                    mContext.startActivity(intent);
                }
            });
            */
        } else if(holder instanceof MyMsgViewHolderWithDayAndVideo){ //내가 작성한 동영상이고 날짜를 보여줘야 하는 경우
            // VideoView에 동여상 uri 세팅
            ((MyMsgViewHolderWithDayAndVideo)holder).video_myMsg.setMediaController(new MediaController(mContext));
            ((MyMsgViewHolderWithDayAndVideo)holder).textView_date.setText(currentItem.getChatDate());

            Uri videoUri= Uri.parse(currentItem.getChatFileUrl());

            //VideoView가 보여줄 동영상의 경로 주소(Uri) 설정하기
            ((MyMsgViewHolderWithDayAndVideo)holder).video_myMsg.setVideoURI(videoUri);

            //동영상을 읽어오는데 시간이 걸리므로..
            ((MyMsgViewHolderWithDayAndVideo)holder).video_myMsg.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    //비디오 시작
                    ((MyMsgViewHolderWithDayAndVideo)holder).video_myMsg.start();
                }
            });

            // 이렇게 하지 않으면 안읽은사람 = 0인데도 기존 뷰홀더가 사용하던 값을 가지고 올 수 있다.
            ((MyMsgViewHolderWithDayAndVideo)holder).not_read_user_count.setVisibility(View.GONE);

            // 사용자 프로필 보이지 않게!
            ((MyMsgViewHolderWithDayAndVideo)holder).user_profile.setVisibility(View.GONE);

            // 읽지 않은 사용자 설정
            if(currentItem.getNotReadUserCount() <= 0){
                ((MyMsgViewHolderWithDayAndVideo)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((MyMsgViewHolderWithDayAndVideo)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((MyMsgViewHolderWithDayAndVideo)holder).not_read_user_count.setText("" + currentItem.getNotReadUserCount());
            }

            /*
            // 클릭 시 재생 및 화면으로 이동
            ((MyMsgViewHolderWithDayAndVideo)holder).video_myMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GlideLoaderActivity.class);

                    intent.putExtra("url", currentItem.getChatFileUrl());
                    mContext.startActivity(intent);
                }
            });
            */
        } else if(holder instanceof OpponentMsgViewHolderViewHolderWithDayAndVideo){ //상대방이 작성한 동영상이고 날짜를 보여줘야 하는 경우
            if(currentItem.getProfileImageURL().equals("null")) {
                ((OpponentMsgViewHolderViewHolderWithDayAndVideo)holder).user_profile.setImageDrawable(mContext.getDrawable(R.drawable.profile));
            } else{
                Glide.with(mContext).load(cloudFrontAddr + currentItem.getProfileImageURL()).into(((OpponentMsgViewHolderViewHolderWithDayAndVideo)holder).user_profile);
            }

            // VideoView에 동여상 uri 세팅
            ((OpponentMsgViewHolderViewHolderWithDayAndVideo)holder).video_OpponentMsg.setMediaController(new MediaController(mContext));

            Uri videoUri= Uri.parse(currentItem.getChatFileUrl());

            //VideoView가 보여줄 동영상의 경로 주소(Uri) 설정하기
            ((OpponentMsgViewHolderViewHolderWithDayAndVideo)holder).video_OpponentMsg.setVideoURI(videoUri);

            //동영상을 읽어오는데 시간이 걸리므로..
            ((OpponentMsgViewHolderViewHolderWithDayAndVideo)holder).video_OpponentMsg.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    //비디오 시작
                    ((OpponentMsgViewHolderViewHolderWithDayAndVideo)holder).video_OpponentMsg.start();
                }
            });

            // 이렇게 하지 않으면 안읽은사람 = 0인데도 기존 뷰홀더가 사용하던 값을 가지고 올 수 있다.
            ((OpponentMsgViewHolderViewHolderWithDayAndVideo)holder).not_read_user_count.setVisibility(View.GONE);

            // 읽지 않은 사용자 설정
            if(currentItem.getNotReadUserCount() <= 0){
                ((OpponentMsgViewHolderViewHolderWithDayAndVideo)holder).not_read_user_count.setVisibility(View.GONE);
            } else {
                ((OpponentMsgViewHolderViewHolderWithDayAndVideo)holder).not_read_user_count.setVisibility(View.VISIBLE);

                ((OpponentMsgViewHolderViewHolderWithDayAndVideo)holder).not_read_user_count.setText("" + currentItem.getNotReadUserCount());
            }

            /*
            // 클릭 시 재생 및 화면으로 이동
            ((OpponentMsgViewHolderViewHolderWithDayAndVideo)holder).video_OpponentMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GlideLoaderActivity.class);

                    intent.putExtra("url", currentItem.getChatFileUrl());
                    mContext.startActivity(intent);
                }
            });
            */
        } else if(holder instanceof EnterAndExitViewHolder){ //나가거나 들어온 경우
            ((EnterAndExitViewHolder)holder).textView_enter_or_exit.setText(currentItem.getChatText());
        }
    }

    // 각 채팅메시지가 어떤 뷰홀더에 매칭되어야 하는지 체크!!
    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        boolean isMyChat = chatItemList.get(position).getChatUserId() != null? chatItemList.get(position).getChatUserId().equals(UserInfo.user_id) : false;
        boolean isDateShowed = false;

        // 이미지 메시지
        boolean isPhotoMsg = (chatItemList.get(position).getChatType() == 1);

        // 동영상 메시지
        boolean isVideoMsg = (chatItemList.get(position).getChatType() == 2);

        Log.d("position: ", "" + position);

        isDateShowed = false;

        // 채팅 메시지의 날짜 비교
        // 최상단 메시지가 아니라면
        if(position != chatItemList.size() - 1){
            if(!chatItemList.get(position).getChatDate().equals(chatItemList.get(position + 1).getChatDate())) {
                isDateShowed = true;
            }
        } else { //최상단의 날짜는 무조건 보이게하기기
           isDateShowed = true;
        }

        // 들어오거나 나가는 메시지인경우!
        if(chatItemList.get(position).getChatText().contains("입장했습니다.") || chatItemList.get(position).getChatText().contains("나갔습니다.")){
            viewType = 12;
        } else if(isPhotoMsg && isMyChat && isDateShowed){ // 내가 작성한 이미지이고 날짜를 보여줘야 할 때
            viewType = 6;
        } else if(isPhotoMsg && !isMyChat && isDateShowed){ // 상대방이 작성한 이미지이고 날짜를 보여줘야 할 때
            viewType = 7;
        } else if(isVideoMsg && isMyChat && isDateShowed){ // 내가 작성한 동영상이고 날짜를 보여줘야 할 때
            viewType = 10;
        } else if(isVideoMsg && !isMyChat && isDateShowed){ // 상대방이 작성한 동영상이고 날짜를 보여줘야 할 때
            viewType = 11;
        } else if(isPhotoMsg && isMyChat){ // 내가 작성한 이미지이고 날짜를 보여줄 필요가 없을 때
            viewType = 4;
        } else if(isPhotoMsg){ // 상대방이 작성한 이미지이고 날짜를 보여줄 필요가 없을 때
            viewType = 5;
        } else if(isVideoMsg && isMyChat){ // 내가 작성한 동영상이고 날짜를 보여줄 필요가 없을 때
            viewType = 8;
        } else if(isVideoMsg){ // 상대방이 작성한 동영상이고 날짜를 보여줄 필요가 없을 때
            viewType = 9;
        } else if(isMyChat && isDateShowed){ // 내가 작성했고 날짜를 보여줘야 할 때
            viewType = 2;
        } else if(!isMyChat && isDateShowed){ // 상대방이 작성했고 날짜를 보여줘야 할 때
            viewType = 3;
        } else if(isMyChat){ // 내가 작성했고 날짜를 보여줄 필요가 없을 때
            viewType = 0;
        } else { // 상대방이 작성했고 날짜를 보여줄 필요가 없을 때
            viewType = 1;
        }

        return viewType;
    }

    @Override
    public int getItemCount() {
        return chatItemList.size();
    }

    // 메시지 전송 시 리사이클러뷰에 추가
    public int addChatMsgInfo(Chat_Item chatItem){
        chatItemList.add(0, chatItem);
        //notifyItemInserted(chatItemList.size());
        //int position = getItemCount();
        notifyItemInserted(0);
        Log.d("Chat_Adapter/addChatMsgInfo - 받은 메시지 리사이클러뷰에 뿌려줌", chatItem.getChatText());
        return 0;
    }

    public int getPageNo() {
        return pageNo;
    }

    // 안읽은 사용자수를 업데이트 한다!
    public void updateNotReadUserCount(int lastIdx){
        int position = 0;

        // 역순으로 저장하므로 뒤에서부터 체크한다!!
        for(int i = chatItemList.size() - 1; i >= 0; i--){
            if(chatItemList.get(i).getChatId() == lastIdx){
                position = i;
                break;
            }
        }

        for(int j = 0; j < position; j ++) {
            chatItemList.get(j).setNotReadUserCount(chatItemList.get(j).getNotReadUserCount() - 1);
        }

        notifyDataSetChanged();
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
}

package com.mainnet.bubbly.controller;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mainnet.bubbly.R;
import com.mainnet.bubbly.config.Config;
import com.mainnet.bubbly.model.OtherUserInfo;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * 채팅방 만들기 액티비티에서 검색한 사용자를 표시할 리사이클러뷰
 * */
public class Chat_Searched_User_Adapter extends RecyclerView.Adapter<Chat_Searched_User_Adapter.ChatSearchedUserViewHolder> {
    Context mContext;
    // 사용자 정보를 저장하는 리스트
    List<OtherUserInfo> mUserInfoList;
    ChatSearchedUserClickListener listener;

    public Chat_Searched_User_Adapter(Context mContext, List<OtherUserInfo> mUserInfoList, ChatSearchedUserClickListener listener) {
        this.mContext = mContext;
        this.mUserInfoList = mUserInfoList;
        this.listener = listener;
    }

    /**
    * 검색된 사용자 아이템정보를 보여줄 뷰홀더
    * */
    public class ChatSearchedUserViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        CircleImageView userProfile;
        TextView userNickName;
        TextView userLoginId;
        ImageView btSelectedCheck;

        public ChatSearchedUserViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.ll_item_layout);
            userProfile = itemView.findViewById(R.id.chat_searched_user_profile);
            userNickName = itemView.findViewById(R.id.chat_searched_user_nick_name);
            userLoginId = itemView.findViewById(R.id.chat_searched_user_login_id);
            // 선택됐는지 여부 => 선택됐다면 보여진다.
            btSelectedCheck = itemView.findViewById(R.id.chat_searched_user_check);
        }
    }

    /** 채팅방 만들기 액티비티와 연결된 리스너*/
    public interface ChatSearchedUserClickListener{
        public void onItemClicked(OtherUserInfo userInfo, boolean isClicked);
    }

    @NonNull
    @Override
    public ChatSearchedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        v = LayoutInflater.from(mContext).inflate(R.layout.item_chat_searched_user, parent, false);
        ChatSearchedUserViewHolder vHolder = new ChatSearchedUserViewHolder(v);

        return vHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ChatSearchedUserViewHolder holder, int position) {
        // 리스트에 있는 사용자 정보 세팅
        OtherUserInfo currentItem = this.mUserInfoList.get(position);

        Log.d("사용자 프로필: ", Config.cloudfront_addr + currentItem.getProfile_file_name());

        // 사용자 프로필 세팅
        if(currentItem.getProfile_file_name() != null){
            Glide.with(mContext)
                    .load(Config.cloudfront_addr + currentItem.getProfile_file_name())
                    .into(holder.userProfile);
        } else {
            holder.userProfile.setImageDrawable(mContext.getDrawable(R.drawable.blank_profile));
        }


        // 해당 사용자의 닉네임 세팅
        holder.userNickName.setText(currentItem.getUser_nick());

        // 해당 사용자의 로그인 아이디 세팅
        holder.userLoginId.setText(currentItem.getLogin_id());


        // 새로운 항목을 확인할 때 이전에 조회했던 항목이라면 체크여부를 설정해준다
        if(currentItem.isChecked()){
            // 체크 해제
            holder.btSelectedCheck.setVisibility(View.VISIBLE);
            Log.d("검색결과 체크확인", "체크 되어 있었음");
        } else { // 해당 버튼이 눌러져있지 않았다면 체크
            Log.d("검색결과 체크확인", "체크 안되어 있었음");
            // 체크!
            holder.btSelectedCheck.setVisibility(View.GONE);
        }


        /** 레이아웃 클릭 시
         * 1. 체크상태 변경
         * 2. 클릭 시 체크가 됐다면 액티비티로 사용자 정보 전달
         * 3. 클릭 시 체크가 해제됐다면 액티비티의 사용자 리스트에서 사용자제거 && 상단 선택된 리사이클러뷰에서 사용자 제거
         * */
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 해당 버튼이 체크되어있던 상태라면
                if(currentItem.isChecked()){
                    // 체크 해제
                    holder.btSelectedCheck.setVisibility(View.GONE);
                    Log.d("검색결과 체크확인", "체크 되어 있었음");
                    currentItem.setChecked(false);
                } else { // 해당 버튼이 눌러져있지 않았다면 체크
                    Log.d("검색결과 체크확인", "체크 안되어 있었음");
                    // 체크!
                    holder.btSelectedCheck.setVisibility(View.VISIBLE);
                    currentItem.setChecked(true);
                }

                // 리스너에게 체크여부와 사용자정보 전달
                listener.onItemClicked(currentItem, currentItem.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserInfoList.size();
    }

    // 모든 사용자 삭제
    public void clearAllUser(){
        int size = mUserInfoList.size();
        mUserInfoList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void dataSetChanged(ArrayList<OtherUserInfo> newList, ArrayList<OtherUserInfo> clickedList){
        // 상단리스트에 값이 있다면!
        if(clickedList.size() != 0){
            for(int i = 0; i < clickedList.size(); i++) {
                for(int j = 0; j < newList.size(); j++){
                    if(clickedList.get(i).getUser_id().equals(newList.get(j).getUser_id())) {
                        newList.get(j).setChecked(true);
                        break;
                    }
                }
            }
        }

        mUserInfoList = newList;
        notifyDataSetChanged();
    }

    // 상단 리사이클러뷰에서 삭제된 항목의 체크표시 해제
    public void checkRemove(OtherUserInfo removedUserInfo){
        for(int i = 0; i < mUserInfoList.size(); i++) {
            if(mUserInfoList.get(i).getUser_id().equals(removedUserInfo.getUser_id())){
                mUserInfoList.get(i).setChecked(false);
                notifyItemChanged(i);
                break;
            }
        }
    }
}

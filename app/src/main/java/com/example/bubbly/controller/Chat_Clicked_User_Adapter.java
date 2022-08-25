package com.example.bubbly.controller;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bubbly.R;
import com.example.bubbly.model.Chat_Item;
import com.example.bubbly.model.OtherUserInfo;
import com.example.bubbly.model.UserInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * 채팅방 만들기 액티비티에서 체크한 사용자를 표시할 리사이클러뷰
 * */
public class Chat_Clicked_User_Adapter extends RecyclerView.Adapter<Chat_Clicked_User_Adapter.ChatClickedUserViewHolder> {
    Context mContext;
    // 사용자 정보를 저장하는 리스트
    ArrayList<OtherUserInfo> mUserInfoList;
    ChatClickedUserDeleteListener listener;

    public Chat_Clicked_User_Adapter(Context mContext, ArrayList<OtherUserInfo> mUserInfoList, ChatClickedUserDeleteListener listener) {
        this.mContext = mContext;
        this.mUserInfoList = mUserInfoList;
        this.listener = listener;
    }

    /**
    * 클릭된 사용자 아이템정보를 보여줄 뷰홀더
    * */
    public class ChatClickedUserViewHolder extends RecyclerView.ViewHolder {
        TextView chatClickedUserNickName;
        ImageButton btChatClickedUserDel;

        public ChatClickedUserViewHolder(@NonNull View itemView) {
            super(itemView);
            chatClickedUserNickName = itemView.findViewById(R.id.item_chat_clicked_user_name);
            btChatClickedUserDel = itemView.findViewById(R.id.bt_chat_clicked_user_del);
        }
    }

    /** 채팅방 만들기 액티비티와 연결된 리스너*/
    public interface ChatClickedUserDeleteListener{
         void onItemDeleted(OtherUserInfo userInfo);
    }

    @NonNull
    @Override
    public ChatClickedUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        v = LayoutInflater.from(mContext).inflate(R.layout.item_chat_clicked_user, parent, false);
        ChatClickedUserViewHolder vHolder = new ChatClickedUserViewHolder(v);

        return vHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ChatClickedUserViewHolder holder, int position) {
        // 리스트에 있는 사용자 정보 세팅
        OtherUserInfo currentItem = this.mUserInfoList.get(position);
        // 해당 사용자의 닉네임 세팅
        holder.chatClickedUserNickName.setText(currentItem.getUser_nick());

        // X표시 클릭 시 리사이클러뷰에서 삭제제
        holder.btChatClickedUserDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClickedUser(currentItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserInfoList.size();
    }

    // 사용자 추가
    public void addClickedUser(OtherUserInfo userInfo){
        mUserInfoList.add(userInfo);
        notifyItemInserted(getItemCount());
    }


    // 사용자 삭제
    public void deleteClickedUser(OtherUserInfo userInfo){
        for(int i = 0; i < mUserInfoList.size(); i++){
            if(mUserInfoList.get(i).getUser_id().equals(userInfo.getUser_id())){
                listener.onItemDeleted(mUserInfoList.get(i));

                mUserInfoList.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, mUserInfoList.size());

                break;
            }
        }
    }

    // 리스트 반환
    public ArrayList<OtherUserInfo> getList(){
        return mUserInfoList;
    }
}

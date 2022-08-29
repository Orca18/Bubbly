package com.example.bubbly.controller;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bubbly.ChattingRoom;
import com.example.bubbly.MM_Message;
import com.example.bubbly.R;
import com.example.bubbly.chatting.service.ChatService;
import com.example.bubbly.chatting.util.ChatUtil;
import com.example.bubbly.model.Chat_Item;
import com.example.bubbly.model.Chat_Member_FCM_Sub;
import com.example.bubbly.model.Chat_Room_Info;
import com.example.bubbly.model.Chat_Room_Info;
import com.example.bubbly.model.OtherUserInfo;
import com.example.bubbly.model.UserInfo;
import com.example.bubbly.retrofit.ApiClient;
import com.example.bubbly.retrofit.ApiInterface;
import com.example.bubbly.retrofit.ChatApiClient;
import com.example.bubbly.retrofit.ChatApiInterface;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 채팅방 리스트를 보여주는 Adapter
* */
public class Messages_Adapter extends RecyclerView.Adapter<Messages_Adapter.MessagesViewHolder> {

    Context mContext;
    List<Chat_Room_Info> mData;
    String user_id;
    int position;

    public Messages_Adapter(Context mContext, List<Chat_Room_Info> mData) {
        this.mContext = mContext;
        this.mData = mData;

        // 로그인한 user_id값
        user_id = UserInfo.user_id;
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
        String chatRoomName;
        String chatRoomProfile;

        // 채팅방 정보
        Chat_Room_Info currentItem = this.mData.get(position);

        // 마지막으로 읽은 메시지id
        int lastReadMsgId = getLastMsgIdx(currentItem.getChatRoomId());

        if(lastReadMsgId == 99999999){
            // 아직 기존 채팅방에 들어간적이 없어서 읽은 메시지가 없다면
            lastReadMsgId = 0;
        }
        int notReadMsgCount = currentItem.getLatestMsgId() - lastReadMsgId;

        // 해당 사용자가 이 채팅방의 생성자인지 여부
        boolean isChatRoomCreator = user_id.equals(currentItem.getChatCreatorId());

        // 이 사용자가 채팅방 생성자라면
        if(isChatRoomCreator){
            chatRoomName = currentItem.getChatRoomNameCreator();
            chatRoomProfile = currentItem.getProfileFileNameCreator();
        } else { // 이 사용자가 채팅방 생성자가 아니라면
            chatRoomName = currentItem.getChatRoomNameOther();
            chatRoomProfile = currentItem.getProfileFileNameOther();
        }

        // 채팅방 프로필
        if(chatRoomProfile != null){
            Glide.with(holder.itemView.getContext())
                    .load("https://d2gf68dbj51k8e.cloudfront.net/" + chatRoomProfile)
                    .centerCrop()
                    .into(holder.chatprofile);
        }

        // 채팅방명
        holder.name.setText(chatRoomName);

        // 채팅방 마지막 메시지
        holder.content.setText(currentItem.getLatestMsg());

        // 마지막 메시지 수신 시간
        holder.time.setText(currentItem.getLatestMsgTime());

        // 읽지 않은 메시지수
        if(notReadMsgCount > 0){
            holder.notReadMsgCount.setVisibility(View.VISIBLE);
            holder.notReadMsgCount.setText("" + notReadMsgCount);
        } else {
            holder.notReadMsgCount.setVisibility(View.GONE);
        }

        holder.item_message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(position);
                Log.d("채팅방 롱클릭 시 position 저장", "" + position);
                return false;
            }
        });

        //액티비티 전환
        holder.item_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 채팅멤버리스트 정보 가져오기 
                ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
                Call<ArrayList<OtherUserInfo>> call = apiClient.selectChatParticipantUsingChatRoomId(currentItem.getChatRoomId());
                call.enqueue(new Callback<ArrayList<OtherUserInfo>>()
                {
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<OtherUserInfo>> call, @NonNull Response<ArrayList<OtherUserInfo>> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            // 채팅방 아이디
                            ArrayList<OtherUserInfo> chatMemberList = response.body();

                            //TODO 인텐트 만들어주기
                            Intent intent = new Intent(mContext, ChattingRoom.class);

                            // 데이터 넣어주기
                            // 채팅방 아이디
                            intent.putExtra("chatRoomId", currentItem.getChatRoomId());

                            // 채팅방 명
                            intent.putExtra("chatRoomName",chatRoomName);

                            // 새로 생성된 채팅방
                            intent.putExtra("isNew",false);

                            // 메시지 전송여부 - 기존에 생성된 채팅방이기 떄문에 메시지 전송은 한번이상 이뤄짐 따라서 true!
                            intent.putExtra("isMsgTransfered",true);

                            // 채팅멤버 리스트
                            intent.putExtra("chatMemberList", chatMemberList);

                            // 안읽은 메시지 0으로 변경하기
                            holder.notReadMsgCount.setText("0");
                            holder.notReadMsgCount.setVisibility(View.GONE);

                            //어답터에서 클릭 이용할 때, 아래 해줘야됨!
                            mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            
                        } else {
                            Log.e("채팅방 정보 조회 성공 했지만 데이터 없음: ", "1111");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArrayList<OtherUserInfo>> call, @NonNull Throwable t)
                    {
                        Log.e("채팅방  조회 저장 실패", t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    
        LinearLayout item_message;
        CircleImageView chatprofile;
        TextView name, content, time, notReadMsgCount;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            item_message = itemView.findViewById(R.id.item_message);
            chatprofile = itemView.findViewById(R.id.item_message_profile);
            name = itemView.findViewById(R.id.item_message_name);
            content = itemView.findViewById(R.id.item_message_content);
            time = itemView.findViewById(R.id.item_message_time);
            notReadMsgCount = itemView.findViewById(R.id.not_read_msg_count);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.action_chat_room_delete, 1, "나가기");
        }
    }

    public void setFilter(List<Chat_Room_Info> filterdNames) {
        this.mData = filterdNames;
        notifyDataSetChanged();
    }

    public void updateChatRoomInfo(Chat_Item chatItem){
        String chatRoomId = chatItem.getChatRoomId();

        for(int i = 0; i < mData.size(); i++){
            Chat_Room_Info chatRoomInfo = mData.get(i);
            if(chatRoomInfo.getChatRoomId().equals(chatRoomId)){
                chatRoomInfo.setLatestMsg(chatItem.getChatText());
                chatRoomInfo.setLatestMsgId(chatItem.getChatId());
                // 브로커로부터 받은 메시지의 시간은 yyyy년 mm월 dd일 (요일) 오전/오후 hh:mm의 형태로 보여짐
                // db에서 조회할 땐 yyyy.MM.dd hh:mm의 형태로 될 것임 => 둘을 맞춰줘야 함!!
                chatRoomInfo.setLatestMsg(chatItem.getChatDate() + " " + chatItem.getChatTime());
                break;
            }
        }
    }

    public void setPosition(int position){
        this.position = position;
    }

    public int getPosition(){
        return position;
    }

    public Chat_Room_Info getItem(int position){
        return mData.get(position);
    }

    /** 특정 채팅방의 마지막 메시지 idx를 가져와라*/
    public int getLastMsgIdx(String chatRoomId){
        SharedPreferences sp = mContext.getSharedPreferences("chat", Activity.MODE_PRIVATE);
        int idx = sp.getInt(chatRoomId,99999999);

        return idx;
    }
}

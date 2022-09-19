package com.mainnet.bubbly.retrofit.chat;


import android.util.Log;

import com.mainnet.bubbly.model.Chat_Room_Info;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
* 채팅방 정보를 조회한다.
* */
public class SyncGetChatInfo extends Thread{
    private Call<ArrayList<Chat_Room_Info>> call;
    private Chat_Room_Info chatRoomInfo;

    public SyncGetChatInfo(Call<ArrayList<Chat_Room_Info>> call){
        this.call = call;
    }

    @Override
    public void run() {
        try {
            Response response = call.execute();

            /** 채팅 리스트를 가져왔다면*/
            if(response.isSuccessful()){
                chatRoomInfo = (Chat_Room_Info)((ArrayList)response.body()).get(0);
            } else {
                Log.d("MQTT Client ID: ", "실패");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Chat_Room_Info getChatRoomInfo() {
        return chatRoomInfo;
    }
}


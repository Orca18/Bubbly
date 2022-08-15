package com.example.bubbly.retrofit.chat;


import android.util.Log;

import com.example.bubbly.model.Chat_Item_From_Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
* 채팅서버에서 메시지 리스트를 조호한다.
* */
public class SyncGetChatList extends Thread{
    private Call<ArrayList<Chat_Item_From_Server>> call;
    private ArrayList<Chat_Item_From_Server> chatList;

    public SyncGetChatList(Call<ArrayList<Chat_Item_From_Server>> call){
        this.call = call;
    }

    @Override
    public void run() {
        try {
            Response response = call.execute();

            /** 채팅 리스트를 가져왔다면*/
            if(response.isSuccessful()){
                chatList = (ArrayList)response.body();
            } else {
                Log.d("MQTT Client ID: ", "실패");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Chat_Item_From_Server> getChatList() {
        return chatList;
    }
}


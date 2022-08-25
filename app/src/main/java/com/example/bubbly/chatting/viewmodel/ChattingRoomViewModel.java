package com.example.bubbly.chatting.viewmodel;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bubbly.model.Chat_Room_Info;

import java.util.ArrayList;

/** 채팅정보를 저장할  viewModel
 * */
public class ChattingRoomViewModel extends ViewModel {
    // 채팅방리스트에서 사용할 채팅방 정보
    private MutableLiveData<ArrayList<Chat_Room_Info>> chatRoomList;

    // 초기화
    public ChattingRoomViewModel() {
        chatRoomList = new MutableLiveData<>();
    }

    // 채팅방 리스트 반환
    public MutableLiveData<ArrayList<Chat_Room_Info>> getChatRoomList() {
        return chatRoomList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        // 액티비티가 완전히 종료된경우(앱이 종료) isActRunning값을 false로 변경한다.
        //ChattingNotificationService.isActRunning = false;
    }
}

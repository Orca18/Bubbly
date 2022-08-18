package com.example.bubbly.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 채팅방 생성 시 서버로 전달할 모델
 * */
public class Chat_Room_Cre implements Serializable {
    private String chatRoomId;
    private String chatRoomNameCreator;
    private String chatRoomNameOther;
    private String chatCreatorId;
    private String chatOtherId;
    private ArrayList<String> chatRoomMemberList;

    public String getChatRoomNameCreator() {
        return chatRoomNameCreator;
    }

    public void setChatRoomNameCreator(String chatRoomNameCreator) {
        this.chatRoomNameCreator = chatRoomNameCreator;
    }

    public String getChatRoomNameOther() {
        return chatRoomNameOther;
    }

    public void setChatRoomNameOther(String chatRoomNameOther) {
        this.chatRoomNameOther = chatRoomNameOther;
    }

    public String getChatCreatorId() {
        return chatCreatorId;
    }

    public void setChatCreatorId(String chatCreatorId) {
        this.chatCreatorId = chatCreatorId;
    }

    public String getChatOtherId() {
        return chatOtherId;
    }

    public void setChatOtherId(String chatOtherId) {
        this.chatOtherId = chatOtherId;
    }

    public ArrayList<String> getChatRoomMemberList() {
        return chatRoomMemberList;
    }

    public void setChatRoomMemberList(ArrayList<String> chatRoomMemberList) {
        this.chatRoomMemberList = chatRoomMemberList;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    @Override
    public String toString() {
        return "Chat_Room_Cre{" +
                "chatRoomId='" + chatRoomId + '\'' +
                ", chatRoomNameCreator='" + chatRoomNameCreator + '\'' +
                ", chatRoomNameOther='" + chatRoomNameOther + '\'' +
                ", chatCreatorId='" + chatCreatorId + '\'' +
                ", chatOtherId='" + chatOtherId + '\'' +
                ", chatRoomMemberList=" + chatRoomMemberList +
                '}';
    }
}

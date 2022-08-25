package com.example.bubbly.model;

import java.io.Serializable;

public class Chat_Item implements Serializable {
    private String profileImageURL;
    private String chatRoomId;
    private String chatUserId;
    private String chatText;
    private String chatFileUrl;
    private String chatTime;

    public Chat_Item(String imageUrl) {
        this.profileImageURL = imageUrl;
    }

    // 서버와 채팅 메시지를 통신 하기위한 생성자
    public Chat_Item(String chatRoomId, String chatUserId, String chatText, String chatFileUrl, String chatTime) {
        this.chatRoomId = chatRoomId;
        this.chatUserId = chatUserId;
        this.chatText = chatText;
        this.chatFileUrl = chatFileUrl;
        this.chatTime = chatTime;
    }

    public String chatTEST() {
        return profileImageURL;
    }


    public String getChatRoomId() {
        return chatRoomId;
    }

    public String getChatUserId() {
        return chatUserId;
    }

    public String getChatText() {
        return chatText;
    }

    public String getChatFileUrl() {
        return chatFileUrl;
    }

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime;
    }
}



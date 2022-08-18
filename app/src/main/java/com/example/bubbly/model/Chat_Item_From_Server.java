package com.example.bubbly.model;

import java.io.Serializable;

// 서버로부터 받은 채팅 메시지를 저장하는 클래스
public class Chat_Item_From_Server implements Serializable {
    // 채팅 아이디
    private String _id;
    // 채팅의 시퀀스(채팅방별로 다름)
    private int seq;
    // 채팅방 아이디
    private String chatRoomId;
    // Chat_Item의 Byte[]를 문자열화한 데이터 => String to Byte[] to Chat_Item으로 변환해서 사용해야함
    private String chatItemStr;

    public Chat_Item_From_Server(String _id, int seq, String chatRoomId, String chatItemStr){
        this._id = _id;
        this.seq = seq;
        this.chatRoomId = chatRoomId;
        this.chatItemStr = chatItemStr;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getChatItemStr() {
        return chatItemStr;
    }

    public void setChatItemStr(String chatItemStr) {
        this.chatItemStr = chatItemStr;
    }
}

package com.example.bubbly.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

/**
 * 채팅방리스트에서 사용할 정보를 담는 클래스
 * */
public class Chat_Room_Info implements Serializable {
    @SerializedName("chat_room_id")
    private String chatRoomId;
    @SerializedName("chat_creator_id")
    private String chatCreatorId;
    @SerializedName("chat_room_name_creator")
    private String chatRoomNameCreator;
    @SerializedName("chat_room_name_other")
    private String chatRoomNameOther;
    @SerializedName("latest_msg")
    private String latestMsg;
    @SerializedName("latest_msg_time")
    private String latestMsgTime;
    @SerializedName("latest_msg_id")
    private int latestMsgId;
    @SerializedName("profile_file_name_other")
    private String profileFileNameOther;
    @SerializedName("profile_file_name_creator")
    private String profileFileNameCreator;
    @SerializedName("member_count")
    private int member_count;
    private int notReadMsgCount;

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getChatCreatorId() {
        return chatCreatorId;
    }

    public void setChatCreatorId(String chatCreatorId) {
        this.chatCreatorId = chatCreatorId;
    }

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

    public String getLatestMsg() {
        return latestMsg;
    }

    public void setLatestMsg(String latestMsg) {
        this.latestMsg = latestMsg;
    }

    public String getLatestMsgTime() {
        return latestMsgTime;
    }

    public void setLatestMsgTime(String latestMsgTime) {
        this.latestMsgTime = latestMsgTime;
    }

    public int getLatestMsgId() {
        return latestMsgId;
    }

    public void setLatestMsgId(int latestMsgId) {
        this.latestMsgId = latestMsgId;
    }

    public String getProfileFileNameOther() {
        return profileFileNameOther;
    }

    public void setProfileFileNameOther(String profileFileNameOther) {
        this.profileFileNameOther = profileFileNameOther;
    }

    public String getProfileFileNameCreator() {
        return profileFileNameCreator;
    }

    public void setProfileFileNameCreator(String profileFileNameCreator) {
        this.profileFileNameCreator = profileFileNameCreator;
    }

    public int getMember_count() {
        return member_count;
    }

    public void setMember_count(int member_count) {
        this.member_count = member_count;
    }


    public int getNotReadMsgCount() {
        return notReadMsgCount;
    }

    public void setNotReadMsgCount(int notReadMsgCount) {
        this.notReadMsgCount = notReadMsgCount;
    }
}

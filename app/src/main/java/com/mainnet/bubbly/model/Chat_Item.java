package com.mainnet.bubbly.model;

import java.io.Serializable;

public class Chat_Item implements Serializable {
    // 몽고db에서 사용하는 채팅 메시지 아이디
    private String _id;
    private String profileImageURL;
    private String chatRoomId;
    private String chatUserId;
    private String chatText;
    private String chatFileUrl;
    private String chatDate;
    private String chatTime;
    private String chatUserNickName;
    private int chatId;
    private int chatType;
    private int notReadUserCount;

    public Chat_Item(String imageUrl) {
        this.profileImageURL = imageUrl;
    }

    // 서버와 채팅 메시지를 통신 하기위한 생성자
    public Chat_Item(String chatRoomId, String chatUserId, String chatText, String chatFileUrl, String chatDate, String chatTime, String chatUserNickName, int chatType) {
        this.chatRoomId = chatRoomId;
        this.chatUserId = chatUserId;
        this.chatText = chatText;
        this.chatFileUrl = chatFileUrl;
        this.chatDate = chatDate;
        this.chatTime = chatTime;
        this.chatUserNickName = chatUserNickName;
        this.chatType = chatType;
    }

    public Chat_Item(String profileImageURL, String chatRoomId, String chatUserId, String chatText, String chatFileUrl, String chatDate, String chatTime, String chatUserNickName, int chatId, int chatType, int notReadUserCount) {
        this.profileImageURL = profileImageURL;
        this.chatRoomId = chatRoomId;
        this.chatUserId = chatUserId;
        this.chatText = chatText;
        this.chatFileUrl = chatFileUrl;
        this.chatDate = chatDate;
        this.chatTime = chatTime;
        this.chatUserNickName = chatUserNickName;
        this.chatId = chatId;
        this.chatType = chatType;
        this.notReadUserCount = notReadUserCount;
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

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public void setChatUserId(String chatUserId) {
        this.chatUserId = chatUserId;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText;
    }

    public void setChatFileUrl(String chatFileUrl) {
        this.chatFileUrl = chatFileUrl;
    }

    public String getChatDate() {
        return chatDate;
    }

    public void setChatDate(String chatDate) {
        this.chatDate = chatDate;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getChatUserNickName() {
        return chatUserNickName;
    }

    public void setChatUserNickName(String chatUserNickName) {
        this.chatUserNickName = chatUserNickName;
    }

    public int getNotReadUserCount() {
        return notReadUserCount;
    }

    public void setNotReadUserCount(int notReadUserCount) {
        this.notReadUserCount = notReadUserCount;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "Chat_Item{" +
                "profileImageURL='" + profileImageURL + '\'' +
                ", chatRoomId='" + chatRoomId + '\'' +
                ", chatUserId='" + chatUserId + '\'' +
                ", chatText='" + chatText + '\'' +
                ", chatFileUrl='" + chatFileUrl + '\'' +
                ", chatDate='" + chatDate + '\'' +
                ", chatTime='" + chatTime + '\'' +
                ", chatUserNickName='" + chatUserNickName + '\'' +
                ", chatId=" + chatId +
                ", chatType=" + chatType +
                '}';
    }
}



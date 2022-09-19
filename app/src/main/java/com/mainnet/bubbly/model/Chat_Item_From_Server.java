package com.mainnet.bubbly.model;

import java.io.Serializable;

// 서버로부터 받은 채팅 메시지를 저장하는 클래스
public class Chat_Item_From_Server implements Serializable {
    // 몽고db 저장 아이디
    private String _id;
    // 채팅 정보
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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(String chatUserId) {
        this.chatUserId = chatUserId;
    }

    public String getChatText() {
        return chatText;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText;
    }

    public String getChatFileUrl() {
        return chatFileUrl;
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

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime;
    }

    public String getChatUserNickName() {
        return chatUserNickName;
    }

    public void setChatUserNickName(String chatUserNickName) {
        this.chatUserNickName = chatUserNickName;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public int getNotReadUserCount() {
        return notReadUserCount;
    }

    public void setNotReadUserCount(int notReadUserCount) {
        this.notReadUserCount = notReadUserCount;
    }
}

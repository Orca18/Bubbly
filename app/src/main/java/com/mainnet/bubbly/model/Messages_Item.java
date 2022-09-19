package com.mainnet.bubbly.model;

public class Messages_Item {
    // 프사, 이름, 내용
    private String chatProfile;
    private String chatName;
    private String chatContent;
    private String chattime;

    public Messages_Item(String chatProfile, String chatName, String chatContent, String chattime) {
        this.chatProfile = chatProfile;
        this.chatName = chatName;
        this.chatContent = chatContent;
        this.chattime = chattime;
    }

    public String getChatProfile() {
        return chatProfile;
    }

    public String getChatName() {
        return chatName;
    }

    public String getChatContent() {
        return chatContent;
    }

    public String getChattime() {
        return chattime;
    }
}



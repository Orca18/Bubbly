package com.example.bubbly.model;

import java.util.ArrayList;

public class Chat_Member_FCM_Sub {
    private String topic;
    private ArrayList<String> tokenList;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public ArrayList<String> getTokenList() {
        return tokenList;
    }

    public void setTokenList(ArrayList<String> tokenList) {
        this.tokenList = tokenList;
    }
}

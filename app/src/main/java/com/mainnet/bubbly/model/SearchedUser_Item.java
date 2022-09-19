package com.mainnet.bubbly.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchedUser_Item {

    @Expose
    @SerializedName("user_id") private String user_id;

    @Expose
    @SerializedName("nick_name") private String nick_name;

    @Expose
    @SerializedName("profile_file_name") private String profile_file_name;

    @Expose
    @SerializedName("login_id") private String login_id;

    @Expose
    @SerializedName("self_intro") private String self_intro;


    public SearchedUser_Item(String follower_id, String nick_name, String profile_file_name, String login_id, String self_intro) {
        this.user_id = follower_id;
        this.nick_name = nick_name;
        this.profile_file_name = profile_file_name;
        this.login_id = login_id;
        this.self_intro = self_intro;
    }

    public SearchedUser_Item(String follower_id, String nick_name, String profile_file_name) {
        this.user_id = follower_id;
        this.nick_name = nick_name;
        this.profile_file_name = profile_file_name;
    }



    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getProfile_file_name() {
        return profile_file_name;
    }

    public void setProfile_file_name(String profile_file_name) {
        this.profile_file_name = profile_file_name;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getSelf_intro() {
        return self_intro;
    }

    public void setSelf_intro(String self_intro) {
        this.self_intro = self_intro;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

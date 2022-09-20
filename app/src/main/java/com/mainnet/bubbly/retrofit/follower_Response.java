package com.mainnet.bubbly.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class follower_Response {

    @Expose
    @SerializedName("follower_id") private String follower_id;

    @Expose
    @SerializedName("nick_name") private String nick_name;

    @Expose
    @SerializedName("profile_file_name") private String profile_file_name;

    @Expose
    @SerializedName("login_id") private String login_id;


    public follower_Response(String follower_id, String nick_name, String profile_file_name, String login_id) {
        this.follower_id = follower_id;
        this.nick_name = nick_name;
        this.profile_file_name = profile_file_name;
        this.login_id = login_id;
    }


    public String getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(String follower_id) {
        this.follower_id = follower_id;
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
}

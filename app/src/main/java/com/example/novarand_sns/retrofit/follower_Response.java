package com.example.novarand_sns.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class follower_Response {

    @Expose
    @SerializedName("follower_id") private String follower_id;

    @Expose
    @SerializedName("nick_name") private String nick_name;

    @Expose
    @SerializedName("profile_file_name") private String profile_file_name;

    public follower_Response(String follower_id, String nick_name, String profile_file_name) {
        this.follower_id = follower_id;
        this.nick_name = nick_name;
        this.profile_file_name = profile_file_name;
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
}

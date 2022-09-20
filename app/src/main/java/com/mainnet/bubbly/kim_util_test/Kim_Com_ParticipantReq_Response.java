package com.mainnet.bubbly.kim_util_test;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Kim_Com_ParticipantReq_Response {

    @Expose
    @SerializedName("community_id") private String community_id;

    @Expose
    @SerializedName("user_id") private String user_id;

    @Expose
    @SerializedName("nick_name") private String nick_name;

    @Expose
    @SerializedName("profile_file_name") private String profile_file_name;

    public Kim_Com_ParticipantReq_Response(String community_id, String user_id, String nick_name, String profile_file_name) {
        this.community_id = community_id;
        this.user_id = user_id;
        this.nick_name = nick_name;
        this.profile_file_name = profile_file_name;
    }

    public String getCommunity_id() {
        return community_id;
    }

    public void setCommunity_id(String community_id) {
        this.community_id = community_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

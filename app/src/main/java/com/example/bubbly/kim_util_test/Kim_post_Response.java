package com.example.bubbly.kim_util_test;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Kim_post_Response {

    @Expose
    @SerializedName("community_id") private String community_id;

    @Expose
    @SerializedName("community_owner_id") private String community_owner_id;

    @Expose
    @SerializedName("community_name") private String community_name;

    @Expose
    @SerializedName("community_desc") private String community_desc;

    @Expose
    @SerializedName("profile_file_name") private String profile_file_name;

    public Kim_post_Response(String community_id, String community_owner_id, String community_name, String community_desc, String profile_file_name) {
        this.community_id = community_id;
        this.community_owner_id = community_owner_id;
        this.community_name = community_name;
        this.community_desc = community_desc;
        this.profile_file_name = profile_file_name;
    }

    public String getCommunity_id() {
        return community_id;
    }

    public void setCommunity_id(String community_id) {
        this.community_id = community_id;
    }

    public String getCommunity_owner_id() {
        return community_owner_id;
    }

    public void setCommunity_owner_id(String community_owner_id) {
        this.community_owner_id = community_owner_id;
    }

    public String getCommunity_name() {
        return community_name;
    }

    public void setCommunity_name(String community_name) {
        this.community_name = community_name;
    }

    public String getCommunity_desc() {
        return community_desc;
    }

    public void setCommunity_desc(String community_desc) {
        this.community_desc = community_desc;
    }

    public String getProfile_file_name() {
        return profile_file_name;
    }

    public void setProfile_file_name(String profile_file_name) {
        this.profile_file_name = profile_file_name;
    }
}

package com.example.bubbly.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class user_Response {

    @Expose
    @SerializedName("user_id") private String user_id;

    @Expose
    @SerializedName("login_id") private String login_id;

    @Expose
    @SerializedName("email_addr") private String email_addr;

    @Expose
    @SerializedName("phone_num") private String phone_num;

    @Expose
    @SerializedName("novaland_account_addr") private String novaland_account_addr;

    @Expose
    @SerializedName("profile_file_name") private String profile_file_name;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getEmail_addr() {
        return email_addr;
    }

    public void setEmail_addr(String email_addr) {
        this.email_addr = email_addr;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getNovaland_account_addr() {
        return novaland_account_addr;
    }

    public void setNovaland_account_addr(String novaland_account_addr) {
        this.novaland_account_addr = novaland_account_addr;
    }

    public String getProfile_file_name() {
        return profile_file_name;
    }

    public void setProfile_file_name(String profile_file_name) {
        this.profile_file_name = profile_file_name;
    }
}

package com.mainnet.bubbly.model;

import java.io.Serializable;

public class OtherUserInfo implements Serializable {
    private String user_id = "";
    private String login_id = "";
    private String email_addr = "";
    private String phone_num = "";
    private String novaland_account_addr = "";
    private String profile_file_name = "";
    private String nick_name = "";
    private String self_info = "";
    private String token;
    private boolean isChecked = false;

    public OtherUserInfo(){}

    public OtherUserInfo(String user_id, String login_id, String email_addr, String phone_num, String novaland_account_addr, String profile_file_name, String nick_name, String self_info, String token) {
        this.user_id = user_id;
        this.login_id = login_id;
        this.email_addr = email_addr;
        this.phone_num = phone_num;
        this.novaland_account_addr = novaland_account_addr;
        this.profile_file_name = profile_file_name;
        this.nick_name = nick_name;
        this.self_info = self_info;
        this.token = token;
    }

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

    public String getUser_nick() {
        return nick_name;
    }

    public void setUser_nick(String user_nick) {
        this.nick_name = user_nick;
    }

    public String getSelf_info() {
        return self_info;
    }

    public void setSelf_info(String self_info) {
        this.self_info = self_info;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

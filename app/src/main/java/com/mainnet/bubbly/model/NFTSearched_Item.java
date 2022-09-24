package com.mainnet.bubbly.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NFTSearched_Item {

    @Expose
    @SerializedName("profile_file_name") private String profileImageURL;

    @Expose
    @SerializedName("nick_name") private String userName;

    @Expose
    @SerializedName("login_id") private String loginId;

    @Expose
    @SerializedName("user_id") private String userId;

    @Expose
    @SerializedName("nft_id") private String nft_id;

    @Expose
    @SerializedName("holder_id") private String holder_id;

    @Expose
    @SerializedName("nft_creation_time") private String creation_time;

    @Expose
    @SerializedName("is_sell") private String isSell;

    @Expose
    @SerializedName("seller_id") private String seller_id;

    @Expose
    @SerializedName("sell_price") private String sell_price;

    @Expose
    @SerializedName("app_id") private String app_id;

    @Expose
    @SerializedName("nft_name") private String nft_name;

    @Expose
    @SerializedName("nft_desc") private String nft_des;

    @Expose
    @SerializedName("file_save_url") private String file_save_url;

    @Expose
    @SerializedName("novaland_account_addr") private String novaland_account_addr;

    public NFTSearched_Item(String profileImageURL, String userName, String loginId,
                            String userId, String nft_id, String holder_id,
                            String creation_time, String isSell, String seller_id,
                            String sell_price, String app_id, String nft_des,
                            String nft_name, String file_save_url, String novaland_account_addr){
        this.profileImageURL = profileImageURL;
        this.userName = userName;
        this.loginId = loginId;
        this.userId = userId;
        this.nft_id = nft_id;
        this.holder_id = holder_id;
        this.creation_time =creation_time;
        this.isSell = isSell;
        this.nft_des = nft_des;
        this.nft_name = nft_name;
        this.seller_id = seller_id;
        this.sell_price = sell_price;
        this.app_id = app_id;
        this.file_save_url = file_save_url;
        this.novaland_account_addr = novaland_account_addr;
    }

    public String getNft_id() {
        return nft_id;
    }

    public void setNft_id(String nft_id) {
        this.nft_id = nft_id;
    }

    public String getNft_name() {
        return nft_name;
    }

    public void setNft_name(String nft_name) {
        this.nft_name = nft_name;
    }

    public String getNft_des() {
        return nft_des;
    }

    public void setNft_des(String nft_des) {
        this.nft_des = nft_des;
    }

    public String getFile_save_url() {
        return file_save_url;
    }

    public void setFile_save_url(String file_save_url) {
        this.file_save_url = file_save_url;
    }


    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getSell_price() {
        return sell_price;
    }

    public void setSell_price(String sell_price) {
        this.sell_price = sell_price;
    }

    public String getNovaland_account_addr() {
        return novaland_account_addr;
    }

    public void setNovaland_account_addr(String novaland_account_addr) {
        this.novaland_account_addr = novaland_account_addr;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHolder_id() {
        return holder_id;
    }

    public void setHolder_id(String holder_id) {
        this.holder_id = holder_id;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public String getIsSell() {
        return isSell;
    }

    public void setIsSell(String isSell) {
        this.isSell = isSell;
    }
}

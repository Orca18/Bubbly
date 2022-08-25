package com.example.bubbly.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NFT_Item {

    @Expose
    @SerializedName("nft_id") private String nft_id;

    @Expose
    @SerializedName("holder_id") private String holder_id;

    @Expose
    @SerializedName("nft_name") private String nft_name;

    @Expose
    @SerializedName("nft_des") private String nft_des;

    @Expose
    @SerializedName("file_save_url") private String file_save_url;

    private boolean isAlreadySell;
    private String seller_id;
    private String sell_price;
    private String app_id;

    public NFT_Item(String nft_id, String holder_id, String nft_des, String nft_name, String file_save_url){
        this.nft_id = nft_id;
        this.nft_des = nft_des;
        this.nft_name = nft_name;
        this.holder_id = holder_id;
        this.file_save_url = file_save_url;
    }

    public String getNft_id() {
        return nft_id;
    }

    public void setNft_id(String nft_id) {
        this.nft_id = nft_id;
    }

    public String getHolder_id() {
        return holder_id;
    }

    public void setHolder_id(String holder_id) {
        this.holder_id = holder_id;
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

    public boolean isAlreadySell() {
        return isAlreadySell;
    }

    public void setAlreadySell(boolean alreadySell) {
        isAlreadySell = alreadySell;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getSell_price() {
        return sell_price;
    }

    public void setSell_price(String seller_price) {
        this.sell_price = seller_price;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }
}

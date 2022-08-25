package com.example.bubbly.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionHistory_Item {

    private String fee;
    private String amount;
    private String txRoundTimeToDate;
    private String id;
    private String type;
    private String sender;
    private String receiver;
    private String assetId;


    public TransactionHistory_Item(String type, String id, String sender, String txRoundTimeToDate, String fee, String amount, String receiver, String assetId){
    this.type = type;
    this.id = id;
    this.sender = sender;
    this.txRoundTimeToDate = txRoundTimeToDate;
    this.fee = fee;
    this.amount = amount;
    this.receiver = receiver;
    this.assetId = assetId;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTxRoundTimeToDate() {
        return txRoundTimeToDate;
    }

    public void setTxRoundTimeToDate(String txRoundTimeToDate) {
        this.txRoundTimeToDate = txRoundTimeToDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
}

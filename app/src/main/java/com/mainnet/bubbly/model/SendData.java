package com.mainnet.bubbly.model;

// 지갑 관련
public class SendData {
    private String address;
    private long amount;
    private String senderadd;

    public SendData(String address, long amount, String senderadd) {
        this.address = address;
        this.amount = amount;
        this.senderadd = senderadd;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getSenderadd() {
        return senderadd;
    }

    public void setSenderadd(String senderadd) {
        this.senderadd = senderadd;
    }
}

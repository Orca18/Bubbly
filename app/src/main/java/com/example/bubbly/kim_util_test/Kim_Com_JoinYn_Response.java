package com.example.bubbly.kim_util_test;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Kim_Com_JoinYn_Response {

    @Expose
    @SerializedName("join_yn") private String join_yn;

    public Kim_Com_JoinYn_Response(String join_yn) {
        this.join_yn = join_yn;
    }

    public String getJoin_yn() {
        return join_yn;
    }

    public void setJoin_yn(String join_yn) {
        this.join_yn = join_yn;
    }
}

package com.mainnet.bubbly.retrofit;


import android.content.Context;

import com.mainnet.bubbly.model.AccessAndRefreshToken;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
* 채팅방 정보를 조회한다.
* */
public class SyncReIssueAccessToken extends Thread{
    private Call<String> call;
    private Context context;
    private int statusCode;

    public SyncReIssueAccessToken(Call<String> call, Context context){
        this.call = call;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            Response response = call.execute();

            this.statusCode = response.code();

            // 액세스 토큰 갱신
            if(response.body()!=null){
                AccessAndRefreshToken.accessToken = (String)response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getStatusCode() {
        return statusCode;
    }
}


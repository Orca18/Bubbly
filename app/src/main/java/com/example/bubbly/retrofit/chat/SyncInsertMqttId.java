package com.example.bubbly.retrofit.chat;

import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class SyncInsertMqttId extends Thread{
    private Call<String>call;

    public SyncInsertMqttId(Call<String> call){
        this.call = call;
    }

    @Override
    public void run() {
        try {
            Response response = call.execute();

            Log.e("MQTT id 저장 여부: ",  (String)response.body());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


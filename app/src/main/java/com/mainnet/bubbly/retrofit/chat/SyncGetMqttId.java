package com.mainnet.bubbly.retrofit.chat;


import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class SyncGetMqttId extends Thread{
    private Call<String> call;
    private String mqttClientId ;

    public SyncGetMqttId(Call<String> call){
        this.call = call;
    }

    @Override
    public void run() {
        try {
            Log.d("MQTT Client ID: ", "안들어오나?");

            Response response = call.execute();
            Log.d("MQTT Client ID 들어오는데? ", response.body().toString());

            /** Mqtt client아이디를 가져왔다면*/
            if(response.isSuccessful()){
                mqttClientId = response.body().toString();
                Log.d("MQTT Client ID: ", mqttClientId);
            } else {
                Log.d("MQTT Client ID: ", "실패");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMqttClientId() {
        Log.d("MQTT Client ID2222: ", mqttClientId);

        return mqttClientId;
    }
}


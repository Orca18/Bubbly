package com.example.bubbly.retrofit;

import com.example.bubbly.config.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class ChatApiClient {
    private static Retrofit retrofit;

    public static Retrofit getApiClient()
    {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Config.chat_server_addr)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }
}

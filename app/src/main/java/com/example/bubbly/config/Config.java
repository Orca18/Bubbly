package com.example.bubbly.config;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Config {
    private Context context;
    public static String api_server_addr = null;
    public static String chat_server_addr = null;
    public static String aws_access_key = null;
    public static String aws_secret_key = null;
    public static String cloudfront_addr = null;
    public static String mqtt_server = null;
    public static String s3_bucket_name = null;

    public Config(Context context) throws IOException, JSONException {
        this.context = context;
    }

    public void getConfigData() throws IOException, JSONException {
        AssetManager assetManager= context.getAssets();

        // 테스트 서버 접속 시
        //InputStream is= assetManager.open("config.json");

        // 실서버 접속 시
        InputStream is= assetManager.open("config_prod.json");

        InputStreamReader isr= new InputStreamReader(is);
        BufferedReader reader= new BufferedReader(isr);

        StringBuffer buffer= new StringBuffer();
        String line= reader.readLine();

        while (line!=null){
            buffer.append(line+"\n");
            line = reader.readLine();
        }

        String jsonData= buffer.toString();

        JSONObject jsonObj= new JSONObject(jsonData);
        api_server_addr = jsonObj.getString("api_server_address");
        chat_server_addr = jsonObj.getString("chat_server_address");
        aws_access_key = jsonObj.getString("aws_access_key");
        aws_secret_key = jsonObj.getString("aws_secret_key");
        cloudfront_addr = jsonObj.getString("cloudfront_addr");
        mqtt_server = jsonObj.getString("mqtt_server");
        s3_bucket_name = jsonObj.getString("s3_bucket_name");
    }
}

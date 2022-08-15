package com.example.bubbly.retrofit;

import com.example.bubbly.model.Chat_Item_From_Server;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ChatApiInterface {

    // 채팅데이터 조회
    @GET("/selectChatData")
    Call<ArrayList<Chat_Item_From_Server>> selectChatData(@Query("chat_room_id") String chat_room_id, @Query("page_no") int page_no);

    // 채팅서버에게 FCM 토큰 전송
    @FormUrlEncoded
    @POST("/sendTokenToServer") //
    Call<String> sendTokenToServer(@Field("token") String token, @Field("user_id") String user_id);

    // 로그인 시 FCM 토큰 정보 갱싱
    @FormUrlEncoded
    @POST("/refreshToken") //
    Call<String> refreshToken(@Field("token") String token, @Field("user_id") String user_id);

    // mqtt_client 조회
    @GET("/selectMqttClientUsingUserId")
    Call<String> selectMqttClientUsingUserId(@Query("user_id") String user_id);

    // mqtt_client 아이디 저장
    @FormUrlEncoded
    @POST("/insertMqttClientId") //
    Call<String> insertMqttClientId(@Field("mqtt_client") String mqtt_client, @Field("user_id") String user_id);
}

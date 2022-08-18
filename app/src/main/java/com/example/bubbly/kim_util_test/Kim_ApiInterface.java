package com.example.bubbly.kim_util_test;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Kim_ApiInterface {

    // 1. 커뮤니티 생성
    @Multipart
    @POST("community/createCommunity")
    Call<String> createCommunity(
            @Part("community_owner_id") String community_owner_id, // 방장? 아이디
            @Part("writer_name") String writer_name, // 방장 닉네임?
            @Part("community_name") String community_name, // 커뮤니티 이름
            @Part("community_desc") String community_desc, // 커뮤니티 설명
            @Part("profile_file") String profile,
            @Part MultipartBody.Part file
    );


    // 2. 사용자가 가입한 커뮤니티 리스트 조회
    @GET("community/selectCommunityListUsingUserId")
    Call<List<Kim_JoinedCom_Response>> selectCommunityListUsingUserId(
            @Query("user_id") String user_id // 로그인한 user_id 값
    );

    // 3. 커뮤니티 참여자 정보 저장 TODO 커뮤 생성 시, 커뮤 ID 다시 받아와서 만들어야되려나???
    @FormUrlEncoded
    @POST("community/createCommunityParicipant")
    Call<String> createCommunityParicipant(
            @Part("user_id") String user_id,
            @Part("community_id") String community_id
            );
}

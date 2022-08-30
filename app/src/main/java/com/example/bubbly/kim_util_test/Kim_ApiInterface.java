package com.example.bubbly.kim_util_test;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    // 3. 커뮤니티 참여자 정보 저장
    @FormUrlEncoded
    @POST("community/participate/createCommunityParticipant")
    Call<String> createCommunityParticipant(
            @Field("user_id") String user_id,
            @Field("community_id") String community_id
    );

    // 3. 커뮤니티 아이디로 커뮤니티 정보 조회
    @GET("community/selectCommunityUsingCommunityId")
    Call<List<Kim_Com_Info_Response>> selectCommunityUsingCommunityId(
            @Query("community_id") String community_id
    );


    // 4. 멤버 목록 불러오기
    @GET("community/selectCommunityParticipantList")
    Call<List<Kim_Com_Members_Response>> selectCommunityParticipantList(
            @Query("community_id") String community_id
    );


    // 5. 공지사항 가져오기


    // 6. 커뮤니티에서 파생(?)된 NFT 목록


    // 7. 커뮤니티 정보 수정
    @Multipart
    @POST("community/updateCommunity")
    Call<String> updateCommunity(
            @Part("community_name") String community_name,
            @Part("community_desc") String community_desc,
            @Part("profile_file_name") String profile_file_name,
            @Part("community_id") String community_id,
            @Part MultipartBody.Part file,
            @Part("rule") String rule

    );

    // 8. 특정 커뮤니티의 게시글 가져오기
    @GET("post/selectCommunityPost")
    Call<List<Kim_Com_post_Response>> selectCommunityPost(
            @Query("user_id") String user_id,
            @Query("community_id") String community_id
    );


    // 9. 내가 속한 모든 커뮤니티의 글
    @GET("post/selectAllCommunityPost")
    Call<List<Kim_Com_post_Response>> selectAllCommunityPost(
            @Query("user_id") String user_id
    );


    // 9. 커뮤니티 폐쇄
    @FormUrlEncoded
    @POST("community/deleteCommunity")
    Call<String> deleteCommunity(
            @Part("community_id") String community_id
    );

    // 10 ~ : 게시글 작성 관련 수정 & 검색?


    // 커뮤니티 가입 신청
    @FormUrlEncoded
    @POST("community/participate/createCommunityParticipationReq")
    Call<String> createCommunityParticipationReq(
            @Field("community_participation_req_id") String community_participation_req_id, // 이게 유저 아이디
            @Field("community_id") String community_id
    );

    // 커뮤니티 가입 요청자 리스트 조회
    @GET("community/participate/selectCommunityParticipantReqList")
    Call<List<Kim_Com_ParticipantReq_Response>> selectCommunityParticipantReqList(
            @Query("community_id") String community_id
    );

    // 커뮤니티 가입 승인
    @FormUrlEncoded
    @POST("community/participate/approveCommunityParticipation")
    Call<String> approveCommunityParticipation(
            @Field("community_participation_req_id") String user_id,
            @Field("community_id") String community_id
    );

    // 커뮤니티 가입 거절
    @FormUrlEncoded
    @POST("community/participate/rejectCommunityParticipation")
    Call<String> rejectCommunityParticipation(
            @Field("user_id") String user_id,
            @Field("community_id") String community_id
    );


    // 커뮤니티 나가기
    @FormUrlEncoded
    @POST("community/participate/deleteCommunityParticipant")
    Call<String> deleteCommunityParticipant(
            @Field("user_id") String user_id,
            @Field("community_id") String community_id
    );

    // 커뮤니티 가입 여부
    @GET("community/selectCommunityJoinYn")
    Call<List<Kim_Com_JoinYn_Response>> selectCommunityJoinYn(
            @Query("community_id") String community_id
    );

}

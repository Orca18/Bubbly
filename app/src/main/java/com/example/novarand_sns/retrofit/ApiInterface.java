package com.example.novarand_sns.retrofit;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    // todo 회원정보와 관련된 api (시작 지점)
    // 이메일 인증번호 발송 api
    @FormUrlEncoded
    @POST("userinfo/sendEmailCertificationNum")
    Call<String> sendEmailCertificationNum(@Field("email_addr") String email_addr);

    // 아이디 인증번호 검증 api
    @GET("userinfo/verfyEmailCertificationNum")
    Call<String> verfyEmailCertificationNum(@Query("email_addr") String email_addr,
                                            @Query("certification_num") String certification_num);

    // 휴대폰 인증번호 발송 api
    @FormUrlEncoded
    @POST("userinfo/sendPhoneCertificationNum")
    Call<String> sendPhoneCertificationNum(@Field("phone_num") String phone_num);

    // 휴대폰 인증번호 검증 api
    @GET("userinfo/verfyPhoneCertificationNum")
    Call<String> verfyPhoneCertificationNum(@Query("phone_num") String phone_num,
                                            @Query("certification_num") String certification_num);

    // 아이디 중복 조회 api
    @GET("userinfo/selectIsExisingId")
    Call<String> selectIsExisingId(@Query("login_id") String login_id);

    // 회원정보 저장 api
    @FormUrlEncoded
    @POST("userinfo/createUserInfo")
    Call<String> createUserInfo(@Field("login_id") String login_id,
                                @Field("login_pw") String login_pw,
                                @Field("email_addr") String email_addr,
                                @Field("phone_num") String phone_num,
                                @Field("nick_name") String nick_name);

    // 회원정보 수정 api
    @Multipart
    @POST("userinfo/updateUserInfo")
    Call<String> updateUserInfo(@Part("login_id") String login_id,
                                @Part("email_addr") String email_addr,
                                @Part("phone_num") String phone_num,
                                @Part("nick_name") String nick_name,
//                                @Field("profile_file_name") String profile_file_name,
                                @Part("profile_file_name") RequestBody profile_file_name,
                                @Part List<MultipartBody.Part> files,
                                @Part("user_id") String user_id);

    // 회원정보 조회 api
    @GET("userinfo/selectUserInfo")
    Call<List<user_Response>> selectUserInfo(@Query("user_id") String user_id);

    // todo 회원정보와 관련된 api (끝 지점)



    // todo 로그인과 관련된 api (시작 지점)

    // 로그인 api
    @FormUrlEncoded
    @POST("login/login")
    Call<String> login(@Field("login_id") String login_id,
                       @Field("password") String password);

    // todo 로그인과 관련된 api (끝 지점)



    // todo 게시물과 관련된 api (시작 지점)
    @Multipart
    @POST("post/createPost") // 게시글 생성 api
    Call<String> createPost(@Part("post_writer_id") String post_writer_id,
                             @Part("post_contents") String post_contents,
                            @Part("fileContents[]") RequestBody fileContents,
                            @Part List<MultipartBody.Part> files,
//                            @Part("fileContents[]") String fileContents,
                             @Part("share_post_yn") String share_post_yn,
                             @Part("community_id") String community_id,
                             @Part("mentioned_user_id_list[]") String mentioned_user_id_list);
    @Multipart
    @POST("post/updatePost") // 게시글 수정 api
    Call<String> updatePost(@Part("post_id") String post_id,
                            @Part("post_contents") String post_contents,
                            @Part("fileContents[]") RequestBody fileContents,
                            @Part List<MultipartBody.Part> files,
                            @Part("mentioned_user_id_list[]") String mentioned_user_id_list,
                            @Part("mentioned_user_id_list[]") String mentioned_user_id_list1);

    @FormUrlEncoded
    @POST("post/deletePost") // 게시글 삭제 api
    Call<String> deletePost(@Field("post_id") String post_id);

    @GET("post/selectPostUsingPostId") // 게시물 아이디(게시글 auto_increment 값)로 게시물 조회 api
    Call<List<post_Response>> selectPostUsingPostId(@Query("post_id") String post_id,// 클릭 한 게시물 auto_increment 값
                                               @Query("user_id") String user_id); // 로그인한 user_id 값(auto_increment) post/selectLikedPostUsingUserId

    @GET("post/selectLikedPostUsingUserId") // 좋아요 누른 모든 게시물 조회 api
    Call<List<post_Response>> selectLikedPostUsingUserId(@Query("user_id") String user_id); // 로그인한 user_id 값(auto_increment)

    @GET("post/selectPostUsingPostWriterId") // 작성자 아이디로 게시물 조회  api
    Call<List<post_Response>> selectPostUsingPostWriterId(@Query("post_writer_id") String post_writer_id); // 로그인한 user_id 값(auto_increment)

    @GET("post/selectPostMeAndFolloweeAndCommunity") // 나와 팔로위, 속한 커뮤니티의 게시물 조회  api
    Call<List<post_Response>> selectPostMeAndFolloweeAndCommunity(@Query("user_id") String user_id); // 로그인한 user_id 값(auto_increment)

    // todo 게시물과 관련된 api (끝 지점)


    // todo 좋아요와 관련된 api (시작 지점)
    @FormUrlEncoded
    @POST("post/like") // 게시글 좋아요 api
    Call<String> like(@Field("post_id") String post_id,  // 게시글 post_id 값(auto_increment)
                      @Field("user_id") String user_id); // 로그인한 user_id 값(auto_increment)

    @FormUrlEncoded
    @POST("post/dislike") // 게시글 좋아요 취소 api
    Call<String> dislike(@Field("post_id") String post_id,  // 게시글 post_id 값(auto_increment)
                         @Field("user_id") String user_id); // 로그인한 user_id 값(auto_increment)
    // todo 좋아요와 관련된 api (끝 지점)



    // todo 댓글 관련된 api (시작 지점)
    @FormUrlEncoded
    @POST("comment/createComment") // 댓글 생성 api
    Call<String> createComment(@Field("post_id") String post_id, // 게시글 값(auto_increment)
                               @Field("comment_depth") String comment_depth, // 댓글인 경우 1, 대댓글인 경우2
                               @Field("comment_writer_id") String comment_writer_id, // 로그인한 user_id 값(auto_increment)
                               @Field("comment_contents") String comment_contents, // 내용
                               @Field("mentioned_user_id_list[]") String mentioned_user_id_list);

    @FormUrlEncoded
    @POST("comment/deleteComment") // 댓글 삭제 api
    Call<String> deleteComment(@Field("comment_id") String comment_id); // 댓글 값(auto_increment)

    @FormUrlEncoded
    @POST("comment/updateComment") // 댓글 수정 api
    Call<String> updateComment(@Field("comment_id") String comment_id,
                               @Field("comment_contents") String comment_contents,
                               @Field("mentioned_user_id_list[]") String mentioned_user_id_list1,
                               @Field("mentioned_user_id_list[]") String mentioned_user_id_list2,
                               @Field("mentioned_user_id_list[]") String mentioned_user_id_list3);

    @GET("comment/selectCommentUsingPostId") // 게시물 아이디(게시글 auto_increment 값)로 댓글 조회
    Call<List<reply_Response>> selectCommentUsingPostId(@Query("post_id") String post_id);// 클릭 한 게시물 auto_increment 값

    @GET("comment/selectCommentUsingCommentWriterId") // 작성자 아이디(user_id auto_increment 값)로 댓글 조회
    Call<List<reply_Response>> selectCommentUsingCommentWriterId(@Query("comment_writer_id") String comment_writer_id);
    // todo 댓글 관련된 api (끝 지점)

    // todo 팔로우 관련된 api (시작 지점)
    @GET("following/selectFollowerList") // 나를 팔로우한 사람 리스트 api
    Call<List<follower_Response>> selectFollowerList(@Query("followee_id") String followee_id); // 로그인한 user_id 값(auto_increment)

    @GET("following/selectFolloweeList") // 내가 팔로우한 사람 리스트 api
    Call<List<following_Response>> selectFolloweeList(@Query("follower_id") String follower_id); // 로그인한 user_id 값(auto_increment)

    @FormUrlEncoded
    @POST("following/createFollowing") // 팔로잉 신청 api
    Call<String> createFollowing(@Field("followee_id") String followee_id, // 팔로잉 할 사람 user_id 값(auto_increment)
                               @Field("follower_id") String follower_id); // 로그인한 user_id 값(auto_increment)

    @FormUrlEncoded
    @POST("following/deleteFollowing") // 팔로잉 취소 api
    Call<String> deleteFollowing(@Field("followee_id") String followee_id, // 팔로잉 취소 할 사람 user_id 값(auto_increment)
                                 @Field("follower_id") String follower_id); // 로그인한 user_id 값(auto_increment)

    // todo 팔로우 관련된 api (끝 지점)


    // todo 검섹어 관련된 api (끝 지점)
    @FormUrlEncoded
    @POST("realtimetrend/createSerarchText") // 검색어 저장 api
    Call<String> createSerarchText(@Field("searcher_id") String searcher_id, //  user_id 값(auto_increment)
                                 @Field("search_text") String search_text); // 검색어

    // todo 검섹어 관련된 api (끝 지점)

    // todo 블록체인 관련된 api (끝 지점)
    @FormUrlEncoded
    @POST("userinfo/createAddrToBlockchain") // 니모닉 api
    Call<String> createAddrToBlockchain(@Field("user_id") String user_id);
    // todo 블록체인 관련된 api (끝 지점)


    // todo 해시태그 관련된 api (끝 지점)
    @FormUrlEncoded
    @POST("hashtag/selectHashtagInfoList") //
    Call<String> selectHashtagInfoList(@Field("hashtag_name") String hashtag_name);
    // todo 해시태그 관련된 api (끝 지점)


    // todo 맨션 관련된 api (끝 지점)
    @GET("mention/selectUserListForMention") //
    Call<String> selectUserListForMention(@Query("user_id") String user_id,
                                          @Query("search_name") String search_name);
    // todo 맨션 관련된 api (끝 지점)


}

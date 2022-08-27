package com.example.bubbly.retrofit;

import com.example.bubbly.model.Chat_Room_Cre_Or_Del;
import com.example.bubbly.model.Chat_Room_Info;
import com.example.bubbly.model.OtherUserInfo;

import java.util.ArrayList;
import com.example.bubbly.kim_util_test.Kim_JoinedCom_Response;
import com.example.bubbly.model.NFTSell_Item;
import com.example.bubbly.model.NFT_Item;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    // todo 회원정보와 관련된 api (시작 지점)
    // 이메일 인증번호 발송 api
    @FormUrlEncoded
    @POST("userinfo/sendEmailCertificationNum")
    Call<String> sendEmailCertificationNum(@Field("email_addr") String email_addr);

    // 아이디 인증번호 검증 api
    @GET("userinfo/verifyEmailCertificationNum")
    Call<String> verifyEmailCertificationNum(@Query("email_addr") String email_addr,
                                            @Query("certification_num") String certification_num);

    // 휴대폰 인증번호 발송 api
    @FormUrlEncoded
    @POST("userinfo/sendPhoneCertificationNum")
    Call<String> sendPhoneCertificationNum(@Field("phone_num") String phone_num);

    // 휴대폰 인증번호 검증 api
    @GET("userinfo/verifyPhoneCertificationNum")
    Call<String> verifyPhoneCertificationNum(@Query("phone_num") String phone_num,
                                            @Query("certification_num") String certification_num);

    // 아이디 중복 조회 api
    @GET("userinfo/selectIsExistingId")
    Call<String> selectIsExistingId(@Query("login_id") String login_id);

    // 아이디 변경
    @FormUrlEncoded
    @POST("userinfo/changeLoginId")
    Call<String> changeLoginId(@Field("changing_login_id") String changing_login_id,
                                @Field("user_id") String user_id);

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
                                @Part("profile_file_name") String profile_file_name,
                                @Part List<MultipartBody.Part> files,
                                @Part("user_id") String user_id,
                                @Part("self_info") String self_info);


    // 프로필 이미지 업데이트
    @Multipart
    @POST("userinfo/updateUserProfile")
    Call<String> updateUserProfile(@Part List<MultipartBody.Part> files,
                                   @Part("user_id") String user_id);

    // 회원정보 조회 api
    @GET("userinfo/selectUserInfo")
    Call<List<user_Response>> selectUserInfo(@Query("user_id") String user_id);


    //아이디 찾기를 위해 휴대폰 인증번호 전송
    @FormUrlEncoded
    @POST("userinfo/sendPhoneCertificationNumForFind")
    Call<String> sendPhoneCertificationNumForFind(@Field("phone_num") String phone_num);

    //아이디 찾기를 위한 휴대폰 인증번호 검증
    @GET("userinfo/verifyPhoneNumAndGetLoginId")
    Call<String> verifyPhoneNumAndGetLoginId(@Query("phone_num") String phone_num,
                                             @Query("certification_num") String certification_num);

    //비밀번호 변경
    @FormUrlEncoded
    @POST("userinfo/modifyPassword")
    Call<String> modifyPassword(@Field("user_id") String user_id,
                                @Field("modify_pw") String modify_pw);

    //사용자 id로 블록체인 계정 정보 조회
    @GET("userinfo/selectAddrUsingUserId")
    Call<String> selectAddrUsingUserId(@Query("user_id") String user_id);


    @GET("userinfo/selectUserSearchResultList") // 사용자 검색 api
    Call<String> selectUserSearchResultList(@Query("user_id") String user_id, //  user_id 값(auto_increment)
                                            @Query("search_text") String search_text); // 검색어


    // todo 회정보와 관련된 api (끝 지점)



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


    @GET("post/selectPostUsingPostContents") // 게시글 내용으로 검색
    Call<List<post_Response>> selectPostUsingPostContents(@Query("post_contents") String post_contents, //게시글 내용 = 검색어
                                             @Query("user_id") String user_id); //사용자 id


    @GET("post/selectPostUsingPostContentsOrderBylike") // 게시글 내용으로 검색
    Call<List<post_Response>> selectPostUsingPostContentsOrderBylike(@Query("user_id") String user_id, //사용자 id
                                                                     @Query("search_text") String search_text); //검색어

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

    @GET("realtimetrend/selectRealTimeTrends") // 검색창 실시간 트랜드
    Call <String> selectRealTimeTrends(@Query("current_time") String current_time); // 현재시간


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


    // 채팅방만들기에서 사용자 검색
    @GET("userinfo/selectSearchedUserList") //
    Call<ArrayList<OtherUserInfo>> selectSearchedUserList(@Query("user_id") String user_id,
                                                          @Query("search_text") String search_text);

    // 채팅방 정보 저장
    @POST("chat/createChatRoom")
    Call<String> createChatRoom(@Body Chat_Room_Cre_Or_Del chatRoomCre);

    // 채팅방 멤버리스트 조회
    @GET("chat/selectChatParticipantUsingChatRoomId") //
    Call<ArrayList<OtherUserInfo>> selectChatParticipantUsingChatRoomId(@Query("chat_room_id") String chatRoomId);

    // 채팅방 아이디로 채팅방정보 조회
    @GET("chat/selectChatRoomInfo") //
    Call<ArrayList<Chat_Room_Info>> selectChatRoomInfo(@Query("chat_room_id") String chatRoomId);

    // 사용자 아이디로 채팅방 리스트 조회
    @GET("chat/selectChatRoomListUsingUserId") //
    Call<ArrayList<Chat_Room_Info>> selectChatRoomListUsingUserId(@Query("user_id") String userId);

    // 채팅방 정보 삭제
    @FormUrlEncoded
    @POST("chat/deleteChatRoom")
    Call<String> deleteChatRoom(@Field("chat_room_id") String chatRoomId);

    // 채팅방 멤버 삭제
    @FormUrlEncoded
    @POST("chat/deleteChatParticipant")
    Call<String> deleteChatParticipant(@Field("user_id") String userId, @Field("chat_room_id") String chatRoomId);

    // todo nft 관련된 api (끝 지점)
    @Multipart
    @POST("nft-creation") // nft 저장
    Call<String> nftCreation(@Part("mnemonic") String mnemonic,
                            @Part("assetName") String assetName,
                            @Part("description") String description,
                            @Part("user_id") String user_id,
                            @Part("post_id") String post_id,
                            @Part List<MultipartBody.Part> files);

    @FormUrlEncoded
    @POST("nft-sell") // nft 판매
    Call<String> nftSell(@Field("mnemonic") String mnemonic,
                         @Field("nft_id") String nft_id,
                         @Field("sell_price") String sell_price,
                         @Field("seller_id") String seller_id,
                         @Field("nft_desc") String nft_desc);

    @FormUrlEncoded
    @POST("nft-stop-sell") // nft 판매 취소
    Call<String> nftStopSell(@Field("mnemonic") String mnemonic,
                        @Field("nft_id") String nft_id,
                        @Field("app_id") String app_id,
                        @Field("sell_price") String sell_price);


    @FormUrlEncoded
    @POST("nft-buy") // nft 구매
    Call<String> nftBuy(@Field("nft_owner_address") String nft_owner_address,
                         @Field("buyer_mnemonic") String buyer_mnemonic,
                         @Field("nftID") String nftID,
                         @Field("appID") String appID,
                         @Field("buyPrice") String buyPrice,
                         @Field("buyer_id") String buyer_id);

    @GET("nft/selectNftUsingNftId") // nft id로 정보 조회
    Call<String> selectNftUsingNftId(@Query("nft_id") String nft_id);

    @GET("nft/selectNftUsingHolderId") //holder_id로 nft 조회
    Call<List<NFT_Item>> selectNftUsingHolderId(@Query("holder_id") String holder_id);

    @GET("nft/selectAllSelledNftList") // 모든 판매중인 nft리스트
    Call<String> selectAllSelledNftList();

    @GET("nft/selectSelledNftListUsingSellerId") // 특정 사용자가 판매중인 NFT리스트
    Call<List<NFTSell_Item>> selectSelledNftListUsingSellerId(@Query("seller_id") String seller_id);


    // todo nft 관련된 api (끝 지점)


    // todo community 관련 api (시작 지점)

    @GET("community/selectCommunitySearchResultList") // 사용자 검색 api
    Call<List<Kim_JoinedCom_Response>> selectCommunitySearchResultList(@Query("search_text") String search_text); // 검색어

    // todo community 관련 api (끝 지점)

    //todo wallet 관련 api
    @FormUrlEncoded
    @POST("wallet/exchange")
    Call<String> exchange(@Field("sender_addr") String sender_addr,
                          @Field("sender_mnemonic") String sender_mnemonic,
                          @Field("token_amount") String token_amount);

    // todo 블록체인에서 거래기록 가져오기
    @GET("v2/accounts/{address}/transactions")
    Call<String> transactionHistory(@Header("x-api-key") String token,
                                    @Path(value = "address", encoded = true) String address,
                                    @Query("limit") int limit, @Query("next") String nextToken);
}

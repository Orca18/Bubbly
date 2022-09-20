package com.mainnet.bubbly.model;

public class MyCommunitiesList_Item {
    // 프사 링크, 유저명, 아이디, 내용, 미디어, 좋아요수, 답글수, 리트윗수, 게시글 링크, 게시 시간
    private String profileImageURL;
    private String userName;
    private String userId;
    private String postContent;
    private String postMedia;
    private int postLikeCount;
    private int postReplyCount;
    private int postRebuCount;
    private String postURL;
    private String postTime;

    public MyCommunitiesList_Item(String imageUrl, String userName, String userId, String postContent, String postMedia, int postLikeCount, int postReplyCount, int postRebuCount, String postURL, String postTime) {
        this.profileImageURL = imageUrl;
        this.userName = userName;
        this.userId = userId;
        this.postContent = postContent;
        this.postMedia = postMedia;
        this.postLikeCount = postLikeCount;
        this.postReplyCount = postReplyCount;
        this.postRebuCount = postRebuCount;
        this.postURL = postURL;
        this.postTime = postTime;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostContent() {
        return postContent;
    }

    public String getPostMedia() {
        return postMedia;
    }

    public int getPostLikeCount() {
        return postLikeCount;
    }

    public int getPostReplyCount() {
        return postReplyCount;
    }

    public int getPostRebuCount() {
        return postRebuCount;
    }

    public String getPostURL() {
        return postURL;
    }
    public String getPostTime() {
        return postTime;
    }
}



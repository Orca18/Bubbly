package com.mainnet.bubbly.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class post_Response {

    @Expose
    @SerializedName("post_id") private String post_id;

    @Expose
    @SerializedName("post_writer_id") private String post_writer_id;

    @Expose
    @SerializedName("writer_name") private String writer_name;

    @Expose
    @SerializedName("post_contents") private String post_contents;

    @Expose
    @SerializedName("file_save_names") private String file_save_names;

    @Expose
    @SerializedName("like_count") private String like_count;

    @Expose
    @SerializedName("like_yn") private String like_yn;

    @Expose
    @SerializedName("share_post_yn") private String share_post_yn;

    @Expose
    @SerializedName("nft_post_yn") private String nft_post_yn;

    @Expose
    @SerializedName("nick_name") private String nick_name;

    @Expose
    @SerializedName("profile_file_name") private String profile_file_name;

    @Expose
    @SerializedName("cre_datetime") private String cre_datetime;

    @Expose
    @SerializedName("mentioned_user_list") private String[] mentioned_user_list;


    @Expose
    @SerializedName("login_id") private String login_id;

    @Expose
    @SerializedName("community_id") private String community_id;

    @Expose
    @SerializedName("post_type") private String post_type;



    public post_Response(String post_id, String post_writer_id, String writer_name, String post_contents, String file_save_names, String like_count, String like_yn, String share_post_yn, String nft_post_yn, String nick_name, String profile_file_name, String cre_datetime, String[] mentioned_user_list, String community_id, String login_id, String post_type) {
        this.post_id = post_id;
        this.post_writer_id = post_writer_id;
        this.writer_name = writer_name;
        this.post_contents = post_contents;
        this.file_save_names = file_save_names;
        this.like_count = like_count;
        this.like_yn = like_yn;
        this.share_post_yn = share_post_yn;
        this.nft_post_yn = nft_post_yn;
        this.nick_name = nick_name;
        this.profile_file_name = profile_file_name;
        this.cre_datetime = cre_datetime;
        this.mentioned_user_list = mentioned_user_list;
        this.community_id = community_id;
        this.login_id = login_id;
        this.post_type = post_type;
    }

    public String getCommunity_id() {
        return community_id;
    }

    public void setCommunity_id(String community_id) {
        this.community_id = community_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPost_writer_id() {
        return post_writer_id;
    }

    public void setPost_writer_id(String post_writer_id) {
        this.post_writer_id = post_writer_id;
    }

    public String getWriter_name() {
        return writer_name;
    }

    public void setWriter_name(String writer_name) {
        this.writer_name = writer_name;
    }

    public String getPost_contents() {
        return post_contents;
    }

    public void setPost_contents(String post_contents) {
        this.post_contents = post_contents;
    }

    public String getFile_save_names() {
        return file_save_names;
    }

    public void setFile_save_names(String file_save_names) {
        this.file_save_names = file_save_names;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }
    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public String getLike_yn() {
        return like_yn;
    }

    public void setLike_yn(String like_yn) {
        this.like_yn = like_yn;
    }

    public String getShare_post_yn() {
        return share_post_yn;
    }

    public void setShare_post_yn(String share_post_yn) {
        this.share_post_yn = share_post_yn;
    }

    public String getNft_post_yn() {
        return nft_post_yn;
    }

    public void setNft_post_yn(String nft_post_yn) {
        this.nft_post_yn = nft_post_yn;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getProfile_file_name() {
        return profile_file_name;
    }

    public void setProfile_file_name(String profile_file_name) {
        this.profile_file_name = profile_file_name;
    }

    public String getCre_datetime() {
        return cre_datetime;
    }

    public void setCre_datetime(String cre_datetime) {
        this.cre_datetime = cre_datetime;
    }

    public String[] getMentioned_user_list() {
        return mentioned_user_list;
    }

    public void setMentioned_user_list(String[] mentioned_user_list) {
        this.mentioned_user_list = mentioned_user_list;
    }
    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }
}

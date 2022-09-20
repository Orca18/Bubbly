package com.mainnet.bubbly.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class reply_Response {

    @Expose
    @SerializedName("post_id") private String post_id;

    @Expose
    @SerializedName("comment_writer_id") private String comment_writer_id;

    @Expose
    @SerializedName("comment_depth") private String comment_depth;

    @Expose
    @SerializedName("comment_contents") private String comment_contents;

    @Expose
    @SerializedName("nick_name") private String nick_name;

    @Expose
    @SerializedName("profile_file_name") private String profile_file_name;

    @Expose
    @SerializedName("mentioned_user_list") private String[] mentioned_user_list;

    @Expose
    @SerializedName("cre_datetime_comment") private String cre_datetime_comment;

    @Expose
    @SerializedName("comment_id") private String comment_id;

    public reply_Response(String post_id, String comment_writer_id, String comment_depth, String comment_contents, String nick_name, String profile_file_name, String[] mentioned_user_list, String cre_datetime_comment, String comment_id) {
        this.post_id = post_id;
        this.comment_writer_id = comment_writer_id;
        this.comment_depth = comment_depth;
        this.comment_contents = comment_contents;
        this.nick_name = nick_name;
        this.profile_file_name = profile_file_name;
        this.mentioned_user_list = mentioned_user_list;
        this.cre_datetime_comment = cre_datetime_comment;
        this.comment_id = comment_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getCre_datetime_comment() {
        return cre_datetime_comment;
    }

    public void setCre_datetime_comment(String cre_datetimee_comment) {
        this.cre_datetime_comment = cre_datetimee_comment;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getComment_writer_id() {
        return comment_writer_id;
    }

    public void setComment_writer_id(String comment_writer_id) {
        this.comment_writer_id = comment_writer_id;
    }

    public String getComment_depth() {
        return comment_depth;
    }

    public void setComment_depth(String comment_depth) {
        this.comment_depth = comment_depth;
    }

    public String getComment_contents() {
        return comment_contents;
    }

    public void setComment_contents(String comment_contents) {
        this.comment_contents = comment_contents;
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

    public String[] getMentioned_user_list() {
        return mentioned_user_list;
    }

    public void setMentioned_user_list(String[] mentioned_user_list) {
        this.mentioned_user_list = mentioned_user_list;
    }
}

package com.gic.memorableplaces.DataModels;

public class Comments {

    private String comment;
    private String user_id;
    private String date_posted;
    private String username;
    private String profile_photo;
    private String commentID;

    public Comments() {

    }

    public Comments(String comment, String user_id, String date_posted, String username, String profile_photo, String commentID) {
        this.comment = comment;
        this.user_id = user_id;
        this.date_posted = date_posted;
        this.username = username;
        this.profile_photo = profile_photo;
        this.commentID = commentID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_posted() {
        return date_posted;
    }

    public void setDate_posted(String date_posted) {
        this.date_posted = date_posted;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "comment='" + comment + '\'' +
                ", user_id='" + user_id + '\'' +
                ", date_posted='" + date_posted + '\'' +
                ", username='" + username + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", commentID='" + commentID + '\'' +
                '}';
    }
}

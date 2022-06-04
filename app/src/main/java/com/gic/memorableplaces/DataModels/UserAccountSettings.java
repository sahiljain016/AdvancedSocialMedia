package com.gic.memorableplaces.DataModels;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class UserAccountSettings {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "display_name")
    private String display_name;
    @ColumnInfo(name = "following")
    private long following;
    @ColumnInfo(name = "followers")
    private long followers;
    @ColumnInfo(name = "posts")
    private long posts;
    private String uid;
    @ColumnInfo(name = "course")
    private String course;
    private String profile_photo;
    private String website;

    public UserAccountSettings() {


    }

    public UserAccountSettings(String display_name, long following, long followers,
                               long posts, String uid, String profile_photo, @NonNull String username, String website, String course) {
        this.display_name = display_name;
        this.following = following;
        this.followers = followers;
        this.posts = posts;
        this.uid = uid;
        this.course = course;
        this.profile_photo = profile_photo;
        this.username = username;
        this.website = website;
    }


    public String getDisplay_name() {
        return display_name;
    }


    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }


    @Override
    public String toString() {
        return "UserAccountSettings{" +
                ", display_name='" + display_name + '\'' +
                ", following=" + following +
                ", friendly_xavierites=" + followers +
                ", posts=" + posts +
                ", uid='" + uid + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", username='" + username + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}

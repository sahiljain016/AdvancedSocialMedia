package com.gic.memorableplaces.DataModels;

public class UserAccountSettings {

    private String card_bio;
    private String display_name;
    private long following;
    private long friendly_xavierites;
    private long posts;
    private String uid;
    private String course;
    private String profile_photo;
    private String username;
    private String website;

    public UserAccountSettings(){


    }

    public UserAccountSettings(String card_bio, String display_name, long following, long friendly_xavierites,
                               long posts, String uid, String profile_photo, String username, String website, String course) {
        this.card_bio = card_bio;
        this.display_name = display_name;
        this.following = following;
        this.friendly_xavierites = friendly_xavierites;
        this.posts = posts;
        this.uid = uid;
        this.course = course;
        this.profile_photo = profile_photo;
        this.username = username;
        this.website = website;
    }

    public String getCard_bio() {
        return card_bio;
    }

    public void setCard_bio(String card_bio) {
        this.card_bio = card_bio;
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

    public long getFriendly_xavierites() {
        return friendly_xavierites;
    }

    public void setFriendly_xavierites(long friendly_xavierites) {
        this.friendly_xavierites = friendly_xavierites;
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
                "description='" + card_bio + '\'' +
                ", display_name='" + display_name + '\'' +
                ", following=" + following +
                ", friendly_xavierites=" + friendly_xavierites +
                ", posts=" + posts +
                ", uid='" + uid + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", username='" + username + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}

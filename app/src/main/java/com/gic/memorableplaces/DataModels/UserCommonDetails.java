package com.gic.memorableplaces.DataModels;

public class UserCommonDetails {

    private String display_name;
    private String uid;
    private String profile_photo;
    private String username;

    public UserCommonDetails(){


    }

    public UserCommonDetails(String display_name, String uid, String profile_photo, String username) {
        this.display_name = display_name;
        this.uid = uid;
        this.profile_photo = profile_photo;
        this.username = username;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
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

    @Override
    public String toString() {
        return "UserCommonDetails{" +
                "display_name='" + display_name + '\'' +
                ", uid='" + uid + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

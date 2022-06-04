package com.gic.memorableplaces.DataModels;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Entity(tableName = "user_details")
public class User {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "user_id")
    private String user_id;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "auto_desp")
    private String auto_desp;

    @ColumnInfo(name = "display_name")
    private String display_name;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "course")
    private String course;

    @ColumnInfo(name = "photos_list")
    private ArrayList<String> photos_list;

    @ColumnInfo(name = "isAutoDespEnabled")
    private boolean isAutoDespEnabled;

    @ColumnInfo(name = "profile_photo")
    private String profile_photo;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "gender")
    private String gender;

    @Ignore
    private String age;

    @ColumnInfo(name = "phone_number")
    private long phone_number;

    @ColumnInfo(name = "is_email_verified")
    private boolean email_verified;

    @ColumnInfo(name = "sign_up_complete")
    private boolean sign_up_complete;

    public User() {
    }

    public User(String age) {
        this.age = age;

    }

    public User(@NonNull String user_id, String age, String username, String auto_desp, String display_name, String location, String course, ArrayList<String> photos_list, boolean isAutoDespEnabled, String profile_photo, String email, String gender, long phone_number, boolean email_verified, boolean sign_up_complete) {
        this.user_id = user_id;
        this.username = username;
        this.auto_desp = auto_desp;
        this.display_name = display_name;
        this.location = location;
        this.age = age;
        this.course = course;
        this.photos_list = photos_list;
        this.isAutoDespEnabled = isAutoDespEnabled;
        this.profile_photo = profile_photo;
        this.email = email;
        this.gender = gender;
        this.phone_number = phone_number;
        this.email_verified = email_verified;
        this.sign_up_complete = sign_up_complete;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Ignore
    public String getAge() {
        return age;
    }

    @Ignore
    public void setAge(String age) {
        this.age = age;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    @NonNull
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(@NonNull String user_id) {
        this.user_id = user_id;
    }

    public boolean isEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(boolean email_verified) {
        this.email_verified = email_verified;
    }

    public boolean isSign_up_complete() {
        return sign_up_complete;
    }

    public void setSign_up_complete(boolean sign_up_complete) {
        this.sign_up_complete = sign_up_complete;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public String getAuto_desp() {
        return auto_desp;
    }

    public void setAuto_desp(String auto_desp) {
        this.auto_desp = auto_desp;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public ArrayList<String> getPhotos_list() {
        return photos_list;
    }

    public void setPhotos_list(ArrayList<String> photos_list) {
        this.photos_list = photos_list;
    }

    public boolean isAutoDespEnabled() {
        return isAutoDespEnabled;
    }

    public void setAutoDespEnabled(boolean autoDespEnabled) {
        isAutoDespEnabled = autoDespEnabled;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public static class Converters {
        @TypeConverter
        public static ArrayList<String> fromString(String value) {
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            return new Gson().fromJson(value, listType);
        }

        @TypeConverter
        public static String fromArrayList(ArrayList<String> list) {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            return json;
        }
    }

    public void SetDefault() {
        this.user_id = "N/A";
        this.username = "N/A";
        this.auto_desp = "N/A";
        this.display_name = "N/A";
        this.course = "N/A";
        this.location = "0,0";
        this.photos_list = new ArrayList<>(5);
        this.photos_list.add("N/A");
        this.photos_list.add("N/A");
        this.photos_list.add("N/A");
        this.photos_list.add("N/A");
        this.photos_list.add("N/A");
        this.isAutoDespEnabled = false;
        this.profile_photo = "N/A";
        this.email = "N/A";
        this.gender = "N/A";
        this.phone_number = 0;
        this.email_verified = false;
        this.sign_up_complete = false;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", auto_desp='" + auto_desp + '\'' +
                ", display_name='" + display_name + '\'' +
                ", location='" + location + '\'' +
                ", course='" + course + '\'' +
                ", photos_list=" + photos_list +
                ", isAutoDespEnabled=" + isAutoDespEnabled +
                ", profile_photo='" + profile_photo + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", phone_number=" + phone_number +
                ", email_verified=" + email_verified +
                ", sign_up_complete=" + sign_up_complete +
                '}';
    }
}

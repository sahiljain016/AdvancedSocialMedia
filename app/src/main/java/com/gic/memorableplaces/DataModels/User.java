package com.gic.memorableplaces.DataModels;

public class User {
    private long college_uid;
    private String course;
    private String email;
    private String gender;
    private long phone_number;
    private String user_id;
    private String is_email_verified;
    private String page_number;
    private String username;
    public User(){
    }

    public User(long college_uid, String course, String email, String gender, long phone_number, String user_id,
                String is_email_verified, String page_number, String username) {
        this.college_uid = college_uid;
        this.course = course;
        this.email = email;
        this.gender = gender;
        this.phone_number = phone_number;
        this.user_id = user_id;
        this.is_email_verified = is_email_verified;
        this.page_number = page_number;
        this.username = username;
    }

    public long getCollege_uid() {
        return college_uid;
    }

    public void setCollege_uid(long college_uid) {
        this.college_uid = college_uid;
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

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIs_email_verified() {
        return is_email_verified;
    }

    public void setIs_email_verified(String is_email_verified) {
        this.is_email_verified = is_email_verified;
    }

    public String getPage_number() {
        return page_number;
    }

    public void setPage_number(String page_number) {
        this.page_number = page_number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "college_uid=" + college_uid +
                ", course='" + course + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", phone_number=" + phone_number +
                ", user_id='" + user_id + '\'' +
                ", is_email_verified='" + is_email_verified + '\'' +
                ", page_number='" + page_number + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

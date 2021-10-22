package com.gic.memorableplaces.DataModels;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "filter_privacy_details")
public class FilterPrivacyDetails {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "dummy_pk")
    private Integer dummy_pk = 0;
    @ColumnInfo(name = "age_birthdate_p")
    private boolean age_birthdate_p;
    @ColumnInfo(name = "gender_pronouns_p")
    private boolean gender_pronouns_p;
    @ColumnInfo(name = "general_details_p")
    private boolean general_details_p;
    @ColumnInfo(name = "college_year_p")
    private boolean college_year_p;
    @ColumnInfo(name = "society_p")
    private boolean society_p;
    @ColumnInfo(name = "hobbies_p")
    private boolean hobbies_p;
    @ColumnInfo(name = "video_games_p")
    private boolean video_games_p;
    @ColumnInfo(name = "music_p")
    private boolean music_p;
    @ColumnInfo(name = "books_p")
    private boolean books_p;
    @ColumnInfo(name = "movie_p")
    private boolean movie_p;


    public FilterPrivacyDetails() {

    }


    public FilterPrivacyDetails(@NonNull Integer dummy_pk, boolean age_birthdate_p, boolean gender_pronouns_p, boolean general_details_p, boolean college_year_p, boolean society_p, boolean hobbies_p, boolean video_games_p, boolean music_p, boolean books_p, boolean movie_p) {
        this.dummy_pk = dummy_pk;
        this.age_birthdate_p = age_birthdate_p;
        this.gender_pronouns_p = gender_pronouns_p;
        this.general_details_p = general_details_p;
        this.college_year_p = college_year_p;
        this.society_p = society_p;
        this.hobbies_p = hobbies_p;
        this.video_games_p = video_games_p;
        this.music_p = music_p;
        this.books_p = books_p;
        this.movie_p = movie_p;
    }

    @NonNull
    public Integer getDummy_pk() {
        return dummy_pk;
    }

    public void setDummy_pk(@NonNull Integer dummy_pk) {
        this.dummy_pk = dummy_pk;
    }

    public boolean isAge_birthdate_p() {
        return age_birthdate_p;
    }

    public void setAge_birthdate_p(boolean age_birthdate_p) {
        this.age_birthdate_p = age_birthdate_p;
    }

    public boolean isGender_pronouns_p() {
        return gender_pronouns_p;
    }

    public void setGender_pronouns_p(boolean gender_pronouns_p) {
        this.gender_pronouns_p = gender_pronouns_p;
    }

    public boolean isGeneral_details_p() {
        return general_details_p;
    }

    public void setGeneral_details_p(boolean general_details_p) {
        this.general_details_p = general_details_p;
    }

    public boolean isCollege_year_p() {
        return college_year_p;
    }

    public void setCollege_year_p(boolean college_year_p) {
        this.college_year_p = college_year_p;
    }

    public boolean isSociety_p() {
        return society_p;
    }

    public void setSociety_p(boolean society_p) {
        this.society_p = society_p;
    }

    public boolean isHobbies_p() {
        return hobbies_p;
    }

    public void setHobbies_p(boolean hobbies_p) {
        this.hobbies_p = hobbies_p;
    }

    public boolean isVideo_games_p() {
        return video_games_p;
    }

    public void setVideo_games_p(boolean video_games_p) {
        this.video_games_p = video_games_p;
    }

    public boolean isMusic_p() {
        return music_p;
    }

    public void setMusic_p(boolean music_p) {
        this.music_p = music_p;
    }

    public boolean isBooks_p() {
        return books_p;
    }

    public void setBooks_p(boolean books_p) {
        this.books_p = books_p;
    }

    public boolean isMovie_p() {
        return movie_p;
    }

    public void setMovie_p(boolean movie_p) {
        this.movie_p = movie_p;
    }

    @Override
    public String toString() {
        return "FilterPrivacyDetails{" +
                "dummy_pk=" + dummy_pk +
                ", age_birthdate_p=" + age_birthdate_p +
                ", gender_pronouns_p=" + gender_pronouns_p +
                ", general_details_p=" + general_details_p +
                ", college_year_p=" + college_year_p +
                ", society_p=" + society_p +
                '}';
    }
}

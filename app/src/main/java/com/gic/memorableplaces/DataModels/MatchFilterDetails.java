package com.gic.memorableplaces.DataModels;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

@Entity(tableName = "match_filter_details")
public class MatchFilterDetails implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "type")
    public String type;
    @ColumnInfo(name = "match_age_range")
    public String match_age_range;
    @ColumnInfo(name = "match_gender")
    public ArrayList<String> match_gender;
    @ColumnInfo(name = "match_general_details")
    public ArrayList<String> match_general_details;
    @ColumnInfo(name = "match_college_year")
    public ArrayList<String> match_college_year;
    @ColumnInfo(name = "match_society_in_college")
    public ArrayList<String> match_society_in_college;
    @ColumnInfo(name = "match_titles_posts")
    public ArrayList<String> match_titles_posts;
    @ColumnInfo(name = "match_hobbies")
    public ArrayList<String> match_hobbies;
    @ColumnInfo(name = "match_video_games")
    public ArrayList<String> match_video_games;
    @ColumnInfo(name = "match_music")
    public ArrayList<String> match_music;
    @ColumnInfo(name = "match_books")
    public ArrayList<String> match_books;
    @ColumnInfo(name = "match_movie")
    public ArrayList<String> match_movie;
    @ColumnInfo(name = "match_loc")
    public String match_loc;
    @ColumnInfo(name = "match_dd")
    public ArrayList<String> match_dd;

    public MatchFilterDetails() {

    }

    public MatchFilterDetails(@NonNull String type, String match_age_range, ArrayList<String> match_gender, ArrayList<String> match_general_details, ArrayList<String> match_college_year, ArrayList<String> match_society_in_college, ArrayList<String> match_titles_posts, ArrayList<String> match_hobbies, ArrayList<String> match_video_games, ArrayList<String> match_music, ArrayList<String> match_books, ArrayList<String> match_movie, String match_loc, ArrayList<String> match_dd) {
        this.type = type;
        this.match_age_range = match_age_range;
        this.match_gender = match_gender;
        this.match_general_details = match_general_details;
        this.match_college_year = match_college_year;
        this.match_society_in_college = match_society_in_college;
        this.match_titles_posts = match_titles_posts;
        this.match_hobbies = match_hobbies;
        this.match_video_games = match_video_games;
        this.match_music = match_music;
        this.match_books = match_books;
        this.match_movie = match_movie;
        this.match_loc = match_loc;
        this.match_dd = match_dd;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public String getMatch_age_range() {
        return match_age_range;
    }

    public void setMatch_age_range(String match_age_range) {
        this.match_age_range = match_age_range;
    }


    public ArrayList<String> getMatch_gender() {
        return match_gender;
    }

    public void setMatch_gender(ArrayList<String> match_gender) {
        this.match_gender = match_gender;
    }

    public ArrayList<String> getMatch_general_details() {
        return match_general_details;
    }

    public void setMatch_general_details(ArrayList<String> match_general_details) {
        this.match_general_details = match_general_details;
    }

    public ArrayList<String> getMatch_college_year() {
        return match_college_year;
    }

    public void setMatch_college_year(ArrayList<String> match_college_year) {
        this.match_college_year = match_college_year;
    }

    public ArrayList<String> getMatch_society_in_college() {
        return match_society_in_college;
    }

    public void setMatch_society_in_college(ArrayList<String> match_society_in_college) {
        this.match_society_in_college = match_society_in_college;
    }

    public ArrayList<String> getMatch_titles_posts() {
        return match_titles_posts;
    }

    public void setMatch_titles_posts(ArrayList<String> match_titles_posts) {
        this.match_titles_posts = match_titles_posts;
    }

    public ArrayList<String> getMatch_hobbies() {
        return match_hobbies;
    }

    public void setMatch_hobbies(ArrayList<String> match_hobbies) {
        this.match_hobbies = match_hobbies;
    }

    public ArrayList<String> getMatch_video_games() {
        return match_video_games;
    }

    public void setMatch_video_games(ArrayList<String> match_video_games) {
        this.match_video_games = match_video_games;
    }

    public ArrayList<String> getMatch_music() {
        return match_music;
    }

    public void setMatch_music(ArrayList<String> match_music) {
        this.match_music = match_music;
    }

    public ArrayList<String> getMatch_books() {
        return match_books;
    }

    public void setMatch_books(ArrayList<String> match_books) {
        this.match_books = match_books;
    }

    public ArrayList<String> getMatch_movie() {
        return match_movie;
    }

    public void setMatch_movie(ArrayList<String> match_movie) {
        this.match_movie = match_movie;
    }

    public String getMatch_loc() {
        return match_loc;
    }

    public void setMatch_loc(String match_loc) {
        this.match_loc = match_loc;
    }

    public ArrayList<String> getMatch_dd() {
        return match_dd;
    }

    public void setMatch_dd(ArrayList<String> match_dd) {
        this.match_dd = match_dd;
    }

    public void SetAllDefault(String InitialValue) {
        this.match_age_range = InitialValue;
        this.match_gender = new ArrayList<>(5);
        this.match_gender.add(InitialValue);
        this.match_general_details = new ArrayList<>();
        this.match_general_details.add(InitialValue);
        this.match_college_year = new ArrayList<>();
        this.match_college_year.add(InitialValue);
        this.match_society_in_college = new ArrayList<>(5);
        this.match_society_in_college.add(InitialValue);
        this.match_titles_posts = new ArrayList<>(5);
        this.match_titles_posts.add(InitialValue);
        this.match_hobbies = new ArrayList<>(5);
        this.match_hobbies.add(InitialValue);
        this.match_video_games = new ArrayList<>(5);
        this.match_video_games.add(InitialValue);
        this.match_music = new ArrayList<>(5);
        this.match_music.add(InitialValue);
        this.match_books = new ArrayList<>(5);
        this.match_books.add(InitialValue);
        this.match_movie = new ArrayList<>(5);
        this.match_movie.add(InitialValue);
        this.match_loc = InitialValue;
        this.match_dd = new ArrayList<>(5);
        this.match_dd.add(InitialValue);
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

    @Override
    public String toString() {
        return "MatchFilterDetails{" +
                "type='" + type + '\'' +
                ", match_age_range='" + match_age_range + '\'' +
                ", match_gender=" + match_gender +
                ", match_general_details=" + match_general_details +
                ", match_college_year=" + match_college_year +
                ", match_society_in_college=" + match_society_in_college +
                ", match_titles_posts=" + match_titles_posts +
                ", match_hobbies=" + match_hobbies +
                ", match_video_games=" + match_video_games +
                ", match_music=" + match_music +
                ", match_books=" + match_books +
                ", match_movie=" + match_movie +
                ", match_my_loc='" + match_loc + '\'' +
                ", match_my_dd=" + match_dd +
                '}';
    }
}

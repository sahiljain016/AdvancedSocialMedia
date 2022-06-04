package com.gic.memorableplaces.DataModels;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Entity(tableName = "filter_details")
public class FilterDetails {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "age")
    private Long age = 0L;
    @ColumnInfo(name = "birthdate")
    private String birthdate;
    @ColumnInfo(name = "gender")
    private String gender;
    @ColumnInfo(name = "pronouns")
    private String pronouns;
    @ColumnInfo(name = "general_details")
    public ArrayList<String> general_details;
    @ColumnInfo(name = "college_year")
    private String college_year;
    @ColumnInfo(name = "society_in_college")
    private ArrayList<String> society_in_college;
    @ColumnInfo(name = "titles_posts")
    private ArrayList<String> titles_posts;
    @ColumnInfo(name = "hobbies")
    private ArrayList<String> hobbies;
    @ColumnInfo(name = "video_games")
    private ArrayList<String> video_games;
    @ColumnInfo(name = "music")
    private ArrayList<String> music;
    @ColumnInfo(name = "books")
    private ArrayList<String> books;
    @ColumnInfo(name = "movies")
    private ArrayList<String> movies;
    @ColumnInfo(name = "my_loc")
    private String my_loc;
    @ColumnInfo(name = "my_dd")
    private ArrayList<String> my_dd;


    public FilterDetails() {

    }

    public FilterDetails(@NonNull String type, Long age, String birthdate, String gender, String pronouns, ArrayList<String> general_details, String college_year, ArrayList<String> society_in_college, ArrayList<String> titles_posts, ArrayList<String> hobbies, ArrayList<String> video_games, ArrayList<String> music, ArrayList<String> books, ArrayList<String> movies, String my_loc, ArrayList<String> my_dd) {
        this.type = type;
        this.age = age;
        this.birthdate = birthdate;
        this.gender = gender;
        this.pronouns = pronouns;
        this.general_details = general_details;
        this.college_year = college_year;
        this.society_in_college = society_in_college;
        this.titles_posts = titles_posts;
        this.hobbies = hobbies;
        this.video_games = video_games;
        this.music = music;
        this.books = books;
        this.movies = movies;
        this.my_loc = my_loc;
        this.my_dd = my_dd;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public ArrayList<String> getGeneral_details() {
        return general_details;
    }

    public void setGeneral_details(ArrayList<String> general_details) {
        this.general_details = general_details;
    }

    public String getCollege_year() {
        return college_year;
    }

    public void setCollege_year(String college_year) {
        this.college_year = college_year;
    }

    public ArrayList<String> getSociety_in_college() {
        return society_in_college;
    }

    public void setSociety_in_college(ArrayList<String> society_in_college) {
        this.society_in_college = society_in_college;
    }

    public ArrayList<String> getTitles_posts() {
        return titles_posts;
    }

    public void setTitles_posts(ArrayList<String> titles_posts) {
        this.titles_posts = titles_posts;
    }

    public ArrayList<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(ArrayList<String> hobbies) {
        this.hobbies = hobbies;
    }

    public ArrayList<String> getVideo_games() {
        return video_games;
    }

    public void setVideo_games(ArrayList<String> video_games) {
        this.video_games = video_games;
    }

    public ArrayList<String> getMusic() {
        return music;
    }

    public void setMusic(ArrayList<String> music) {
        this.music = music;
    }

    public ArrayList<String> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<String> books) {
        this.books = books;
    }

    public ArrayList<String> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<String> movies) {
        this.movies = movies;
    }

    public String getMy_loc() {
        return my_loc;
    }

    public void setMy_loc(String my_loc) {
        this.my_loc = my_loc;
    }

    public ArrayList<String> getMy_dd() {
        return my_dd;
    }

    public void setMy_dd(ArrayList<String> my_dd) {
        this.my_dd = my_dd;
    }

    public void SetAllDefault(String InitialValue) {
        this.age = 0L;
        this.birthdate = InitialValue;
        this.gender = InitialValue;
        this.pronouns = InitialValue;
        this.general_details = new ArrayList<>(10);
        this.general_details.add(InitialValue);
        this.college_year = InitialValue;
        this.society_in_college = new ArrayList<>(5);
        this.society_in_college.add(InitialValue);
        this.titles_posts = new ArrayList<>(5);
        this.titles_posts.add(InitialValue);
        this.hobbies = new ArrayList<>(5);
        this.hobbies.add(InitialValue);
        this.video_games = new ArrayList<>(5);
        this.video_games.add(InitialValue);
        this.music = new ArrayList<>(5);
        this.music.add(InitialValue);
        this.movies = new ArrayList<>(5);
        this.movies.add(InitialValue);
        this.books = new ArrayList<>(5);
        this.books.add(InitialValue);
        this.my_loc = InitialValue;
        this.my_dd = new ArrayList<>(5);
        this.my_dd.add(InitialValue);
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
        return "FilterDetails{" +
                "type='" + type + '\'' +
                ", age=" + age +
                ", birthdate='" + birthdate + '\'' +
                ", gender='" + gender + '\'' +
                ", pronouns='" + pronouns + '\'' +
                ", general_details=" + general_details +
                ", college_year='" + college_year + '\'' +
                ", society_in_college=" + society_in_college +
                ", titles_posts=" + titles_posts +
                ", hobbies=" + hobbies +
                ", video_games=" + video_games +
                ", music=" + music +
                ", books=" + books +
                ", movies=" + movies +
                ", my_loc='" + my_loc + '\'' +
                ", my_dd=" + my_dd +
                '}';
    }
}

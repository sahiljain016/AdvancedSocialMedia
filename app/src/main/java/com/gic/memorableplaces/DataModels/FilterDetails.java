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


    public FilterDetails() {

    }


    public FilterDetails(@NonNull String type, Long age, String birthdate, String gender, String pronouns, ArrayList<String> general_details, String college_year, ArrayList<String> society_in_college, ArrayList<String> hobbies, ArrayList<String> video_games, ArrayList<String> music, ArrayList<String> books, ArrayList<String> movies) {
        this.type = type;
        this.age = age;
        this.birthdate = birthdate;
        this.gender = gender;
        this.pronouns = pronouns;
        this.general_details = general_details;
        this.college_year = college_year;
        this.society_in_college = society_in_college;
        this.hobbies = hobbies;
        this.video_games = video_games;
        this.music = music;
        this.books = books;
        this.movies = movies;
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
                '}';
    }
}

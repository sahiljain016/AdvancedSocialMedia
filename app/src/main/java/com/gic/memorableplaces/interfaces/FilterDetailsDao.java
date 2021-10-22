package com.gic.memorableplaces.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.gic.memorableplaces.DataModels.FilterDetails;

import java.util.ArrayList;
import java.util.List;

@Dao
public
interface FilterDetailsDao {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Query("SELECT COUNT(*) FROM filter_details")
    Integer getCount();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void InsertNewDetail(FilterDetails filterDetails);

    @Query("UPDATE filter_details SET age = :age,birthdate = :birthdate WHERE type = :type")
    void UpdateAgeAndBirthdate(Long age, String birthdate, String type);

    @Query("UPDATE filter_details SET gender = :gender,pronouns = :pronouns WHERE type = :type")
    void UpdateGenderAndPronouns(String gender, String pronouns, String type);

    @Query("UPDATE filter_details SET general_details = :generalDetails WHERE type = :type")
    void UpdateGeneralDetails(ArrayList<String> generalDetails, String type);

    @Query("UPDATE filter_details SET college_year = :college_year WHERE type = :type")
    void UpdateCollegeYear(String college_year, String type);

    @Query("UPDATE filter_details SET society_in_college = :society WHERE type = :type")
    void UpdateSociety(ArrayList<String> society, String type);

    @Query("UPDATE filter_details SET hobbies = :hobbies WHERE type = :type")
    void UpdateHobbies(ArrayList<String> hobbies, String type);

    @Query("UPDATE filter_details SET video_games = :video_games WHERE type = :type")
    void UpdateVideoGames(ArrayList<String> video_games, String type);

    @Query("UPDATE filter_details SET music = :music WHERE type = :type")
    void UpdateMusic(ArrayList<String> music, String type);

    @Query("UPDATE filter_details SET books = :books WHERE type = :type")
    void UpdateBooks(ArrayList<String> books, String type);

    @Query("UPDATE filter_details SET movies = :movie WHERE type = :type")
    void UpdateMovie(ArrayList<String> movie, String type);

    @Query("SELECT * FROM filter_details")
    List<FilterDetails> GetAllFilterDetails();


}

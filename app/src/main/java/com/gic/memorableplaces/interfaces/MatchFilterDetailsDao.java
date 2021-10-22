package com.gic.memorableplaces.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.gic.memorableplaces.DataModels.MatchFilterDetails;

import java.util.ArrayList;
import java.util.List;

@Dao
public
interface MatchFilterDetailsDao {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Query("SELECT COUNT(*) FROM filter_details")
    Integer getCount();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void InsertNewDetail(MatchFilterDetails filterDetails);

    @Query("UPDATE match_filter_details SET match_age_range = :other_age_range WHERE type = :type")
    void UpdateMatchAgeRange(String other_age_range, String type);

    @Query("UPDATE match_filter_details SET match_gender = :match_gender WHERE type = :type")
    void UpdateMatchGender(String match_gender, String type);

    @Query("UPDATE match_filter_details SET match_general_details = :match_general_details WHERE type = :type")
    void UpdateMatchGeneralDetails(ArrayList<String> match_general_details, String type);

    @Query("UPDATE match_filter_details SET match_college_year = :match_college_year WHERE type = :type")
    void UpdateMatchCollegeYear(String match_college_year, String type);

    @Query("UPDATE match_filter_details SET match_society_in_college = :match_society WHERE type = :type")
    void UpdateMatchSociety(ArrayList<String> match_society, String type);

    @Query("UPDATE match_filter_details SET match_hobbies = :match_hobbies WHERE type = :type")
    void UpdateMatchHobbies(ArrayList<String> match_hobbies, String type);

    @Query("UPDATE match_filter_details SET match_video_games = :match_video_games WHERE type = :type")
    void UpdateMatchVideoGames(ArrayList<String> match_video_games, String type);

    @Query("UPDATE match_filter_details SET match_music = :match_music WHERE type = :type")
    void UpdateMatchMusic(ArrayList<String> match_music, String type);

    @Query("UPDATE match_filter_details SET match_books = :match_books WHERE type = :type")
    void UpdateMatchBooks(ArrayList<String> match_books, String type);

    @Query("UPDATE match_filter_details SET match_movie = :match_movie WHERE type = :type")
    void UpdateMatchMovie(ArrayList<String> match_movie, String type);

    @Query("SELECT * FROM match_filter_details")
    List<MatchFilterDetails> GetAllMatchFilterDetails();


}

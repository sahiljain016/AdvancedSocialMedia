package com.gic.memorableplaces.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.gic.memorableplaces.DataModels.FilterPrivacyDetails;

import java.util.List;

@Dao
public
interface FilterPrivacyDao {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Query("SELECT COUNT(*) FROM filter_privacy_details")
    Integer getCount();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void InsertPrivacyDetails(FilterPrivacyDetails filterPrivacyDetails);

    @Query("UPDATE filter_privacy_details SET age_birthdate_p = :age_birthdate_p WHERE dummy_pk = 0")
    void UpdateAgeAndBirthdatePrivacy(boolean age_birthdate_p);

    @Query("UPDATE filter_privacy_details SET gender_pronouns_p = :gender_pronouns_p WHERE dummy_pk = 0")
    void UpdateGenderAndPronounsPrivacy(boolean gender_pronouns_p);

    @Query("UPDATE filter_privacy_details SET general_details_p = :general_details_p WHERE dummy_pk = 0")
    void UpdateGeneralDetailsPrivacy(boolean general_details_p);

    @Query("UPDATE filter_privacy_details SET college_year_p = :college_year_p WHERE dummy_pk = 0")
    void UpdateCollegeYearPrivacy(boolean college_year_p);

    @Query("UPDATE filter_privacy_details SET society_p = :society_p WHERE dummy_pk = 0")
    void UpdateSocietyPrivacy(boolean society_p);

    @Query("UPDATE filter_privacy_details SET hobbies_p = :hobbies_p WHERE dummy_pk = 0")
    void UpdateHobbiesPrivacy(boolean hobbies_p);

    @Query("UPDATE filter_privacy_details SET video_games_p = :videos_games_P WHERE dummy_pk = 0")
    void UpdateVideoGamesPrivacy(boolean videos_games_P);

    @Query("UPDATE filter_privacy_details SET music_p = :music_p WHERE dummy_pk = 0")
    void UpdateMusicPrivacy(boolean music_p);

    @Query("UPDATE filter_privacy_details SET books_p = :books_p WHERE dummy_pk = 0")
    void UpdateBooksPrivacy(boolean books_p);

    @Query("UPDATE filter_privacy_details SET movie_p = :movies_p WHERE dummy_pk = 0")
    void UpdateMoviePrivacy(boolean movies_p);

    @Query("SELECT * FROM filter_privacy_details")
    List<FilterPrivacyDetails> GetAllPrivacyDetails();




}

package com.gic.memorableplaces.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.gic.memorableplaces.DataModels.User;

import java.util.ArrayList;
import java.util.List;

@Dao
public
interface UserDetailsDao {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertNewDetail(User user);

    @Query("SELECT * FROM user_details WHERE user_id = :user_ID")
    List<User> GetAllDetails(String user_ID);

    @Query("UPDATE user_details SET isAutoDespEnabled = :isAutoDespEnabled WHERE user_id =:user_id")
    void UpdateAutoDespBool(boolean isAutoDespEnabled, String user_id);

    @Query("UPDATE user_details SET is_email_verified = :isEmailVerified WHERE user_id =:user_id")
    void UpdateEmailVerifiedBool(boolean isEmailVerified, String user_id);

    @Query("UPDATE user_details SET sign_up_complete = :sign_up_complete WHERE user_id =:user_id")
    void UpdateSignUpCompleteBool(boolean sign_up_complete, String user_id);

    @Query("UPDATE user_details SET photos_list = :alsImages WHERE user_id =:user_id")
    void UpdatePhotoList(ArrayList<String> alsImages, String user_id);

    @Query("UPDATE user_details SET profile_photo = :profile_photo WHERE user_id =:user_id")
    void UpdateProfilePhoto(String profile_photo, String user_id);

    @Query("UPDATE user_details SET course = :course, auto_desp = :auto_desp,location=:Location,display_name = :display_name WHERE user_id =:user_id")
    void UpdateCourseDespFullNameLoc(String course, String auto_desp, String display_name, String Location, String user_id);
}

package com.gic.memorableplaces.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.gic.memorableplaces.DataModels.Questions;

import java.util.List;

@Dao
public
interface QuestionsDao {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertNewAnswer(Questions questions);

    @Query("SELECT * FROM questions")
    List<Questions> GetAllAnswers();

    @Query("DELETE FROM questions WHERE q_no = :q_no")
    void RemoveAnswer(String q_no);
}

package com.gic.memorableplaces.DataModels;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions")
public class Questions {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "q_no")
    private String q_no;
    @ColumnInfo(name = "answer")
    private String answer;


    public Questions() {

    }

    public Questions(@NonNull String q_no, String answer) {
        this.q_no = q_no;
        this.answer = answer;
    }

    @NonNull
    public String getQ_no() {
        return q_no;
    }

    public void setQ_no(@NonNull String q_no) {
        this.q_no = q_no;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Questions{" +
                "q_no='" + q_no + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}

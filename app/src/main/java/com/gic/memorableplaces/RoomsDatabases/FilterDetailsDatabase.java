package com.gic.memorableplaces.RoomsDatabases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.gic.memorableplaces.DataModels.FilterDetails;
import com.gic.memorableplaces.DataModels.FilterPrivacyDetails;
import com.gic.memorableplaces.DataModels.MatchFilterDetails;
import com.gic.memorableplaces.DataModels.Questions;
import com.gic.memorableplaces.interfaces.FilterDetailsDao;
import com.gic.memorableplaces.interfaces.FilterPrivacyDao;
import com.gic.memorableplaces.interfaces.MatchFilterDetailsDao;
import com.gic.memorableplaces.interfaces.QuestionsDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {FilterDetails.class, MatchFilterDetails.class, FilterPrivacyDetails.class, Questions.class}, version = 1, exportSchema = false)
@TypeConverters(MatchFilterDetails.Converters.class)
public abstract class FilterDetailsDatabase extends RoomDatabase {

    public abstract FilterDetailsDao filterDetailsDao();

    public abstract MatchFilterDetailsDao matchFilterDetailsDao();

    public abstract FilterPrivacyDao filterPrivacyDao();

    public abstract QuestionsDao questionsDao();

    private static volatile FilterDetailsDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static FilterDetailsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FilterDetailsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FilterDetailsDatabase.class, "filter_details_database")
                            .setJournalMode(JournalMode.TRUNCATE)
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}

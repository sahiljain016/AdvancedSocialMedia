package com.gic.memorableplaces.RoomsDatabases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.gic.memorableplaces.DataModels.User;
import com.gic.memorableplaces.interfaces.UserDetailsDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class}, version = 1, exportSchema = false)
@TypeConverters(User.Converters.class)
public abstract class UserDetailsDatabase extends RoomDatabase {

    public abstract UserDetailsDao userDetailsDao();

    private static volatile UserDetailsDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static UserDetailsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDetailsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserDetailsDatabase.class, "user_details_database")
                            .setJournalMode(JournalMode.TRUNCATE)
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}

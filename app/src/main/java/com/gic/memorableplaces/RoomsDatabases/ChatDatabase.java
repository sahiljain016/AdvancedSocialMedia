package com.gic.memorableplaces.RoomsDatabases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.gic.memorableplaces.DataModels.Chat;
import com.gic.memorableplaces.interfaces.ChatDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Chat.class}, version = 1, exportSchema = false)
public abstract class ChatDatabase extends RoomDatabase {

    public abstract ChatDao chatDao();

    private static volatile ChatDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static ChatDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ChatDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ChatDatabase.class, "chat_database")
                            .setJournalMode(JournalMode.TRUNCATE)
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}

package com.example.gpslocation;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {LocalLine.class},version = 6,exportSchema = false)
public abstract class LinesDatabase extends RoomDatabase {
    private static LinesDatabase INSTANCE;
    static synchronized LinesDatabase getDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),LinesDatabase.class,"Lines_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
    public abstract LineDao getLineDao();
}

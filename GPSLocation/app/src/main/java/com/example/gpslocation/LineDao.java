package com.example.gpslocation;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LineDao {
    @Insert
    void InsertLines(LocalLine...localLine);

    @Update
    void UpdateLines(LocalLine...localLine);

    @Delete
    void DeleteLines(LocalLine...localLine);

    @Query("DELETE FROM LOCALLINE")
    void DeleteAllLines();

    @Query("SELECT * FROM LOCALLINE ORDER BY ID DESC")
    LiveData<List<LocalLine>> getAllLines();

}

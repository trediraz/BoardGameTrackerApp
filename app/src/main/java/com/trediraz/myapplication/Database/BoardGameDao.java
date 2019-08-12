package com.trediraz.myapplication.Database;

import android.widget.LinearLayout;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BoardGameDao {

    @Query("SELECT * FROM game")
    List<Game> getAllGames();
}

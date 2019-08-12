package com.trediraz.myapplication.Database;

import android.widget.LinearLayout;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BoardGameDao {

    @Query("SELECT * FROM game")
    List<Game> getAllGames();

    @Query("SELECT id FROM game WHERE name = :name")
    int getGameIdByName(String name);

    @Query("SELECT * FROM scenario")
    List<Scenario> getAllScenarios();

    @Query("SELECT * FROM expansion")
    List<Expansion> getAllExpansion();

    @Insert
    void insertGame(Game game);

    @Insert
    void insertScenario(Scenario scenario);

    @Insert
    void insertExpansion(Expansion expansion);
}

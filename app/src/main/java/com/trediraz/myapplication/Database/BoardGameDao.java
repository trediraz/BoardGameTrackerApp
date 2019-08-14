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

    @Query("SELECT name FROM game")
    List<String> getAllGameNames();

    @Query("SELECT * FROM game WHERE name = :name")
    Game getGameByName(String name);

    @Query("SELECT id FROM game WHERE name = :name")
    int getGameIdByName(String name);

    @Query("SELECT expansion.name,expansion.game_id,expansion.id FROM expansion " +
            "INNER JOIN game ON expansion.game_id = game.id " +
            "WHERE game.name = :name")
    List<Expansion> getExpansionsByGameName(String name);

    @Query("SELECT scenario.id, scenario.game_id, scenario.name, scenario.type FROM scenario " +
            "INNER JOIN game ON scenario.game_id = game.id " +
            "WHERE game.name = :name")
    List<Scenario> getScenariosByGameName(String name);

    @Query("SELECT min_number_of_players FROM game WHERE name = :name LIMIT 1")
    int getMinNumberOfPlayersByGameName(String name);

    @Query("SELECT max_number_of_players FROM game WHERE name = :name LIMIT 1")
    int getMaxNumberOfPlayersByGameName(String name);

    @Query("SELECT name FROM player")
    List<String> getAllPlayerNames();

    @Insert
    void insertGame(Game game);

    @Insert
    void insertPlayer(Player player);

    @Insert
    void insertScenario(Scenario scenario);

    @Insert
    void insertExpansion(Expansion expansion);
}

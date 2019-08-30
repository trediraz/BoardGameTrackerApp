package com.trediraz.myapplication.Database;

import android.widget.LinearLayout;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BoardGameDao {

    @Query("SELECT * FROM `match` ORDER BY date ASC")
    List<Match> getAllMatches();

    @Query("SELECT * FROM `match` WHERE id = :id")
    Match getMatchById(int id);

    @Query("SELECT name FROM game")
    List<String> getAllGameNames();

    @Query("SELECT * FROM game WHERE id = :id")
    Game getGameById(int id);

    @Query("SELECT * FROM game WHERE name = :name")
    Game getGameByName(String name);

    @Query("SELECT id FROM game WHERE name = :name")
    int getGameIdByName(String name);

    @Query("SELECT name FROM game WHERE id = :id")
    String getGameNameById(int id);

    @Query("SELECT expansion.name,expansion.game_id,expansion.id FROM expansion " +
            "INNER JOIN game ON expansion.game_id = game.id " +
            "WHERE game.name = :name")
    List<Expansion> getExpansionsByGameName(String name);

    @Query("SELECT expansion.id,expansion.name,expansion.game_id FROM matchexpansion " +
            "LEFT JOIN expansion on expansion.id = matchexpansion.expansion_id " +
            "WHERE match_id = :id")
    List<Expansion> getExpansionsByMatchId(int id);

    @Query("SELECT scenario.id, scenario.game_id, scenario.name, scenario.type FROM scenario " +
            "INNER JOIN game ON scenario.game_id = game.id " +
            "WHERE game.name = :name")
    List<Scenario> getScenariosByGameName(String name);

    @Query("SELECT * FROM scenario WHERE id = :id")
    Scenario getScenarioById(int id);

    @Query("SELECT name FROM scenario WHERE id = :id")
    String getScenarioNameById(int id);

    @Query("SELECT * FROM scenario WHERE name = :name AND game_id = :gameId LIMIT 1")
    Scenario getScenarioByNameAndGameId(String name,int gameId);

    @Query("SELECT min_number_of_players FROM game WHERE name = :name LIMIT 1")
    int getMinNumberOfPlayersByGameName(String name);

    @Query("SELECT max_number_of_players FROM game WHERE name = :name LIMIT 1")
    int getMaxNumberOfPlayersByGameName(String name);

    @Query("SELECT * FROM player")
    List<Player> getAllPlayers();

    @Query("SELECT name FROM player")
    List<String> getAllPlayerNames();

    @Query("SELECT name FROM player WHERE id = :id")
    String getPlayerNameById(int id);

    @Query("SELECT * FROM player WHERE name = :name")
    Player getPlayerByName(String name);

    @Query("SELECT * FROM playedin WHERE match_id = :id")
    List<PlayedIn> getAllPlayersInMatchById(int id);

    @Query("UPDATE `match` " +
            "SET outcome = :newPlayerName " +
            "WHERE outcome = :playerName")
    void updatePlayerOutcomes(String playerName,String newPlayerName);

    @Query("SELECT COUNT(id) FROM player")
    int countPlayers();

    @Query("SELECT COUNT(id) FROM game")
    int countGames();

    @Insert
    long insertMatch(Match match);

    @Insert
    void insertGame(Game game);

    @Insert
    void insertPlayer(Player player);

    @Insert
    void insertScenario(Scenario scenario);

    @Insert
    void insertExpansion(Expansion expansion);

    @Insert
    void insertPlayedIn(PlayedIn playedIn);

    @Insert
    void insertMatchExpansion(MatchExpansion mE);

    @Update
    void updatePlayer(Player player);

    @Update
    void  updateExpansion(Expansion expansion);

    @Delete
    void deleteMatch(Match match);

}

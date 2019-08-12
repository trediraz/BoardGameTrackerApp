package com.trediraz.myapplication.Database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Game.class, Expansion.class, Player.class,PlayedIn.class,Scenario.class,Match.class,MatchExpansion.class},
        version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BoardGameDao boardGameDao();
}

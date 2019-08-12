package com.trediraz.myapplication.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Game
{
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public int min_number_of_players;

    public int max_number_of_players;

    public boolean requireScenario;
}

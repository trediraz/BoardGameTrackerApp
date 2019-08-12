package com.trediraz.myapplication.Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(foreignKeys = {
        @ForeignKey(entity = Game.class,
                parentColumns = "id",
                childColumns = "game_id"),
        @ForeignKey(entity = Scenario.class,
                parentColumns = "id",
                childColumns = "scenario_id")},
        indices = {@Index("game_id"),@Index("scenario_id")})
public class Match {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int game_id;

    public int scenario_id;

    public String outcome;

    public String date;

    public String comments;
}

package com.trediraz.myapplication.Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Game.class,
                parentColumns = "id",
                childColumns = "game_id")},
        indices = @Index("game_id"))
public class Scenario {

    @Ignore
    public static final String DEFAULT_NAME = "__default_scenario__";
    @Ignore
    public static final String OVERLORD = "Overlord";
    @Ignore
    public static final String COOP = "Coop";
    @Ignore
    public static final String VERSUS = "Versus";

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int game_id;

    public String name;

    public String type;
}

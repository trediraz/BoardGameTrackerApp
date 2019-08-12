package com.trediraz.myapplication.Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Game.class,
                parentColumns = "id",
                childColumns = "game_id")},
        indices = @Index("game_id"))
public class Scenario {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int game_id;

    public String name;

    public String type;
}

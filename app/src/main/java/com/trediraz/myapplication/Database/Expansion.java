package com.trediraz.myapplication.Database;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Game.class,
                parentColumns = "id",
                childColumns = "game_id")},
        indices = {@Index("game_id")}
)
public class Expansion {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int game_id;

    public String name;

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof Expansion))
            return false;
        return this.id == ((Expansion)obj).id;
    }
}

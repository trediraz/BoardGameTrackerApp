package com.trediraz.myapplication.Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"expansion_id","match_id"},
foreignKeys = {
        @ForeignKey(entity = Expansion.class,
                parentColumns = "id",
                childColumns = "expansion_id"),
        @ForeignKey(entity = Match.class,
                parentColumns = "id",
                childColumns = "match_id")},
        indices = {@Index("expansion_id"),@Index("match_id")})
public class MatchExpansion {
    public int expansion_id;
    public int match_id;
}

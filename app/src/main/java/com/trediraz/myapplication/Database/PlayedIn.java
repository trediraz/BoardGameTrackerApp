package com.trediraz.myapplication.Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(primaryKeys = {"player_id","match_id"},
        foreignKeys = {
                @ForeignKey(entity = Player.class,
                        parentColumns = "id",
                        childColumns = "player_id"),
                @ForeignKey(entity = Match.class,
                        parentColumns = "id",
                        childColumns = "match_id"),},
        indices = {@Index("player_id"),@Index("match_id")})
public class PlayedIn {

    public int player_id;
    public int match_id;
    public int place;

    public String role;
    @Ignore
    public static final String HERO = "Hero";
    @Ignore
    public static final String OVERLORD = "Overlord";
}

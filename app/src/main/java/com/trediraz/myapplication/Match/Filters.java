package com.trediraz.myapplication.Match;

import android.os.Parcel;
import android.os.Parcelable;

public class Filters implements Parcelable {
    private int game_id;
    private int player_id;
    private int scenario_id;
    private String dateFloor;
    private String dateCelling;
    private int expansion_id;

    private Filters(Parcel in) {
        game_id = in.readInt();
        player_id = in.readInt();
        scenario_id = in.readInt();
        dateFloor = in.readString();
        dateCelling = in.readString();
        expansion_id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(game_id);
        dest.writeInt(player_id);
        dest.writeInt(scenario_id);
        dest.writeString(dateFloor);
        dest.writeString(dateCelling);
        dest.writeInt(expansion_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Filters> CREATOR = new Creator<Filters>() {
        @Override
        public Filters createFromParcel(Parcel in) {
            return new Filters(in);
        }

        @Override
        public Filters[] newArray(int size) {
            return new Filters[size];
        }
    };
}

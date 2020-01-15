package com.trediraz.myapplication.Match;

import android.os.Parcel;
import android.os.Parcelable;

public class Filters implements Parcelable {
    String gameName;
    String playerName;
    String scenarioName;
    String dateFloor;
    String dateCelling;
    String expansionName;

    Filters() {

    }

    private Filters(Parcel in) {
        gameName = in.readString();
        playerName = in.readString();
        scenarioName = in.readString();
        dateFloor = in.readString();
        dateCelling = in.readString();
        expansionName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gameName);
        dest.writeString(playerName);
        dest.writeString(scenarioName);
        dest.writeString(dateFloor);
        dest.writeString(dateCelling);
        dest.writeString(expansionName);
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

package com.trediraz.myapplication.Match;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.trediraz.myapplication.Database.Expansion;
import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.Database.Match;
import com.trediraz.myapplication.Database.PlayedIn;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.List;

public class Filters implements Parcelable {
    String gameName;
    String playerName;
    String scenarioName;
    String dateFloor;
    String dateCelling;
    String expansionName;

    static final String ALL = "Wszystkie";
    static final String ALL_PLAYERS = "Wszyscy";
    static final String NO_ITEMS = "Brak";

    Filters() {
        gameName = ALL;
        playerName = ALL_PLAYERS;
        scenarioName = ALL;
        dateFloor = ALL;
        dateCelling = ALL;
        expansionName = ALL;
    }

    boolean isRight(Match match){
        if(!gameName.equals(ALL)){
            Game game = MainActivity.mBoardGameDao.getGameByName(gameName);
            if(!(match.game_id == game.id)){
                return false;
            } else {
                if(!scenarioName.equals(ALL)){
                    Scenario scenario = MainActivity.mBoardGameDao.getScenarioByNameAndGameId(scenarioName,game.id);
                    if(!(match.scenario_id == scenario.id))
                        return false;
                }
                List<Expansion> matchExpansions = MainActivity.mBoardGameDao.getExpansionsByMatchId(match.id);
                if(expansionName.equals(NO_ITEMS) && matchExpansions.size() != 0) {
                    return false;
                }
                else if(!expansionName.equals(ALL) && !expansionName.equals(NO_ITEMS)){
                    Expansion expansion = MainActivity.mBoardGameDao.getExpansionByNameAndGameId(expansionName,game.id);
                    if(!matchExpansions.contains(expansion))
                        return false;
                }
            }
        }
        if(!playerName.equals(ALL_PLAYERS)){
            List<String> playerNames = MainActivity.mBoardGameDao.getPlayerNamesFromMatch(match.id);
            if(!playerNames.contains(playerName))
                return false;
        }
        return true;
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

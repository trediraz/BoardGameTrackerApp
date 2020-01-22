package com.trediraz.myapplication.Match;

import android.os.Parcel;
import android.os.Parcelable;

import com.trediraz.myapplication.Database.Expansion;
import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.Database.Match;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Filters implements Parcelable {
    String gameName;
    String playerName;
    String scenarioName;
    String minDate;
    String maxDate;
    String expansionName;

    static final String ALL = "Wszystkie";
    static final String ALL_PLAYERS = "Wszyscy";
    static final String DEFAULT_MAX_DATE = "Teraz";
    static final String DEFAULT_MIN_DATE = "Początku";
    static final String NO_ITEMS = "Brak";

    Filters() {
        gameName = ALL;
        playerName = ALL_PLAYERS;
        scenarioName = ALL;
        expansionName = ALL;
        minDate = DEFAULT_MIN_DATE;
        maxDate = DEFAULT_MAX_DATE;
    }

    boolean isRight(Match match){
        if(!gameName.equals(ALL)){
            Game game = MainActivity.mBoardGameDao.getGameByName(gameName);
            if(!(match.game_id == game.id)){
                return false;
            }
            if(!scenarioName.equals(ALL)){
                Scenario scenario = MainActivity.mBoardGameDao.getScenarioByNameAndGameId(scenarioName,game.id);
                if(!(match.scenario_id == scenario.id))
                    return false;
            }
            List<Expansion> matchExpansions = MainActivity.mBoardGameDao.getExpansionsByMatchId(match.id);
            if(expansionName.equals(NO_ITEMS) && matchExpansions.size() != 0) {
                return false;
            }
            if(!expansionName.equals(ALL) && !expansionName.equals(NO_ITEMS)){
                Expansion expansion = MainActivity.mBoardGameDao.getExpansionByNameAndGameId(expansionName,game.id);
                if(!matchExpansions.contains(expansion))
                    return false;
            }

        }
        if(!playerName.equals(ALL_PLAYERS)){
            List<String> playerNames = MainActivity.mBoardGameDao.getPlayerNamesFromMatch(match.id);
            if(!playerNames.contains(playerName))
                return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date matchDate = sdf.parse(match.date);
            if(!minDate.equals(DEFAULT_MIN_DATE)){
               Date minD = sdf.parse(minDate);
               if(Objects.requireNonNull(matchDate).before(minD))
                   return false;
            }
            if(!maxDate.equals(Filters.DEFAULT_MAX_DATE)){
                Date maxD = sdf.parse(maxDate);
                if(Objects.requireNonNull(matchDate).after(maxD))
                    return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private Filters(Parcel in) {
        gameName = in.readString();
        playerName = in.readString();
        scenarioName = in.readString();
        minDate = in.readString();
        maxDate = in.readString();
        expansionName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gameName);
        dest.writeString(playerName);
        dest.writeString(scenarioName);
        dest.writeString(minDate);
        dest.writeString(maxDate);
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

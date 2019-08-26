package com.trediraz.myapplication.Match;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trediraz.myapplication.Database.Expansion;
import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.Database.Match;
import com.trediraz.myapplication.Database.PlayedIn;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MatchInfoFragment extends Fragment {


    public MatchInfoFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView gameName = Objects.requireNonNull(getView()).findViewById(R.id.game_name);
        TextView scenarioView = getView().findViewById(R.id.scenario);
        TextView comment = getView().findViewById(R.id.comments);
        TextView outcome = getView().findViewById(R.id.outcome);
        TextView date = getView().findViewById(R.id.date);

        int matchId = MatchInfoFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getMatchId();
        Match match = MainActivity.mBoardGameDao.getMatchById(matchId);
        Game game = MainActivity.mBoardGameDao.getGameById(match.game_id);
        Scenario scenario = MainActivity.mBoardGameDao.getScenarioById(match.scenario_id);

        gameName.setText(game.name);

        LinearLayout scenarioLayout = getView().findViewById(R.id.scenario_layout);
        if(scenario.name.equals(Scenario.DEFAULT_NAME))
            scenarioLayout.setVisibility(View.GONE);
        else
            scenarioView.setText(scenario.name);
        comment.setText(match.comments);
        outcome.setText(match.outcome);
        date.setText(match.date);

        LinearLayout players = getView().findViewById(R.id.players_list);
        List<PlayedIn> playedIns = MainActivity.mBoardGameDao.getAllPlayersInMatchById(match.id);
        Collections.sort(playedIns,new SortPlayedIns());
        for (PlayedIn playedIn : playedIns) {
            String playerName = MainActivity.mBoardGameDao.getPlayerNameById(playedIn.player_id);
            if(scenario.type.equals(Scenario.COOP))
                addTextView(players,playerName);
            else {
                PlayerView playerView = new PlayerView(getContext(),playerName,playedIn);
                players.addView(playerView);
            }
        }

        LinearLayout expansionsView = getView().findViewById(R.id.expansions_list);
        List<Expansion> expansions = MainActivity.mBoardGameDao.getExpansionsByMatchId(match.id);
        for (Expansion expansion : expansions) {
            addTextView(expansionsView,expansion.name);
        }


        setItemListVisibilityListener(R.id.game_title,R.id.game_name,R.id.game_divider);
        setItemListVisibilityListener(R.id.scenario_title,R.id.scenario,R.id.scenarios_divider);
        setItemListVisibilityListener(R.id.players_title,R.id.players_list,R.id.players_divider);
        setItemListVisibilityListener(R.id.comments_title,R.id.comments,R.id.comments_divider);
        setItemListVisibilityListener(R.id.expansions_title,R.id.expansions_list,R.id.expansions_divider);

    }


    private void addTextView(LinearLayout players, String playerName) {
        TextView textView = new TextView(getContext());
        textView.setText(playerName);
        textView.setTextAppearance(R.style.PrimaryText);
        players.addView(textView);
    }

    private void setItemListVisibilityListener(int titleId,int layoutId, int dividerId){
        final View layout = Objects.requireNonNull(getView()).findViewById(layoutId);
        final View divider = getView().findViewById(dividerId);
        TextView title = getView().findViewById(titleId);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemListVisibility(layout,divider);
            }
        });
    }

    private void setItemListVisibility(View layout, View divider) {
        if(layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
        }
    }

    class SortPlayedIns implements Comparator<PlayedIn>{

        @Override
        public int compare(PlayedIn playedIn, PlayedIn t1) {
            if(playedIn.role != null)
                if(playedIn.role.equals(Scenario.OVERLORD))
                    return -1;
            if(t1.role != null)
                if(t1.role.equals(Scenario.OVERLORD))
                    return 1;
            if(t1.place == PlayedIn.NO_PLACE)
                return -1;
            if(playedIn.place == PlayedIn.NO_PLACE)
                return 1;
            return playedIn.place - t1.place;
        }
    }

}

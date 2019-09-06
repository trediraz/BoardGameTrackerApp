package com.trediraz.myapplication.Match;


import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trediraz.myapplication.Database.Expansion;
import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.Database.Match;
import com.trediraz.myapplication.Database.PlayedIn;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        final List<Expansion> expansions = MainActivity.mBoardGameDao.getExpansionsByMatchId(match.id);
        for (Expansion expansion : expansions) {
            addTextView(expansionsView,expansion.name);
        }

        ImageView expansionsButton = getView().findViewById(R.id.edit_expansion_button);
        expansionsButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
            builder.setMultiChoiceItems(expansions.stream().map(p -> p.name).toArray(String[]::new),null,null);
            builder.show();
        });

        setItemListVisibilityListener(R.id.game_title,R.id.game_name,R.id.game_divider);
        setItemListVisibilityListener(R.id.scenario_title,R.id.scenario,R.id.scenarios_divider);
        setItemListVisibilityListener(R.id.players_title,R.id.players_list,R.id.players_divider);
        setVisibilityListenerWithCondition(R.id.comments_title,R.id.comments,R.id.comments_divider, match.comments.trim().equals(""));
        setVisibilityListenerWithCondition(R.id.expansions_title,R.id.expansions_list,R.id.expansions_divider,expansions.size() == 0);
    }

    private void setVisibilityListenerWithCondition(int titleId, int viewId, int divId, boolean condition) {
        View divider = Objects.requireNonNull(getView()).findViewById(viewId);
        View view = Objects.requireNonNull(getView()).findViewById(divId);
        View title = getView().findViewById(titleId);
        if(condition) {
            setItemListVisibility(view,divider);
            title.setClickable(false);
        } else
            setItemListVisibilityListener(title,view,divider);
    }


    private void addTextView(LinearLayout players, String playerName) {
        TextView textView = new TextView(getContext());
        textView.setText(playerName);
        textView.setTextAppearance(R.style.PrimaryText);
        players.addView(textView);
    }

    private void setItemListVisibilityListener(int titleId,int containerId, int dividerId){
        View title = Objects.requireNonNull(getView()).findViewById(titleId);
        View container = getView().findViewById(containerId);
        View divider = getView().findViewById(dividerId);
        setItemListVisibilityListener(title,container,divider);
    }


    private void setItemListVisibilityListener(View title, final View container, final View divider){
        title.setOnClickListener(view -> setItemListVisibility(container,divider));
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

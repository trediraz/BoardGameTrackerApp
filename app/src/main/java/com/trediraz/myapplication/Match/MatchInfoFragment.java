package com.trediraz.myapplication.Match;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.Database.Match;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

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
        TextView comment = getView().findViewById(R.id.comment);
        TextView outcome = getView().findViewById(R.id.outcome);
        TextView date = getView().findViewById(R.id.date);

        int matchId = MatchInfoFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getMatchId();
        Match match = MainActivity.mBoardGameDao.getMatchById(matchId);
        Game game = MainActivity.mBoardGameDao.getGameById(match.game_id);
        Scenario scenario = MainActivity.mBoardGameDao.getScenarioById(match.scenario_id);

        gameName.setText(game.name);
        scenarioView.setText(scenario.name);
        comment.setText(match.comments);
        outcome.setText(match.outcome);
        date.setText(match.date);
    }
}

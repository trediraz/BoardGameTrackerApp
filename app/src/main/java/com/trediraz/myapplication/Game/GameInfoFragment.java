package com.trediraz.myapplication.Game;

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
import com.trediraz.myapplication.Database.Match;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.List;
import java.util.Objects;

public class GameInfoFragment extends Fragment {

    public GameInfoFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView gameNameView = Objects.requireNonNull(getView()).findViewById(R.id.game_name);
        String gameName = GameInfoFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getGameName();
        gameNameView.setText(gameName);

        List<Scenario> scenarios = MainActivity.mBoardGameDao.getScenariosByGameName(gameName);
        List<Expansion> expansions = MainActivity.mBoardGameDao.getExpansionsByGameName(gameName);

        setScenarioView(scenarios);
        setExpansionView(expansions);
    }

    private void setScenarioView(List<Scenario> scenarios) {
        LinearLayout scenarioViews = Objects.requireNonNull(getView()).findViewById(R.id.scenarios);

        for (Scenario scenario : scenarios) {
            if(!scenario.name.equals(Scenario.DEFAULT_NAME)) {
                ScenarioView scenarioView = new ScenarioView(getContext(),scenario);
                scenarioViews.addView(scenarioView);
            }
        }
        if(scenarioViews.getChildCount() == 0){
            TextView textView = new TextView(getContext());
            textView.setText(R.string.no_item);
            textView.setTextAppearance(R.style.SecondaryText);
            scenarioViews.addView(textView);
        }
    }

    private void setExpansionView(List<Expansion> expansions) {
        LinearLayout expansionViews = Objects.requireNonNull(getView()).findViewById(R.id.expansions);
        for (Expansion expansion : expansions) {
            TextView expansionView = new TextView(getContext());
            expansionView.setText(expansion.name);
            expansionView.setTextAppearance(R.style.PrimaryText);
            expansionViews.addView(expansionView);
        }
    }
}

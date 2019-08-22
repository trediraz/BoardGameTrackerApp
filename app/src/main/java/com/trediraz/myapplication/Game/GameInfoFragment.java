package com.trediraz.myapplication.Game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.trediraz.myapplication.Database.Expansion;
import com.trediraz.myapplication.Database.Game;
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

        Game game = MainActivity.mBoardGameDao.getGameByName(gameName);

        int min = game.min_number_of_players;
        int max = game.max_number_of_players;


        setGameTypeView(scenarios);
        setScenarioView(scenarios);
        setExpansionView(expansions);
        setPlayerNumberView(min, max);
    }

    private void setGameTypeView(List<Scenario> scenarios) {
        TextView gameTypeView = Objects.requireNonNull(getView()).findViewById(R.id.game_type_text);
        String gameType = null;
        for (Scenario scenario : scenarios) {
            if(scenario.name.equals(Scenario.DEFAULT_NAME)) {
                gameType = scenario.type;
                break;
            }
        }
        LinearLayout parent = getView().findViewById(R.id.game_type_layout);
        if (gameType != null) {
            gameTypeView.setText(gameType);
            parent.setVisibility(View.VISIBLE);
        } else {
            parent.setVisibility(View.GONE);
        }

    }

    private void setScenarioView(List<Scenario> scenarios) {
        LinearLayout scenarioViews = Objects.requireNonNull(getView()).findViewById(R.id.scenarios);

        for (Scenario scenario : scenarios) {
            if(!scenario.name.equals(Scenario.DEFAULT_NAME)) {
                ScenarioView scenarioView = new ScenarioView(getContext(),scenario);
                scenarioViews.addView(scenarioView);
           }
        }
        setNoItemText(scenarioViews);
    }

    private void setNoItemText(LinearLayout layout) {
        if(layout.getChildCount() == 0){
            TextView textView = new TextView(getContext());
            textView.setText(R.string.no_item);
            textView.setTextAppearance(R.style.SecondaryText);
            layout.addView(textView);
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
        setNoItemText(expansionViews);

    }

    private void setPlayerNumberView(int min, int max) {
        TextView minView = Objects.requireNonNull(getView()).findViewById(R.id.min_players_number);
        TextView maxView = Objects.requireNonNull(getView()).findViewById(R.id.max_players_number);
        minView.setText(String.valueOf(min));
        maxView.setText(String.valueOf(max));
    }
}

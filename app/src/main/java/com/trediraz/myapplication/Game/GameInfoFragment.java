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
        View view = getView();
        if(view != null) {
            LinearLayout scenariosNameView = view.findViewById(R.id.info_scenario_name);
            LinearLayout scenarioTypeView = view.findViewById(R.id.info_scenario_type);
            LinearLayout expansionsView = view.findViewById(R.id.expansions_info);
            TextView gameNameView = view.findViewById(R.id.info_game_name);
            TextView min = view.findViewById(R.id.min_number);
            TextView max = view.findViewById(R.id.max_number);

            String gameName = GameInfoFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getGameName();
            gameNameView.setText(gameName);

            List<Expansion> expansions =  MainActivity.mBoardGameDao.getExpansionsByGameName(gameName);
            for(Expansion expansion : expansions){
                TextView textView = new TextView(getContext());
                textView.setText(expansion.name);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                expansionsView.addView(textView);
            }
            List<Scenario> scenarios = MainActivity.mBoardGameDao.getScenariosByGameName(gameName);
            for(Scenario scenario : scenarios){
                if(!scenario.name.equals("__default_scenario__")){
                    TextView name = new TextView(getContext());
                    TextView type = new TextView(getContext());
                    name.setText(scenario.name);
                    name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    type.setText(scenario.type);
                    type.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    scenariosNameView.addView(name);
                    scenarioTypeView.addView(type);
                }
            }

            String minNumber = String.valueOf(MainActivity.mBoardGameDao.getMinNumberOfPlayersByGameName(gameName));
            String maxNumber = String.valueOf(MainActivity.mBoardGameDao.getMaxNumberOfPlayersByGameName(gameName));

            min.setText(minNumber);
            max.setText(maxNumber);

        }
    }
}

package com.trediraz.myapplication.Match;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FiltersFragment extends Fragment {


    public FiltersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filters, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Filters filters = MatchFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getFilters();

        Spinner gameSpinner = Objects.requireNonNull(getView()).findViewById(R.id.game_spinner);
        List<String> gameNames = MainActivity.mBoardGameDao.getAllGameNames();
        gameNames.add(0,getString(R.string.all));
        ArrayAdapter<String> gameAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_list_item_1,gameNames);
        gameAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        gameSpinner.setAdapter(gameAdapter);

        Spinner scenarioSpinner = getView().findViewById(R.id.scenario_spinner);
        List<String> scenarioNames = new ArrayList<>();
        scenarioNames.add("-");
        ArrayAdapter<String> scenarioAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,scenarioNames);
        scenarioAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        scenarioSpinner.setAdapter(scenarioAdapter);
        scenarioSpinner.setEnabled(false);

        gameSpinner.setSelection(gameNames.indexOf(Objects.requireNonNull(filters).gameName));

        Button saveButton = getView().findViewById(R.id.save_button);
        saveButton.setOnClickListener(view -> {
            filters.gameName = gameSpinner.getSelectedItem().toString();
            FiltersFragmentDirections.ToMatches action = FiltersFragmentDirections.toMatches();
            action.setFilters(filters);
            Navigation.findNavController(getView()).navigate(action);
        });

        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                scenarioNames.clear();
                String gameName = (String) parent.getItemAtPosition(position);
                if(gameName.equals(getString(R.string.all))){
                    scenarioNames.add("-");
                }
                else{
                    List<Scenario> scenarios = MainActivity.mBoardGameDao.getScenariosByGameName(gameName);
                    if(scenarios.size() > 1){
                        scenarioNames.add(getString(R.string.all));
                    }
                    for(Scenario s : scenarios){
                        if(s.name.equals(Scenario.DEFAULT_NAME))
                            scenarioNames.add("Podstawka");
                        else
                            scenarioNames.add(s.name);
                    }
                }
                scenarioSpinner.setEnabled(scenarioNames.size() > 1);
                scenarioSpinner.setAdapter(null);
                scenarioSpinner.setAdapter(scenarioAdapter);
                scenarioSpinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button clearButton = Objects.requireNonNull(getView()).findViewById(R.id.clear_button);
        clearButton.setOnClickListener(v -> {
            filters.gameName = getString(R.string.all);
            gameSpinner.setSelection(0);
        });
    }
}

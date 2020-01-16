package com.trediraz.myapplication.Match;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

    private Filters mFilters;
    private int mScenarioIndex;
    private Spinner mGameSpinner;
    private Spinner mScenarioSpinner;

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

        mFilters = MatchFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getFilters();
        if(mFilters == null)
            mFilters = new Filters();

        mGameSpinner = Objects.requireNonNull(getView()).findViewById(R.id.game_spinner);
        List<String> gameNames = MainActivity.mBoardGameDao.getAllGameNames();
        gameNames.add(0,Filters.ALL);
        ArrayAdapter<String> gameAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_list_item_1,gameNames);
        gameAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mGameSpinner.setAdapter(gameAdapter);

        mScenarioSpinner = getView().findViewById(R.id.scenario_spinner);
        List<String> scenarioNames = new ArrayList<>();
        ArrayAdapter<String> scenarioAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,scenarioNames);
        scenarioAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        setStartingScenarioIndex();

        mGameSpinner.setSelection(gameNames.indexOf(mFilters.gameName));

        Button saveButton = getView().findViewById(R.id.save_button);
        saveButton.setOnClickListener(view -> {
            setFilters();
            FiltersFragmentDirections.ToMatches action = FiltersFragmentDirections.toMatches();
            action.setFilters(mFilters);
            Navigation.findNavController(getView()).navigate(action);
        });

        mGameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                scenarioNames.clear();
                String gameName = (String) parent.getItemAtPosition(position);
                if(gameName.equals(Filters.ALL)){
                    scenarioNames.add("-");
                }
                else{
                    List<Scenario> scenarios = MainActivity.mBoardGameDao.getScenariosByGameName(gameName);
                    if(scenarios.size() > 1){
                        scenarioNames.add(Filters.ALL);
                    }
                    for(Scenario s : scenarios){
                        if(s.name.equals(Scenario.DEFAULT_NAME))
                            scenarioNames.add("Podstawka");
                        else
                            scenarioNames.add(s.name);
                    }
                }
                mScenarioSpinner.setEnabled(scenarioNames.size() > 1);
                mScenarioSpinner.setAdapter(null);
                mScenarioSpinner.setAdapter(scenarioAdapter);
                mScenarioSpinner.setSelection(mScenarioIndex);
                mScenarioIndex = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button clearButton = Objects.requireNonNull(getView()).findViewById(R.id.clear_button);
        clearButton.setOnClickListener(v -> {
            clearFilters();
        });
    }

    private void setFilters() {
        mFilters.gameName = mGameSpinner.getSelectedItem().toString();
        String scenarioName = mScenarioSpinner.getSelectedItem().toString();
        if(scenarioName.equals("Podstawka"))
            mFilters.scenarioName = Scenario.DEFAULT_NAME;
        else
            mFilters.scenarioName = scenarioName;
    }

    private void clearFilters() {
        mFilters.gameName = Filters.ALL;
        mGameSpinner.setSelection(0);
    }

    private void setStartingScenarioIndex() {
        List<Scenario> scenarios = MainActivity.mBoardGameDao.getScenariosByGameName(mFilters.gameName);
        mScenarioIndex = 0;
        if(scenarios.size() > 1)
            for(int i = 0; i < scenarios.size(); i++){
                if(scenarios.get(i).name.equals(mFilters.scenarioName))
                    mScenarioIndex = i+1;
            }
    }
}

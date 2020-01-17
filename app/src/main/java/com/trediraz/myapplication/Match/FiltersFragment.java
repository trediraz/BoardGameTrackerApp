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

import com.trediraz.myapplication.Database.Expansion;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FiltersFragment extends Fragment {

    private Filters mFilters;
    private int mScenarioIndex;
    private int mExpansionIndex;
    private Spinner mGameSpinner;
    private Spinner mScenarioSpinner;
    private Spinner mExpansionSpinner;

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
        mScenarioSpinner.setAdapter(scenarioAdapter);

        mExpansionSpinner = getView().findViewById(R.id.expansion_spinner);
        List<String> expansionNames = new ArrayList<>();
        ArrayAdapter<String> expansionAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,expansionNames);
        expansionAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mExpansionSpinner.setAdapter(expansionAdapter);

        setStartingScenarioIndex();
        setStartingExpansionIndex();

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
                expansionNames.clear();
                String gameName = (String) parent.getItemAtPosition(position);
                if(gameName.equals(Filters.ALL)){
                    scenarioNames.add("-");
                    expansionNames.add("-");
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
                    List<Expansion> expansions = MainActivity.mBoardGameDao.getExpansionsByGameName(gameName);
                    if(expansions.size() > 0){
                        expansionNames.add(Filters.ALL);
                    }
                    expansionNames.add("-");
                    expansionNames.addAll(expansions.stream().map(x -> x.name).collect(Collectors.toList()));
                }
                setSpinner(mScenarioSpinner, mScenarioIndex, scenarioNames.size() > 1);
                setSpinner(mExpansionSpinner, mExpansionIndex, expansionNames.size() > 1);
                mExpansionIndex = 0;
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

    private void setSpinner(Spinner spinner, int index, boolean enabled) {
        spinner.setEnabled(enabled);
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinner.getAdapter();
        spinner.setAdapter(null);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(index);

    }

    private void setFilters() {
        mFilters.gameName = mGameSpinner.getSelectedItem().toString();
        String scenarioName = mScenarioSpinner.getSelectedItem().toString();
        if(scenarioName.equals("Podstawka"))
            mFilters.scenarioName = Scenario.DEFAULT_NAME;
        else
            mFilters.scenarioName = scenarioName;
        mFilters.expansionName = mExpansionSpinner.getSelectedItem().toString();
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

    private void setStartingExpansionIndex() {
        List<Expansion> expansions = MainActivity.mBoardGameDao.getExpansionsByGameName(mFilters.gameName);
        mExpansionIndex = 0;
        if(expansions.size() > 0)
            if(mFilters.expansionName.equals("-"))
                mExpansionIndex = 1;
            else
                for(int i = 0; i < expansions.size(); i++){
                    if(expansions.get(i).name.equals(mFilters.expansionName))
                        mExpansionIndex = i + 2;
                }
    }
}

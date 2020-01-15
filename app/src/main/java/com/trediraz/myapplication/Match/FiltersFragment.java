package com.trediraz.myapplication.Match;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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

        gameSpinner.setSelection(gameNames.indexOf(Objects.requireNonNull(filters).gameName));

        Button saveButton = getView().findViewById(R.id.save_button);
        saveButton.setOnClickListener(view -> {
            filters.gameName = gameSpinner.getSelectedItem().toString();
            FiltersFragmentDirections.ToMatches action = FiltersFragmentDirections.toMatches();
            action.setFilters(filters);
            Navigation.findNavController(getView()).navigate(action);
        });
    }
}

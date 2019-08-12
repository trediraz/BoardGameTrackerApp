package com.trediraz.myapplication.Game;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.trediraz.myapplication.Database.BoardGameDao;
import com.trediraz.myapplication.Database.Expansion;
import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.List;
import java.util.Objects;

public class GameFragment extends Fragment {

    public GameFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button addGameButton = Objects.requireNonNull(getView()).findViewById(R.id.add_game_button);

        LogAll();

        addGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogName = new GameDialog();
                dialogName.setCancelable(false);
                dialogName.show(Objects.requireNonNull(getFragmentManager()),"dialog");
            }
        });
    }

    private void LogAll() {
        List<Game> gamesNames = MainActivity.mBoardGameDao.getAllGames();
        for(Game game: gamesNames){
            Log.d("Database",game.name + " Id: " + game.id + " Min: " + game.min_number_of_players
                    + " Max: " + game.max_number_of_players +" bool: " + game.requireScenario);
        }
        List<Expansion> expansions = MainActivity.mBoardGameDao.getAllExpansion();
        for(Expansion expansion: expansions){
            Log.d("Database",expansion.game_id + " " + expansion.name);
        }
        List<Scenario> scenarios = MainActivity.mBoardGameDao.getAllScenarios();
        for(Scenario scenario: scenarios){
            Log.d("Database", scenario.game_id + " " + scenario.name +" "+ scenario.type);
        }
    }

}

package com.trediraz.myapplication;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

public class StatsFragment extends Fragment {


    public StatsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView numberOfGamesV = Objects.requireNonNull(getView()).findViewById(R.id.number_of_games);
        TextView numberOfPlayersV = Objects.requireNonNull(getView()).findViewById(R.id.number_of_players);
        TextView mostPlayedGameV = Objects.requireNonNull(getView()).findViewById(R.id.most_played_game);

        numberOfGamesV.setText(String.valueOf(MainActivity.mBoardGameDao.countGames()));
        numberOfPlayersV.setText(String.valueOf(MainActivity.mBoardGameDao.countPlayers()));
    }
}

package com.trediraz.myapplication.Game;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.trediraz.myapplication.Database.BoardGameDao;
import com.trediraz.myapplication.Database.Expansion;
import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.GameInfoFragment;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.ArrayList;
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

        ListView gameList = getView().findViewById(R.id.game_list_view);
        ArrayList<String> gameNames = (ArrayList<String>) MainActivity.mBoardGameDao.getAllGameNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),R.layout.game_list_view_layout,gameNames);
        gameList.setAdapter(adapter);
        gameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String gameName = (String) adapterView.getItemAtPosition(i);
                GameFragmentDirections.GoToGameInfo action = GameFragmentDirections.goToGameInfo(gameName);
                Navigation.findNavController(view).navigate(action);
            }
        });

        addGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogName = new GameDialog();
                dialogName.setCancelable(false);
                dialogName.show(Objects.requireNonNull(getFragmentManager()),"dialog");
            }
        });
    }


}

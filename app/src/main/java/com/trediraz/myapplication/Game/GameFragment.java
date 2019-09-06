package com.trediraz.myapplication.Game;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class GameFragment extends Fragment implements GameDialog.GameDialogInterface {

    private ArrayList<String> mGameNames;
    private ArrayAdapter<String> adapter;

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
        FloatingActionButton addGameButton = Objects.requireNonNull(getView()).findViewById(R.id.add_game_button);

        ListView gameList = getView().findViewById(R.id.game_list_view);
        mGameNames = (ArrayList<String>) MainActivity.mBoardGameDao.getAllGameNames();
        adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),R.layout.game_list_view_layout, mGameNames);
        gameList.setAdapter(adapter);
        gameList.setOnItemClickListener((adapterView, view, i, l) -> {
            String gameName = (String) adapterView.getItemAtPosition(i);
            GameFragmentDirections.GoToGameInfo action = GameFragmentDirections.goToGameInfo(gameName);
            Navigation.findNavController(view).navigate(action);
        });

        addGameButton.setOnClickListener(view -> {
            DialogFragment dialogName = new GameDialog();
            dialogName.setCancelable(false);
            dialogName.show(Objects.requireNonNull(getChildFragmentManager()),"dialog");
        });
    }


    @Override
    public void onGameAdded(Game game) {
        mGameNames.add(game.name);
        Collections.sort(mGameNames);
        adapter.notifyDataSetChanged();
    }
}

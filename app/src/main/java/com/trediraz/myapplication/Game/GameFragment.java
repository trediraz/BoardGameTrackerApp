package com.trediraz.myapplication.Game;


import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.ArrayList;
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

        gameList.setOnItemLongClickListener((adapterView, view, i, l) -> {
            String selected = (String) adapterView.getItemAtPosition(i);
            int id = MainActivity.mBoardGameDao.getGameIdByName(selected);
            int usages = MainActivity.mBoardGameDao.countGameUsages(id);
            String message;
            if(usages > 0)
                message = getString(R.string.delete_used, selected, usages);
            else
                message = getString(R.string.delete, selected);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
            builder.setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
                    .setPositiveButton("OK", (dialogInterface, i1) -> deleteGame(id, selected))
                    .setNegativeButton(R.string.cancel,null)
                    .show();
            return true;
        });

        addGameButton.setOnClickListener(view -> {
            DialogFragment dialogName = new GameDialog();
            dialogName.setCancelable(false);
            dialogName.show(Objects.requireNonNull(getChildFragmentManager()),"dialog");
        });
    }

    private void deleteGame(int id,String name) {
        updateDatabase(id);
        mGameNames.remove(name);
        adapter.notifyDataSetChanged();
    }

    private void updateDatabase(int id) {
        MainActivity.mBoardGameDao.deleteMatchesByGameID(id);
        MainActivity.mBoardGameDao.deleteScenariosByGameID(id);
        MainActivity.mBoardGameDao.deleteExpansionsByGameID(id);
        MainActivity.mBoardGameDao.deleteGame(id);
    }


    @Override
    public void onGameAdded(Game game) {
        mGameNames.add(game.name);
        Collections.sort(mGameNames);
        adapter.notifyDataSetChanged();
    }
}

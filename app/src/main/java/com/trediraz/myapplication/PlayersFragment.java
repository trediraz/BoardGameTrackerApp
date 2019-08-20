package com.trediraz.myapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trediraz.myapplication.Database.Player;

import java.util.List;
import java.util.Objects;

public class PlayersFragment extends Fragment {


    private List<String> players;

    public PlayersFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_players, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        ListView listView = Objects.requireNonNull(view).findViewById(R.id.players_list_view);
        players = MainActivity.mBoardGameDao.getAllPlayerNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),R.layout.game_list_view_layout,players);
        listView.setAdapter(adapter);

        FloatingActionButton addPlayerButton = view.findViewById(R.id.add_players_button);
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewPlayerDialog();
            }
        });
    }

    private void showNewPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
        final View view = LinearLayout.inflate(getContext(),R.layout.add_player_dialog_view,null);
        builder.setTitle(R.string.add_player)
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText editText = view.findViewById(R.id.player_name_edit_text);
                        String newPlayerName = editText.getText().toString();
                        for(String name : players){
                            if(newPlayerName.equals(name)){
                                Toast.makeText(getContext(),"Gracz o takiej nazwie już istnieje",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        Player  player = new Player();
                        player.name = newPlayerName;
                        MainActivity.mBoardGameDao.insertPlayer(player);
                        players.add(player.name);
                    }
                })
                .show();
    }
}

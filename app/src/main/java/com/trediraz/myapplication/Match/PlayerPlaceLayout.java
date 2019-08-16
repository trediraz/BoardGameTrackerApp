package com.trediraz.myapplication.Match;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.trediraz.myapplication.Database.Player;
import com.trediraz.myapplication.R;

import java.util.ArrayList;

public class PlayerPlaceLayout extends LinearLayout {

    private TextView placeView;
    private Spinner playersSpinner;

    public PlayerPlaceLayout(Context context) {
        super(context);
        init(context);
    }

    public PlayerPlaceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.player_place_layout,this);
        initComponents();
    }

    private void initComponents() {
        placeView = findViewById(R.id.place);
        playersSpinner = findViewById(R.id.player_spinner);
    }

    public void setTexts(int place, ArrayList<Player> players){
        placeView.setText(getResources().getString(R.string.place,place));
        ArrayList<String> playerNames = new ArrayList<>();
        playerNames.add("-");
        for (Player player : players) {
            playerNames.add(player.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item,playerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playersSpinner.setAdapter(adapter);
    }

    public void setPlaceText(String text){
        placeView.setText(text);
    }
}
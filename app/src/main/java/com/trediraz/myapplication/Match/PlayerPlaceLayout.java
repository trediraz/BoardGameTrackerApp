package com.trediraz.myapplication.Match;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.trediraz.myapplication.Database.Player;
import com.trediraz.myapplication.R;

import java.util.ArrayList;

public class PlayerPlaceLayout extends LinearLayout {

    public interface PlayerPlaceLayoutListener {
        void onDrawClicked();
        void onPlayerSelected(String name, int id);
    }

    private PlayerPlaceLayoutListener mListener;

    public void setListener(PlayerPlaceLayoutListener listener) {
        mListener = listener;
    }

    private TextView placeView;
    private Spinner playersSpinner;
    private CheckBox drawCheckBox;
    private int place;

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
        drawCheckBox = findViewById(R.id.draw_checkbox);
    }

    public void setTexts(final int place, ArrayList<Player> players){
        setPlace(place);

        ArrayList<String> playerNames = new ArrayList<>();
        playerNames.add(getContext().getString(R.string.choose_text));
        for (Player player : players) {
            playerNames.add(player.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item,playerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playersSpinner.setAdapter(adapter);

        drawCheckBox.setOnCheckedChangeListener((compoundButton, b) -> mListener.onDrawClicked());

        playersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.onPlayerSelected(adapterView.getItemAtPosition(i).toString(), getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void setPlace(int place){
        this.place = place;
        placeView.setText(getResources().getString(R.string.place,place));
    }

    public void hideDrawButton(){
        drawCheckBox.setVisibility(GONE);
    }

    public boolean isDraw(){
        return drawCheckBox.isChecked();
    }

    public String getSelectedPlayer(){
        return playersSpinner.getSelectedItem().toString();
    }
    public void setPlayerToNone(){
        playersSpinner.setSelection(0);
    }
    public int getPlace(){
        return place;
    }
}

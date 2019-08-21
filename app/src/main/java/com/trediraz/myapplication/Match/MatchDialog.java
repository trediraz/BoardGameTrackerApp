package com.trediraz.myapplication.Match;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.trediraz.myapplication.Database.Expansion;
import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.Database.Match;
import com.trediraz.myapplication.Database.MatchExpansion;
import com.trediraz.myapplication.Database.PlayedIn;
import com.trediraz.myapplication.Database.Player;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MatchDialog extends DialogFragment {

    private ViewFlipper mViewFlipper;
    private RadioGroup mRadioGameButtons;
    private RadioGroup mScenariosRadio;

    private List<Scenario> mAllScenarios;
    private List<Expansion> mAllExpansions;

    private Game mGame;
    private Scenario mScenario;
    private List<Player> mPlayers = new ArrayList<>();
    private String mOutcome;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
        builder.setTitle(R.string.chose_game)
                .setView(R.layout.match_dialog_layout);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewFlipper = getDialog().findViewById(R.id.new_match_view_flipper);
        Button nextButton = getDialog().findViewById(R.id.next_button);
        Button previousButton = getDialog().findViewById(R.id.previous_button);
        CheckBox toggleAllExpansions = getDialog().findViewById(R.id.toggle_all_expansions);
        DatePicker datePicker = getDialog().findViewById(R.id.date_picker);

        datePicker.setMaxDate(new Date().getTime());

        mScenariosRadio = getDialog().findViewById(R.id.scenarios_view);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNextAction();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePreviousAction();
            }
        });

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK){
                    if(KeyEvent.ACTION_DOWN == keyEvent.getAction()){

                        handlePreviousAction();
                    }
                    return true;
                }
                else
                    return false;
            }
        });

        final LinearLayout expansionView = getDialog().findViewById(R.id.expansions_view);
        toggleAllExpansions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for(int i = 0; i < expansionView.getChildCount(); i++){
                    CheckBox checkBox = (CheckBox) expansionView.getChildAt(i);
                    checkBox.setChecked(b);
                }
            }
        });

        setUpGameChoiceView();
        setUpPlayersView();
    }

    private void setUpGameChoiceView() {
        mRadioGameButtons = getDialog().findViewById(R.id.game_button_group);
        if(mRadioGameButtons.getChildCount() == 0){
            List<String> gameNames = MainActivity.mBoardGameDao.getAllGameNames();
            for(String name : gameNames){
                addRadioButtonToLinearLayout(mRadioGameButtons,name);
            }
            setScrollViewSize(mRadioGameButtons,9);
        }
    }

    private void setUpPlayersView() {
        List<Player> players = MainActivity.mBoardGameDao.getAllPlayers();
        LinearLayout playersView = getDialog().findViewById(R.id.players_view);
        playersView.removeAllViews();
        for(Player player : players){
            addCheckboxButtonToLinearLayout(playersView,player.name);
        }
        setScrollViewSize(playersView,9);
    }

    private void handlePreviousAction() {
        switch (mViewFlipper.getDisplayedChild()){
            case 0:
                dismiss();
                break;
            case 1:
                mScenariosRadio.clearCheck();
                mViewFlipper.showPrevious();
                break;
            case 2:
                if(mAllScenarios.size() == 1 && !mGame.requireScenario){
                    mViewFlipper.setDisplayedChild(0);
                } else {
                    mViewFlipper.showPrevious();
                }
                break;
            case 3:
                if(skipScenarioAndDialog())
                    mViewFlipper.setDisplayedChild(0);
                else if(mAllExpansions.size() == 0)
                    mViewFlipper.setDisplayedChild(1);
                else
                    mViewFlipper.showPrevious();
                break;
            case 5:
                if(mScenario.type.equals(Scenario.OVERLORD))
                    mViewFlipper.showPrevious();
                else
                    mViewFlipper.setDisplayedChild(3);
                break;
            default:
                mViewFlipper.showPrevious();
        }
        setTitle();
    }

    private boolean skipScenarioAndDialog(){
        return mAllExpansions.size() == 0 && mAllScenarios.size() == 1 && !mGame.requireScenario;
    }

    private void handleNextAction() {
        if (mViewFlipper.getDisplayedChild() != mViewFlipper.getChildCount()-1) {

            switch (mViewFlipper.getDisplayedChild()){
                case 0:
                    handleGameChoiceNextAction();
                    break;
                case 1:
                    handleScenarioChoiceNextAction();
                    break;
                case 3:
                    handlePlayerChoiceNextAction();
                    break;
                case 5:
                    handleResultChoiceNextAction();
                    break;

                default:
                    mViewFlipper.showNext();
            }
        setTitle();
        }else {
            updateDatabase();
            dismiss();
        }
    }

    private void updateDatabase() {
        String date;
        DatePicker datePicker = getDialog().findViewById(R.id.date_picker);
        date = String.format("%s-%s-%s",datePicker.getYear(),datePicker.getMonth()+1,datePicker.getDayOfMonth());

        EditText commentsView = getDialog().findViewById(R.id.comments);

        Match newMatch = new Match();
        newMatch.game_id = mGame.id;
        newMatch.scenario_id = mScenario.id;
        newMatch.outcome = mOutcome;
        newMatch.date = date;
        newMatch.comments = commentsView.getText().toString();
        newMatch.id = (int) MainActivity.mBoardGameDao.insertMatch(newMatch);

        String overlordPlayer = null;
        if(mScenario.type.equals(Scenario.OVERLORD)) {
            Spinner spinner = getDialog().findViewById(R.id.overlord_spinner);
            overlordPlayer = spinner.getSelectedItem().toString();
        }

        for (Player player : mPlayers) {
            PlayedIn playedIn = new PlayedIn();
            playedIn.match_id = newMatch.id;
            playedIn.player_id = player.id;
            if(mScenario.type.equals(Scenario.VERSUS))
                playedIn.place = getPlayerPlace(player.name);
            else playedIn.place = 0;
            if(mScenario.type.equals(Scenario.OVERLORD)){
                playedIn.role = player.name.equals(overlordPlayer) ? PlayedIn.OVERLORD : PlayedIn.HERO;
            }
            MainActivity.mBoardGameDao.insertPlayedIn(playedIn);
        }

        LinearLayout layout = getDialog().findViewById(R.id.expansions_view);
        for(int i = 0; i < layout.getChildCount(); i++){
            CheckBox checkBox = (CheckBox) layout.getChildAt(i);
            if(checkBox.isChecked()){
                MatchExpansion mE = new MatchExpansion();
                mE.match_id = newMatch.id;
                mE.expansion_id = getExpansionIdFromListByName(checkBox.getText().toString());
                Log.d("Database",mE.match_id + " " + mE.expansion_id);
                MainActivity.mBoardGameDao.insertMatchExpanion(mE);
            }
        }

    }

    private int getExpansionIdFromListByName(String name) {
        for (Expansion expansion : mAllExpansions) {
            if(expansion.name.equals(name))
                return expansion.id;
        }
        return -1;
    }

    private int getPlayerPlace(String name) {
        LinearLayout layout = getDialog().findViewById(R.id.game_outcome_view);
        for(int i = 0; i < layout.getChildCount();i++){
            PlayerPlaceLayout playerPlaceLayout = (PlayerPlaceLayout) layout.getChildAt(i);
            if(name.equals(playerPlaceLayout.getSelectedPlayer()))
                return playerPlaceLayout.getPlace();
        }
        return 0;
    }


    private void handleGameChoiceNextAction() {
        int checkedGameId = mRadioGameButtons.getCheckedRadioButtonId();
        if(checkedGameId == RadioGroup.NO_ID) {
            Toast.makeText(getContext(),getString(R.string.no_game_chosen),Toast.LENGTH_SHORT).show();
        } else {
            RadioButton checkedGameButton = mRadioGameButtons.findViewById(checkedGameId);
            String gameName = checkedGameButton.getText().toString();
            mGame = MainActivity.mBoardGameDao.getGameByName(gameName);
            setUpScenarioView();
            mViewFlipper.showNext();
        }
    }

    private void handleScenarioChoiceNextAction() {
        int checkScenarioId = mScenariosRadio.getCheckedRadioButtonId();
        if(checkScenarioId == RadioGroup.NO_ID){
            Toast.makeText(getContext(), R.string.no_scenario_chosen,Toast.LENGTH_SHORT).show();
        } else{
            setUpExpansionView();
            mScenario = getScenarioFromView(checkScenarioId);
            mViewFlipper.showNext();
        }

    }

    private Scenario getScenarioFromView(int id){
        RadioButton radioButton = mScenariosRadio.findViewById(id);
        String scenarioName = radioButton.getText().toString();
        if(scenarioName.equals(getString(R.string.default_scenario))){
            scenarioName = Scenario.DEFAULT_NAME;
        }
        return MainActivity.mBoardGameDao.getScenarioByNameAndGameId(scenarioName,mGame.id);
    }

    private void handlePlayerChoiceNextAction() {
        LinearLayout playersView = getDialog().findViewById(R.id.players_view);
        int max = MainActivity.mBoardGameDao.getMaxNumberOfPlayersByGameName(mGame.name);
        int min = MainActivity.mBoardGameDao.getMinNumberOfPlayersByGameName(mGame.name);
        int checkedButton = countChecks(playersView);
        if(checkedButton > max || checkedButton < min){
            if(min == max)
                Toast.makeText(getContext(),getString(R.string.max_eql_min) + max,Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(),getString(R.string.wrong_number_of_players,min,max),Toast.LENGTH_SHORT).show();
        } else {
            setPlayers(playersView);
            setUpOutcomeView();
            if(mScenario.type.equals(Scenario.OVERLORD)) {
                setOverlordView();
                mViewFlipper.showNext();
            } else
                mViewFlipper.setDisplayedChild(5);
        }
    }


    private void handleResultChoiceNextAction() {
        switch (mScenario.type) {
            case Scenario.VERSUS:
                if(isPlaceViewDataValid()) {
                    setPlayerOutcome();
                    mViewFlipper.showNext();
                }
                break;
            case Scenario.OVERLORD:
            case Scenario.COOP:
                setOutcomeFromSpinner();
                mViewFlipper.showNext();
        }
    }

    private boolean isPlaceViewDataValid() {
        final String NO_PLAYER = "-";
        LinearLayout places = getDialog().findViewById(R.id.game_outcome_view);
        CheckBox unfinished = getDialog().findViewById(R.id.unfinished_checkbox);
        if(!unfinished.isChecked()) {
            PlayerPlaceLayout child = (PlayerPlaceLayout) places.getChildAt(0);
            if(child.getSelectedPlayer().equals(NO_PLAYER)) {
                Toast.makeText(getContext(), R.string.no_player_at_first_place,Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        for(int i = 0; i < places.getChildCount();i++){
            PlayerPlaceLayout child = (PlayerPlaceLayout) places.getChildAt(i);
            PlayerPlaceLayout nextChild = (PlayerPlaceLayout) places.getChildAt(i+1);
            if(child.isDraw() && nextChild.getSelectedPlayer().equals(NO_PLAYER)) {
                Toast.makeText(getContext(), R.string.draw_place_empty,Toast.LENGTH_SHORT).show();
                return false;
            }
            if(child.getSelectedPlayer().equals(NO_PLAYER))
                for(int j = i+1; j < places.getChildCount();j++){
                    PlayerPlaceLayout placeLayout = (PlayerPlaceLayout) places.getChildAt(j);
                    if(!placeLayout.getSelectedPlayer().equals(NO_PLAYER)) {
                        Toast.makeText(getContext(), R.string.wrong_place_order, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
        }
        return true;
    }

    private void setPlayerOutcome() {
        LinearLayout places = getDialog().findViewById(R.id.game_outcome_view);
        CheckBox unfinished = getDialog().findViewById(R.id.unfinished_checkbox);
        if(unfinished.isChecked())
            mOutcome = getString(R.string.unfinished);
        else {
            PlayerPlaceLayout playerPlaceLayout = (PlayerPlaceLayout) places.getChildAt(0);
            if(playerPlaceLayout.isDraw())
                mOutcome = getString(R.string.draw);
            else
                mOutcome = playerPlaceLayout.getSelectedPlayer();
        }
    }

    private void setOutcomeFromSpinner() {
        Spinner spinner = getDialog().findViewById(R.id.outcome_spinner);
        mOutcome = spinner.getSelectedItem().toString();
    }

    private void setPlayers(@NonNull LinearLayout layout) {
        mPlayers.clear();
        for(int i = 0; i < layout.getChildCount(); i++){
            CheckBox checkBox = (CheckBox) layout.getChildAt(i);
            if(checkBox.isChecked()){
                String text = checkBox.getText().toString();
                mPlayers.add(MainActivity.mBoardGameDao.getPlayerByName(text));
            }
        }
    }

    private int countChecks(@NonNull LinearLayout layout){
        int result = 0;
        CheckBox checkBox;
        for(int i = 0; i < layout.getChildCount(); i++){
            checkBox = (CheckBox) layout.getChildAt(i);
            if(checkBox.isChecked())
                result++;
        }
        return result;
    }

    private void setUpScenarioView() {
        RadioGroup scenarioLayout = getDialog().findViewById(R.id.scenarios_view);
        scenarioLayout.removeAllViews();
        mAllScenarios = MainActivity.mBoardGameDao.getScenariosByGameName(mGame.name);
        if(mAllScenarios.size() == 1 && !mGame.requireScenario){
            mViewFlipper.showNext();
            mScenario = MainActivity.mBoardGameDao.getScenarioByNameAndGameId(Scenario.DEFAULT_NAME,mGame.id);
            setUpExpansionView();
        } else {
            for(Scenario scenario : mAllScenarios){
                String buttonText  = (scenario.name.equals(Scenario.DEFAULT_NAME)) ? getString(R.string.default_scenario) : scenario.name;
                addRadioButtonToLinearLayout(scenarioLayout,buttonText);
            }
            setScrollViewSize(scenarioLayout,9);
        }
    }

    private void setUpExpansionView() {
        mAllExpansions = MainActivity.mBoardGameDao.getExpansionsByGameName(mGame.name);
        if(mAllExpansions.size() == 0)
            mViewFlipper.showNext();
        else {
            LinearLayout expansionsView = getDialog().findViewById(R.id.expansions_view);
            expansionsView.removeAllViews();
            for(Expansion expansion : mAllExpansions){
                CheckBox checkBox = new CheckBox(getContext());
                setButtonAttributes(checkBox,expansion.name);
                expansionsView.addView(checkBox);
            }
            setScrollViewSize(expansionsView, 9);
        }

    }

    private void setOverlordView() {
        Spinner spinner = getDialog().findViewById(R.id.overlord_spinner);
        ArrayList<String> names = new ArrayList<>();
        for (Player player : mPlayers) {
            names.add(player.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),R.layout.support_simple_spinner_dropdown_item,names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setUpOutcomeView() {
        ArrayList<String> outcomes = new ArrayList<>();
        final LinearLayout layout = getDialog().findViewById(R.id.game_outcome_view);
        CheckBox unfinished = getDialog().findViewById(R.id.unfinished_checkbox);
        switch (mScenario.type){
            case Scenario.VERSUS:
                unfinished.setVisibility(View.VISIBLE);
                addPlaceLayouts();
                break;
            case Scenario.OVERLORD:
                unfinished.setVisibility(View.GONE);
                outcomes.add(getString(R.string.overlord));
                outcomes.add(getString(R.string.heroes));
                outcomes.add(getString(R.string.unfinished));
                setSpinnerFromArrayList(outcomes);
                break;
            case Scenario.COOP:
                unfinished.setVisibility(View.GONE);
                outcomes.add(getString(R.string.victory));
                outcomes.add(getString(R.string.defeat));
                outcomes.add(getString(R.string.unfinished));
                setSpinnerFromArrayList(outcomes);
        }
        setScrollViewSize(layout,4);

    }

    private void addPlaceLayouts() {
        final LinearLayout layout = getDialog().findViewById(R.id.game_outcome_view);
        layout.removeAllViews();
        for(int i = 0; i < mPlayers.size();i++){
            PlayerPlaceLayout playerPlaceLayout = createPlayerPlaceLayout(layout, i);
            layout.addView(playerPlaceLayout);
        }
    }

    private PlayerPlaceLayout createPlayerPlaceLayout(final LinearLayout layout , int id) {
        PlayerPlaceLayout playerPlaceLayout = new PlayerPlaceLayout(getContext());
        playerPlaceLayout.setTexts(id+1, (ArrayList<Player>) mPlayers);
        playerPlaceLayout.setId(id);
        playerPlaceLayout.setListener(new PlayerPlaceLayout.PlayerPlaceLayoutListener() {
            @Override
            public void onDrawClicked() {
                for(int i = 0, j = 1; i < layout.getChildCount();i++){
                    PlayerPlaceLayout child = (PlayerPlaceLayout) layout.getChildAt(i);
                    child.setPlace(j);
                    if(!child.isDraw())
                        j = i+2;
                }
            }

            @Override
            public void onPlayerSelected(String selectedPlayer, int id) {
                for(int i = 0; i < layout.getChildCount();i++){
                    PlayerPlaceLayout child = (PlayerPlaceLayout) layout.getChildAt(i);
                    if(selectedPlayer.equals(child.getSelectedPlayer()) && i != id){
                        child.setPlayerToNone();
                        return;
                    }
                }
            }
        });
        if(id == mPlayers.size()-1)
            playerPlaceLayout.hideDrawButton();
        return playerPlaceLayout;
    }

    private void setSpinnerFromArrayList(ArrayList<String> arrayList) {
        LinearLayout layout = getDialog().findViewById(R.id.game_outcome_view);
        Spinner outcomeSpinner = new Spinner(getContext());
        ArrayAdapter<String> outcomeAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.support_simple_spinner_dropdown_item, arrayList);
        outcomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        outcomeSpinner.setAdapter(outcomeAdapter);
        outcomeSpinner.setId(R.id.outcome_spinner);

        if(!(layout.getChildAt(1) instanceof Spinner))
            layout.removeAllViews();
        layout.addView(outcomeSpinner);
    }


    private void addRadioButtonToLinearLayout(@NonNull RadioGroup layout, String text) {
        RadioButton button = new RadioButton(getContext());
        setButtonAttributes(button ,text);
        layout.addView(button);
    }

    private void addCheckboxButtonToLinearLayout(@NonNull LinearLayout layout, String text) {
        CheckBox checkBox = new CheckBox(getContext());
        setButtonAttributes(checkBox,text);
        layout.addView(checkBox);
    }

    private void setButtonAttributes(@NonNull Button button, String text){
        button.setText(text);
        button.setTextSize(20);
        button.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        button.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void setScrollViewSize(@NonNull ViewGroup viewGroup, int maxChildNumber){
        final int MAX_HEIGHT = 800;
        ScrollView scrollView = (ScrollView) viewGroup.getParent();
        ViewGroup.LayoutParams layoutParams;
        if(viewGroup.getChildCount() > maxChildNumber){
            if(scrollView.getParent() instanceof LinearLayout)
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, MAX_HEIGHT);
            else
                layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, MAX_HEIGHT);
        }else {
            if(scrollView.getParent() instanceof LinearLayout)
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            else
                layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        }
        scrollView.setLayoutParams(layoutParams);
    }

    private void setTitle() {
        String newTitle = "";
        switch (mViewFlipper.getDisplayedChild()){
            case 0:
                newTitle = getString(R.string.chose_game);
                break;
            case 1:
                newTitle = getString(R.string.chose_scenario);
                break;
            case 2:
                newTitle = getString(R.string.chose_expansion);
                break;
            case 3:
                newTitle = getString(R.string.chose_players);
                break;
            case 4:
                newTitle = getString(R.string.overlord);
                break;
            case 5:
                newTitle = getString(R.string.chose_outcome);
                break;
            case 6:
                newTitle = getString(R.string.date);
                break;
            case 7:
                newTitle = getString(R.string.comment_title);
        }
        getDialog().setTitle(newTitle);
    }
}

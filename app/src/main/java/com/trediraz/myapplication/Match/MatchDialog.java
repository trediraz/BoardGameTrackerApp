package com.trediraz.myapplication.Match;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.trediraz.myapplication.Database.Expansion;
import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.Database.Player;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.List;

public class MatchDialog extends DialogFragment {

    private ViewFlipper mViewFlipper;
    private RadioGroup mRadioGameButtons;
    private RadioGroup mScenariosRadio;

    private List<Scenario> mAllScenarios;
    private List<Expansion> mAllExpansions;
    private Game mGame;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
        builder.setTitle(R.string.new_match)
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
            setScrollViewSize(mRadioGameButtons);
        }
    }

    private void setUpPlayersView() {
        List<Player> players = MainActivity.mBoardGameDao.getAllPlayers();
        LinearLayout playersView = getDialog().findViewById(R.id.players_view);
        playersView.removeAllViews();
        for(Player player : players){
            addCheckboxButtonToLinearLayout(playersView,player.name);
        }
        setScrollViewSize(playersView);
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
                if(mAllExpansions.size() == 0 && mAllScenarios.size() == 1 && !mGame.requireScenario)
                    mViewFlipper.setDisplayedChild(0);
                else if(mAllExpansions.size() == 0)
                    mViewFlipper.setDisplayedChild(1);
                else
                    mViewFlipper.showPrevious();
                break;
            default:
                mViewFlipper.showPrevious();
        }
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
                default:
                    mViewFlipper.showNext();
            }

        }else
            dismiss();
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
            mViewFlipper.showNext();
        }

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
        } else
            mViewFlipper.showNext();

    }

    private int countChecks(LinearLayout layout){
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
            setUpExpansionView();
        } else {
            for(Scenario scenario : mAllScenarios){
                String buttonText  = (scenario.name.equals(Scenario.DEFAULT_NAME)) ? getString(R.string.default_scenario) : scenario.name;
                addRadioButtonToLinearLayout(scenarioLayout,buttonText);
            }
            setScrollViewSize(scenarioLayout);
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
            setScrollViewSize(expansionsView);
        }

    }

    private void addRadioButtonToLinearLayout(RadioGroup layout, String text) {
        RadioButton button = new RadioButton(getContext());
        setButtonAttributes(button ,text);
        layout.addView(button);
    }

    private void addCheckboxButtonToLinearLayout(LinearLayout layout,String text) {
        CheckBox checkBox = new CheckBox(getContext());
        setButtonAttributes(checkBox,text);
        layout.addView(checkBox);
    }

    private void setButtonAttributes(Button button, String text){
        button.setText(text);
        button.setTextSize(20);
        button.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        button.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void setScrollViewSize(ViewGroup viewGroup){
        final int MAX_HEIGHT = 800;
        ScrollView scrollView = (ScrollView) viewGroup.getParent();
        if(viewGroup.getChildCount() > 9){
            if(scrollView.getParent() instanceof LinearLayout)
                scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, MAX_HEIGHT));
            else
                scrollView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, MAX_HEIGHT));
        }else {
            if(scrollView.getParent() instanceof LinearLayout)
                scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            else
                scrollView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        }
    }
}

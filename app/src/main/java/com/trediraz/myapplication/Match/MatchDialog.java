package com.trediraz.myapplication.Match;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.List;

public class MatchDialog extends DialogFragment {

    private ViewFlipper mViewFlipper;
    private RadioGroup mRadioGameButtons;
    private Game mGame;
    private List<Scenario> mScenarios;
    private List<Expansion> mExpansions;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),android.R.style.Theme_Material_Light_Dialog);
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

        setUpGameChoiceView();
    }

    private void setUpGameChoiceView() {
        mRadioGameButtons = getDialog().findViewById(R.id.game_button_group);
        if(mRadioGameButtons.getChildCount() == 0){
            List<String> gameNames = MainActivity.mBoardGameDao.getAllGameNames();
            for(String name : gameNames){
                RadioButton button = createRadioButton(name);
                mRadioGameButtons.addView(button);
            }
            setScrollViewSize( mRadioGameButtons);
        }
    }

    private void handlePreviousAction() {
        switch (mViewFlipper.getDisplayedChild()){
            case 0:
                dismiss();
                break;
            case 2:
                if(mScenarios.size() == 1 && !mGame.requireScenario){
                    mViewFlipper.setDisplayedChild(0);
                } else
                    mViewFlipper.showPrevious();
                break;
            case 3:
                if(mExpansions.size() == 0 && mScenarios.size() == 1 && !mGame.requireScenario)
                    mViewFlipper.setDisplayedChild(0);
                else if(mExpansions.size() == 0)
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
        RadioGroup scenariosRadio = getDialog().findViewById(R.id.scenarios_view);
        int checkScenarioId = scenariosRadio.getCheckedRadioButtonId();
        if(checkScenarioId == RadioGroup.NO_ID){
            Toast.makeText(getContext(),"Wybierz scenariusz",Toast.LENGTH_SHORT).show();
        } else{
            setUpExpansionView();
            mViewFlipper.showNext();
        }

    }

    private void setUpScenarioView() {
        RadioGroup scenarioLayout = getDialog().findViewById(R.id.scenarios_view);
        scenarioLayout.removeAllViews();
        mScenarios = MainActivity.mBoardGameDao.getScenariosByGameName(mGame.name);
        if(mScenarios.size() == 1 && !mGame.requireScenario){
            mViewFlipper.showNext();
            setUpExpansionView();
        } else {
            for(Scenario scenario : mScenarios){
                RadioButton button = createRadioButton( (scenario.name.equals(Scenario.DEFAULT_NAME)) ? "Brak" : scenario.name);
                scenarioLayout.addView(button);
            }
            setScrollViewSize(scenarioLayout);
        }
    }

    private void setUpExpansionView() {
        mExpansions = MainActivity.mBoardGameDao.getExpansionsByGameName(mGame.name);
        if(mExpansions.size() == 0)
            mViewFlipper.showNext();
        else {
            LinearLayout expansionsView = getDialog().findViewById(R.id.expansions_view);
            for(Expansion expansion : mExpansions){
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setText(expansion.name);
                expansionsView.addView(checkBox);
            }
        }

    }

    private RadioButton createRadioButton(String text){
        RadioButton button = new RadioButton(getContext());
        button.setText(text);
        button.setTextSize(20);
        button.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        button.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return button;
    }

    private void setScrollViewSize(RadioGroup radioGroup){
        final int MAX_HEIGHT = 800;
        ScrollView scrollView = (ScrollView) radioGroup.getParent();
        if(radioGroup.getChildCount() > 9){
            scrollView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT, MAX_HEIGHT));
        }else {
            scrollView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT, ScrollView.LayoutParams.WRAP_CONTENT));
        }
    }
}

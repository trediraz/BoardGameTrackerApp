package com.trediraz.myapplication.Game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.util.Objects;

public class GameDialog extends DialogFragment {

    public interface GameDialogInterface{
        void onGameAdded(Game game);
    }

    private ViewFlipper mViewFlipper;
    private boolean requiresScenario = false;

    private GameDialogInterface mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
        builder.setTitle(R.string.new_game)
                .setView(R.layout.game_dialog_layout);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (GameDialogInterface) getParentFragment();
        }catch ( ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewFlipper = getDialog().findViewById(R.id.create_game_flipper);
        Button nextButton = getDialog().findViewById(R.id.next_button);
        Button previousButton = getDialog().findViewById(R.id.previous_button);

        CheckBox requiresScenarioCheckBox = getDialog().findViewById(R.id.requires_scenario_checkbox);
        requiresScenarioCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                requiresScenario = b;
            }
        });

        Spinner spinner = getDialog().findViewById(R.id.game_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),R.array.game_types,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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

        final LinearLayout scenarioLayout = getDialog().findViewById(R.id.scenarios);
        if(requiresScenario) createNewScenarioView();

        Button addScenarioButton = getDialog().findViewById(R.id.add_scenario_button);
        addScenarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewScenarioView(scenarioLayout);
            }
        });

        final LinearLayout expansionsLayout = getDialog().findViewById(R.id.expansions);
        if(expansionsLayout.getChildCount() == 0) createNewExpansionView(expansionsLayout);

        final Button addExpansionButton = getDialog().findViewById(R.id.add_expansion_button);
        addExpansionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewExpansionView();
            }
        });
    }

    private void addNewScenarioView(LinearLayout scenarioLayout) {
        AddScenarioLayout last = (AddScenarioLayout) scenarioLayout.getChildAt(scenarioLayout.getChildCount()-1);
        LinearLayout linearLayout = getDialog().findViewById(R.id.scenarios);
        if(last == null) {
            createNewScenarioView();
        }
        else if(!containsEmptyNameScenario(linearLayout)){
            createNewScenarioView();
            if(scenarioLayout.getChildCount() == 2){
                ScrollView scrollView = getDialog().findViewById(R.id.scenarios_scroll_view);
                scrollView.setLayoutParams(new LinearLayout.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT,500));
            }
        } else {
            Toast.makeText(getContext(), R.string.empty_scenario_name,Toast.LENGTH_SHORT).show();
        }
    }

    private void createNewScenarioView() {
        LinearLayout scenarioLayout = getDialog().findViewById(R.id.scenarios);
        AddScenarioLayout addScenarioLayout = new AddScenarioLayout(getContext());
        scenarioLayout.addView(addScenarioLayout,scenarioLayout.getChildCount());
    }

    private void addNewExpansionView() {
        LinearLayout linearLayout = getDialog().findViewById(R.id.expansions);
        ScrollView scrollView = getDialog().findViewById(R.id.expansions_scroll_view);
        EditText lastEditText = (EditText) linearLayout.getChildAt(linearLayout.getChildCount()-1);
        if(!lastEditText.getText().toString().trim().equals("")) {
            createNewExpansionView(linearLayout);
            if(linearLayout.getChildCount() == 5){
                scrollView.setLayoutParams(new LinearLayout.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT,500));
            }
        }
    }

    private void createNewExpansionView(final LinearLayout layout) {
        final EditText editText = createExpansionEditText();
        layout.addView(editText,layout.getChildCount());
        editText.requestFocus();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(textView == layout.getChildAt(layout.getChildCount()-1)) {
                    addNewExpansionView();
                    return true;
                }
                else if(textView.getText().toString().trim().equals("")){
                    layout.removeView(textView);
                    layout.getChildAt(layout.getChildCount()-1).requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private EditText createExpansionEditText() {
        EditText editText = new EditText(getContext());
        editText.setHint(R.string.expansion_name);
        editText.setTextAlignment(EditText.TEXT_ALIGNMENT_CENTER);
        editText.setSingleLine(true);
        editText.setEms(10);
        return  editText;
    }

    private void handlePreviousAction(){

        int currentView = mViewFlipper.getDisplayedChild();
        boolean skipOptionalView = requiresScenario;
        final int OPTIONAL_VIEW = 1;

        if (currentView == 0) {
            dismiss();
        } else if(currentView == OPTIONAL_VIEW + 1 && skipOptionalView){
            mViewFlipper.setDisplayedChild(OPTIONAL_VIEW - 1);
        }
        else {
            mViewFlipper.showPrevious();
        }
        setTitle();
    }

    private void handleNextAction() {
        hideKeyboard();
        int currentView = mViewFlipper.getDisplayedChild();
        boolean skipOptionalView = requiresScenario;
        boolean isDataValid = true;
        final int OPTIONAL_VIEW = 1;

        int errorToastMessage = 0;
        switch (currentView){
            case 0:
                EditText gameNameText = getDialog().findViewById(R.id.game_name);
                String gameName = gameNameText.getText().toString().trim();
                if (gameName.equals("")) {
                    isDataValid = false;
                    errorToastMessage = R.string.no_name_toast;
                } else if(MainActivity.mBoardGameDao.getAllGameNames().contains(gameName)){
                    errorToastMessage =  R.string.game_name_used;
                    isDataValid = false;
                } else {
                    mViewFlipper.setDisplayedChild((skipOptionalView) ? (OPTIONAL_VIEW+1) : OPTIONAL_VIEW);
                }
                break;
            case 1:
                mViewFlipper.showNext();
                break;
            case 2:
                LinearLayout layout = getDialog().findViewById(R.id.scenarios);
                if (layout.getChildCount() == 0 && requiresScenario) {
                    isDataValid = false;
                    errorToastMessage = R.string.no_scenario;
                } else if (containsEmptyNameScenario(layout)) {
                    isDataValid = false;
                    errorToastMessage = R.string.empty_scenario_name;
                } else if(!isScenarioNameUnique(layout)){
                    isDataValid = false;
                    errorToastMessage = R.string.duplicate_scenario_name;

                } else {
                    mViewFlipper.showNext();
                }
                break;
            case 3:
                if(!isExpansionNameUnique()){
                    isDataValid = false;
                    errorToastMessage = R.string.duplicate_expansion_name;
                }
                else {
                mViewFlipper.showNext();
                }
                break;
            case 4:
                EditText minView = getDialog().findViewById(R.id.min_number_of_players);
                EditText maxView = getDialog().findViewById(R.id.max_number_of_players);
                String minText = minView.getText().toString();
                String maxText = maxView.getText().toString();

                if(minText.equals("") || maxText.equals("")){
                    isDataValid = false;
                    errorToastMessage = R.string.no_number_of_players;
                }else {
                    int min = Integer.parseInt(minText);
                    int max = Integer.parseInt(maxText);
                    if(min > max){
                        isDataValid = false;
                        errorToastMessage = R.string.min_grater_then_max;
                        break;
                    }
                    else {
                        createNewGame();
                        dismiss();
                    }
                }
                break;
        }
        if(isDataValid){
            setTitle();
        }
        else
            Toast.makeText(getContext(),errorToastMessage,Toast.LENGTH_SHORT).show();
    }

    private boolean isExpansionNameUnique() {
        LinearLayout layout = getDialog().findViewById(R.id.expansions);
        EditText comperedNameText,nameText;
        String compereName, name;

        for(int i = 0; i < layout.getChildCount();i++){
            comperedNameText = (EditText) layout.getChildAt(i);
            compereName = comperedNameText.getText().toString().trim();
            for(int j = i+1; j < layout.getChildCount();j++){
                nameText = (EditText) layout.getChildAt(j);
                name = nameText.getText().toString().trim();
                if(compereName.equals(name)){
                    nameText.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean containsEmptyNameScenario(LinearLayout layout) {
        for(int i = 0; i < layout.getChildCount();i++){
            AddScenarioLayout scenarioLayout = (AddScenarioLayout) layout.getChildAt(i);
            if(scenarioLayout.isEmpty()){
                return true;
            }
        }
        return false;
    }

    private boolean isScenarioNameUnique(LinearLayout layout) {
        for(int i = 0; i < layout.getChildCount();i++){
            AddScenarioLayout comperedLayout = (AddScenarioLayout) layout.getChildAt(i);
            for(int j = i+1; j < layout.getChildCount();j++){
                AddScenarioLayout scenarioLayout = (AddScenarioLayout) layout.getChildAt(j);
                if(comperedLayout.getScenarioName().equals(scenarioLayout.getScenarioName())){
                    scenarioLayout.requestFocus();
                    return false;
                }
            }
        }
        return true;
    }

    private void createNewGame() {

        Game newGame = new Game();

        EditText gameNameText = getDialog().findViewById(R.id.game_name);
        EditText min_number_of_players = getDialog().findViewById(R.id.min_number_of_players);
        EditText max_number_of_players = getDialog().findViewById(R.id.max_number_of_players);

        newGame.name = gameNameText.getText().toString().trim();
        newGame.min_number_of_players = Integer.parseInt(min_number_of_players.getText().toString());
        newGame.max_number_of_players = Integer.parseInt(max_number_of_players.getText().toString());
        newGame.requireScenario = requiresScenario;

        MainActivity.mBoardGameDao.insertGame(newGame);
        newGame.id = MainActivity.mBoardGameDao.getGameIdByName(newGame.name);

        mListener.onGameAdded(newGame);

        if(!requiresScenario) {
            Spinner spinner = getDialog().findViewById(R.id.game_type_spinner);
            Scenario defaultScenario = new Scenario();
            defaultScenario.name = Scenario.DEFAULT_NAME;
            defaultScenario.type = spinner.getSelectedItem().toString();
            defaultScenario.game_id = newGame.id;
            MainActivity.mBoardGameDao.insertScenario(defaultScenario);
        }

        LinearLayout scenarioLayout = getDialog().findViewById(R.id.scenarios);

        for(int i = 0; i < scenarioLayout.getChildCount();i++){
            AddScenarioLayout addScenarioLayout = (AddScenarioLayout) scenarioLayout.getChildAt(i);
            Scenario scenario = addScenarioLayout.getScenario();
            scenario.game_id = newGame.id;
            MainActivity.mBoardGameDao.insertScenario(scenario);
        }

        LinearLayout expansionLayout = getDialog().findViewById(R.id.expansions);
        for(int i = 0; i < expansionLayout.getChildCount();i++){
            String expansionName = ((EditText) expansionLayout.getChildAt(i)).getText().toString().trim();
            if(!expansionName.equals("")){
                Expansion expansion = new Expansion();
                expansion.name = expansionName;
                expansion.game_id = newGame.id;
                MainActivity.mBoardGameDao.insertExpansion(expansion);
            }
        }
    }


    private void setTitle() {
        String newTitle = "";
        switch (mViewFlipper.getDisplayedChild()){
            case 0:
                newTitle = getString(R.string.new_game);
                break;
            case 1:
                newTitle = getString(R.string.title_game_type);
                break;
            case 2:
                newTitle = getString(R.string.title_scenarios);
                break;
            case 3:
                newTitle = getString(R.string.title_expansions);
                break;
            case 4:
                newTitle = getString(R.string.title_number_of_players);
                break;
        }
        getDialog().setTitle(newTitle);
    }

    private void hideKeyboard() {
        View view = getDialog().getCurrentFocus();
        if(view != null){
            view.clearFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        }
    }
}

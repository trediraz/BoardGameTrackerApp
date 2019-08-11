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
import android.widget.RelativeLayout;
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
import com.trediraz.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameDialog extends DialogFragment {

    private ViewFlipper mViewFlipper;
    private boolean requiresScenario = false;
    private String gameName;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),android.R.style.Theme_Material_Light_Dialog);
        builder.setTitle(R.string.new_game)
                .setView(R.layout.game_dialog_layout);
        return builder.create();
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
                int currentView =  mViewFlipper.getDisplayedChild();
                boolean isDataValid = true;
                int errorToastMessage = 0;
                switch (currentView){
                    case 0:
                        EditText gameNameText = getDialog().findViewById(R.id.game_name);
                        gameName = gameNameText.getText().toString();
                        if (gameName.equals("")){
                            isDataValid = false;
                            errorToastMessage = R.string.no_name_toast;
                        }
                        break;
                    case 2:
                        LinearLayout layout = getDialog().findViewById(R.id.scenarios);
                        if (layout.getChildCount() == 0 && requiresScenario) {
                            isDataValid = false;
                            errorToastMessage = R.string.no_scenario;
                        } else if (containsEmptyNameScenario(layout)) {
                            isDataValid = false;
                            errorToastMessage = R.string.empty_scenario_name;
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
                            }
                        }
                        break;
                }
                if(isDataValid)
                    handleNextAction();
                else
                    Toast.makeText(getContext(),errorToastMessage,Toast.LENGTH_SHORT).show();
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
                addNewScenario(scenarioLayout);
            }
        });

        final LinearLayout expansionsLayout = getDialog().findViewById(R.id.expansions);
        createNewExpansionView(expansionsLayout);

        final Button addExpansionButton = getDialog().findViewById(R.id.add_expansion_button);
        addExpansionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewExpansionView();
            }
        });
    }

    private boolean containsEmptyNameScenario(LinearLayout layout) {
        for(int i = 0; i < layout.getChildCount();i++){
            AddScenarioLayout scenarioLayout = (AddScenarioLayout) layout.getChildAt(i);
            if(scenarioLayout.getScenarioName().equals("")){
                return true;
            }
        }
        return false;
    }

    private void addNewScenario(LinearLayout scenarioLayout) {
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
        if(!lastEditText.getText().toString().equals("")) {
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
                else if(textView.getText().toString().equals("")){
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
        final int OPTIONAL_VIEW = 1;

        if (currentView == mViewFlipper.getChildCount() - 1) {
            createNewGame();
            dismiss();
        } else if(currentView == (OPTIONAL_VIEW - 1) && skipOptionalView){
            mViewFlipper.setDisplayedChild(OPTIONAL_VIEW + 1);
        } else{
            mViewFlipper.showNext();
        }
        setTitle();
    }

    private void createNewGame() {

        Game game = new Game();
        EditText editText = getDialog().findViewById(R.id.name);
        EditText min = getDialog().findViewById(R.id.min_number_of_players);
        EditText max = getDialog().findViewById(R.id.max_number_of_players);
        game.id = 1;
        game.name = editText.getText().toString();
        game.min_number_of_players = Integer.parseInt(min.getText().toString());
        game.max_number_of_players = Integer.parseInt(max.getText().toString());
        game.requireScenario = requiresScenario;

        List<Expansion> expansions = new ArrayList<>();
        LinearLayout expansionLayout = getDialog().findViewById(R.id.expansions);
        for(int i = 0; i < expansionLayout.getChildCount();i++){
            String expansionName = ((EditText) expansionLayout.getChildAt(i)).getText().toString();
            if(!expansionName.equals("")){
                Expansion expansion = new Expansion();
                expansion.name = expansionName;
                expansion.game_id = game.id;
                expansions.add(expansion);
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

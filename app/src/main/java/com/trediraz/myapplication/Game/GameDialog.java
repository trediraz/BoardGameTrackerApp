package com.trediraz.myapplication.Game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import com.trediraz.myapplication.R;

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
        Button addExpansionButton = getDialog().findViewById(R.id.add_expansion_button);
        LinearLayout expansionsLayout = getDialog().findViewById(R.id.expansions);

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
                switch (currentView){
                    case 0:
                        EditText gameNameText = getDialog().findViewById(R.id.game_name);
                        gameName = gameNameText.getText().toString();
                        if (gameName.equals("")){
                            isDataValid = false;
                            Toast.makeText(getContext(), R.string.no_name_toast, Toast.LENGTH_SHORT).show();
                        }break;
                    case 1:
                        Toast.makeText(getContext(),"OPTIONAL",Toast.LENGTH_SHORT).show();
                }
                if(isDataValid)
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

        createNewExpansionView(expansionsLayout);

        addExpansionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewExpansionView();
            }
        });
    }

    private void createNewExpansionView(final LinearLayout layout) {
        final EditText editText = new EditText(getContext());
        editText.setHint(R.string.expansion_name);
        editText.setTextAlignment(EditText.TEXT_ALIGNMENT_CENTER);
        editText.setSingleLine(true);
        editText.setEms(10);
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

    private void handlePreviousAction(){

        int currentView = mViewFlipper.getDisplayedChild();
        boolean skipOptionalView = requiresScenario;
        final int OPTIONAL_VIEW = 1;

        if (currentView == 0) {
            dismiss();
        } else if(currentView == OPTIONAL_VIEW + 1 && skipOptionalView){
            mViewFlipper.setDisplayedChild(OPTIONAL_VIEW - 1);
        }
        else{
            mViewFlipper.showPrevious();
        }
    }

    private void handleNextAction() {
        hideKeyboard();
        int currentView = mViewFlipper.getDisplayedChild();
        boolean skipOptionalView = requiresScenario;
        final int OPTIONAL_VIEW = 1;

        if (currentView == mViewFlipper.getChildCount() - 1) {
            dismiss();
        } else if(currentView == (OPTIONAL_VIEW - 1) && skipOptionalView){
            mViewFlipper.setDisplayedChild(OPTIONAL_VIEW + 1);
        } else{
            mViewFlipper.showNext();
        }
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

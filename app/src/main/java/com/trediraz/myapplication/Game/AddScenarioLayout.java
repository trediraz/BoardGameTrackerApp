package com.trediraz.myapplication.Game;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.R;

import java.util.Objects;

public class AddScenarioLayout extends LinearLayout {

    private Spinner spinner;
    private EditText editText;
    private ImageView deleteButton;


    public AddScenarioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddScenarioLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.add_scenario_view,this);
        initComponents();
    }

    private void initComponents() {
        spinner = findViewById(R.id.spinner);
        editText = findViewById(R.id.scenario_type);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                textView.clearFocus();
                hideKeyboard(textView);
                return true;
            }
        });
        requestFocus();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),R.array.game_types,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        deleteButton = findViewById(R.id.delete_scenario_button);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                remove();
            }
        });
    }

    public String getScenarioName(){
        return editText.getText().toString().trim();
    }

    public boolean isEmpty(){
        return getScenarioName().equals("");
    }

    public Scenario getScenario(){
        Scenario scenario = new Scenario();
        scenario.name = getScenarioName();
        scenario.type = spinner.getSelectedItem().toString().trim();
        return scenario;
    }

    private void remove() {
        ViewParent layout = getParent();
        if (layout instanceof LinearLayout) {
            ((LinearLayout) layout).removeView(this);
            if (((LinearLayout) layout).getChildCount() == 1) {
                if (layout.getParent() instanceof ScrollView) {
                    ScrollView scrollView = (ScrollView) layout.getParent();
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ScrollView.LayoutParams.WRAP_CONTENT, ScrollView.LayoutParams.WRAP_CONTENT);
                    scrollView.setLayoutParams(params);
                }
            }
        }
    }
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    public void hideDelete(){
        deleteButton.setVisibility(GONE);
    }

}

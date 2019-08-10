package com.trediraz.myapplication.Game;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.trediraz.myapplication.R;

import java.util.Objects;

public class AddScenarioLayout extends LinearLayout {

    private Spinner spinner;
    private EditText editText;

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
        editText = findViewById(R.id.scenario_name);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),R.array.game_types,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public String getScenarioName(){
        return editText.getText().toString();
    }
}

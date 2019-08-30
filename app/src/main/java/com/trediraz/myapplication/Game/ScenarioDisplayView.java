package com.trediraz.myapplication.Game;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.R;

public class ScenarioDisplayView extends LinearLayout {

    private TextView name;
    private TextView type;

    public ScenarioDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScenarioDisplayView(Context context, Scenario scenario){
        super(context);
        init(context);
        setViews(scenario);
    }

    private void init(Context context) {
        inflate(context, R.layout.scenario_view,this);
        initComponents();
    }

    private void initComponents() {
        name = findViewById(R.id.scenario_name);
        type = findViewById(R.id.scenario_type);
    }

    public void setViews(Scenario scenario){
        name.setText(scenario.name);
        type.setText(scenario.type);
    }
}

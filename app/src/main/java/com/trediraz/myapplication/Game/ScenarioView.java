package com.trediraz.myapplication.Game;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.R;

public class ScenarioView extends LinearLayout {

    private TextView name;
    private TextView type;

    public ScenarioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScenarioView(Context context,Scenario scenario){
        super(context);
        init(context);
        name.setText(scenario.name);
        type.setText(scenario.type);
    }

    private void init(Context context) {
        inflate(context, R.layout.scenario_view,this);
        initComponents();
    }

    private void initComponents() {
        name = findViewById(R.id.scenario_name);
        type = findViewById(R.id.scenario_type);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
}

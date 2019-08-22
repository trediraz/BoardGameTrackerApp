package com.trediraz.myapplication.Game;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.trediraz.myapplication.Database.Expansion;
import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.List;
import java.util.Objects;

public class GameInfoFragment extends Fragment {

    private LinearLayout mExpansionViews;
    private LinearLayout mScenariosView;

    private List<Scenario> mScenarios;
    private List<Expansion> mExpansions;
    private Game mGame;

    public GameInfoFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView gameNameView = Objects.requireNonNull(getView()).findViewById(R.id.game_name);
        String gameName = GameInfoFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getGameName();
        gameNameView.setText(gameName);

        mScenarios = MainActivity.mBoardGameDao.getScenariosByGameName(gameName);
        mExpansions = MainActivity.mBoardGameDao.getExpansionsByGameName(gameName);

        mGame = MainActivity.mBoardGameDao.getGameByName(gameName);

        int min = mGame.min_number_of_players;
        int max = mGame.max_number_of_players;


        setGameTypeView();
        setScenarioView();
        setExpansionView();
        setPlayerNumberView(min, max);

        ImageView addScenario = getView().findViewById(R.id.add_scenario_button);
        addScenario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddScenarioDialog();
            }
        });

        ImageView addExpansion = getView().findViewById(R.id.add_expansion_button);
        addExpansion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddExpansionDialog();
            }
        });

    }

    private void setGameTypeView() {
        TextView gameTypeView = Objects.requireNonNull(getView()).findViewById(R.id.game_type_text);
        String gameType = null;
        for (Scenario scenario : mScenarios) {
            if(scenario.name.equals(Scenario.DEFAULT_NAME)) {
                gameType = scenario.type;
                break;
            }
        }
        LinearLayout parent = getView().findViewById(R.id.game_type_layout);
        if (gameType != null) {
            gameTypeView.setText(gameType);
            parent.setVisibility(View.VISIBLE);
        } else {
            parent.setVisibility(View.GONE);
        }

    }

    private void setScenarioView() {
        mScenariosView = Objects.requireNonNull(getView()).findViewById(R.id.scenarios);
        for (Scenario scenario : mScenarios) {
            if(!scenario.name.equals(Scenario.DEFAULT_NAME)) {
                ScenarioView scenarioView = new ScenarioView(getContext(),scenario);
                mScenariosView.addView(scenarioView);
           }
        }
        if(mScenariosView.getChildCount() > 1) {
            hideNoScenarioView();
        }
    }


    private void setExpansionView() {
        mExpansionViews = Objects.requireNonNull(getView()).findViewById(R.id.expansions);
        for (Expansion expansion : mExpansions) {
            TextView expansionView = createExpansionTextView(expansion.name);
            mExpansionViews.addView(expansionView);
        }
        if(mExpansionViews.getChildCount() > 1) {
            hideNoExpansionView();
        }
    }

    private void hideNoScenarioView() {
        TextView noScenarioText = Objects.requireNonNull(getView()).findViewById(R.id.no_scenario_text);
        noScenarioText.setVisibility(View.GONE);
    }

    private void hideNoExpansionView() {
        TextView noExpansionText = Objects.requireNonNull(getView()).findViewById(R.id.no_expansion_text);
        noExpansionText.setVisibility(View.GONE);
    }

    private void setPlayerNumberView(int min, int max) {
        TextView minView = Objects.requireNonNull(getView()).findViewById(R.id.min_players_number);
        TextView maxView = Objects.requireNonNull(getView()).findViewById(R.id.max_players_number);
        minView.setText(String.valueOf(min));
        maxView.setText(String.valueOf(max));
    }

    private void showAddScenarioDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
        final AddScenarioLayout asl = new AddScenarioLayout(getContext());
        asl.hideDelete();
        builder.setView(asl)
                .setTitle(R.string.add_scenario_title)
                .setNegativeButton(R.string.cancel,null)
                .setPositiveButton("OK", null);

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Scenario scenario : mScenarios) {
                    if(asl.getScenarioName().equals("")) {
                        Toast.makeText(getContext(),R.string.empty_scenario_name,Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(asl.getScenarioName().equals(scenario.name)) {
                        Toast.makeText(getContext(),R.string.duplicate_scenario_name,Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
               addNewScenario(asl.getScenario());
                dialog.dismiss();
            }
        });
    }

    private void showAddExpansionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
        final EditText editText = createExpansionEditText();
        builder.setTitle(R.string.add_expansion_title)
                .setView(editText)
                .setNegativeButton(R.string.cancel,null)
                .setPositiveButton("OK",null);

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expansionName = editText.getText().toString().trim();
                if(expansionName.equals("")){
                    Toast.makeText(getContext(),R.string.empty_expansion_name,Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Expansion expansion : mExpansions) {
                    if(expansionName.equals(expansion.name)){
                        Toast.makeText(getContext(),R.string.duplicate_expansion_name,Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                addNewExpansion(expansionName);
                dialog.dismiss();
            }
        });
    }

    private EditText createExpansionEditText() {
        EditText editText = new EditText(getContext());
        editText.setHint(R.string.expansion_name);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        editText.setEms(10);
        return editText;
    }

    private void addNewScenario(Scenario scenario) {
        scenario.game_id = mGame.id;
        MainActivity.mBoardGameDao.insertScenario(scenario);
        mScenarios.add(scenario);
        ScenarioView scenarioView = new ScenarioView(getContext(),scenario);
        mScenariosView.addView(scenarioView);
        hideNoScenarioView();
    }

    private void addNewExpansion(String expansionName) {
        Expansion expansion = new Expansion();
        expansion.name = expansionName;
        expansion.game_id = mGame.id;

        mExpansions.add(expansion);
        MainActivity.mBoardGameDao.insertExpansion(expansion);

        TextView expansionView = createExpansionTextView(expansion.name);
        mExpansionViews.addView(expansionView);
        hideNoExpansionView();
    }

    private TextView createExpansionTextView(String name) {
        TextView expansionView = new TextView(getContext());
        expansionView.setText(name);
        expansionView.setTextAppearance(R.style.PrimaryText);
        return expansionView;
    }
}

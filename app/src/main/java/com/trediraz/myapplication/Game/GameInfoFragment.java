package com.trediraz.myapplication.Game;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

        setItemListVisibilityListener(R.id.scenario_title,R.id.scenarios,R.id.scenarios_divider);
        setItemListVisibilityListener(R.id.expansions_title,R.id.expansions,R.id.expansions_divider);
        setItemListVisibilityListener(R.id.player_title,R.id.number_of_players,R.id.players_divider);

    }

    private void setItemListVisibilityListener(int titleId,int layoutId, int dividerId){
        final View layout = Objects.requireNonNull(getView()).findViewById(layoutId);
        final View divider = getView().findViewById(dividerId);
        TextView title = getView().findViewById(titleId);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemListVisibility(layout,divider);
            }
        });
    }

    private void setItemListVisibility(View layout, View divider) {
        if(layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
        }
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
        for (final Scenario scenario : mScenarios) {
            if(!scenario.name.equals(Scenario.DEFAULT_NAME)) {
                final ScenarioDisplayView scenarioDisplayView = new ScenarioDisplayView(getContext(),scenario);
                scenarioDisplayView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showEditScenarioDialog(scenario,scenarioDisplayView);
                    }
                });
                mScenariosView.addView(scenarioDisplayView);
           }
        }
        if(mScenariosView.getChildCount() > 1) {
            hideNoScenarioView();
        }
    }


    private void setExpansionView() {
        mExpansionViews = Objects.requireNonNull(getView()).findViewById(R.id.expansions);
        for (final Expansion expansion : mExpansions) {
            TextView expansionView = createExpansionTextView(expansion);
            mExpansionViews.addView(expansionView);
        }
        if(mExpansionViews.getChildCount() > 1) {
            setNoExpansionViewVisibility(false);
        }
    }

    private void hideNoScenarioView() {
        TextView noScenarioText = Objects.requireNonNull(getView()).findViewById(R.id.no_scenario_text);
        noScenarioText.setVisibility(View.GONE);
    }

    private void setNoExpansionViewVisibility(boolean visibility) {
        TextView noExpansionText = Objects.requireNonNull(getView()).findViewById(R.id.no_expansion_text);
        noExpansionText.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    private void setPlayerNumberView(int min, int max) {
        TextView minView = Objects.requireNonNull(getView()).findViewById(R.id.min_players_number);
        TextView maxView = Objects.requireNonNull(getView()).findViewById(R.id.max_players_number);
        minView.setText(String.valueOf(min));
        maxView.setText(String.valueOf(max));
    }

    private void showAddScenarioDialog() {
        final EditScenarioView esv = new EditScenarioView(getContext());
        esv.hideDelete();

        final AlertDialog dialog = buildDialog(getString(R.string.add_scenario_title),esv);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isScenarioDataValid(esv)) {
                    addNewScenario(esv.getScenario());
                    dialog.dismiss();
                }
            }
        });
    }

    private void showEditScenarioDialog(final Scenario scenario, final ScenarioDisplayView scenarioDisplayView) {
        final EditScenarioView esv = new EditScenarioView(getContext(), scenario);
        final AlertDialog dialog = buildDialog(getString(R.string.game_info_scenario_title),esv);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isScenarioDataValid(esv)) {
                    MainActivity.mBoardGameDao.updateScenario(esv.getScenario());
                    scenarioDisplayView.setViews(scenario);
                    dialog.dismiss();
                }
            }
        });
    }

    private void showAddExpansionDialog() {
        final EditText editText = createExpansionEditText();
        final AlertDialog dialog = buildDialog(getString(R.string.add_expansion_title),editText);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expansionName = editText.getText().toString().trim();
                if(isExpansionNameValid(expansionName)) {
                    addNewExpansion(expansionName);
                    dialog.dismiss();
                }
            }
        });
    }


    private void showEditExpansionDialog(final Expansion expansion, final TextView textView) {
        final EditText editText = createExpansionEditText();
        editText.setText(expansion.name);
        final AlertDialog dialog = buildDialog(getString(R.string.new_expan_name),editText);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expansionName = editText.getText().toString().trim();
                if(isExpansionNameValid(expansionName)) {
                    expansion.name = expansionName;
                    textView.setText(expansionName);
                    MainActivity.mBoardGameDao.updateExpansion(expansion);
                    dialog.dismiss();
                }
            }
        });
    }

    private boolean isScenarioDataValid(EditScenarioView esv) {
        for (Scenario scenario : mScenarios) {
            if(esv.getScenarioName().equals("")) {
                Toast.makeText(getContext(),R.string.empty_scenario_name,Toast.LENGTH_LONG).show();
                return false;
            }
            if(esv.getScenarioName().equals(scenario.name) && esv.getScenario() != scenario) {
                Toast.makeText(getContext(),R.string.duplicate_scenario_name,Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean isExpansionNameValid(String expansionName) {
        if(expansionName.equals("")){
            Toast.makeText(getContext(),R.string.empty_expansion_name,Toast.LENGTH_SHORT).show();
            return false;
        }
        for (Expansion expansion : mExpansions) {
            if(expansionName.equals(expansion.name)){
                Toast.makeText(getContext(),R.string.duplicate_expansion_name,Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private AlertDialog buildDialog(String title, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
        builder.setTitle(title)
                .setView(view)
                .setNegativeButton(R.string.cancel,null)
                .setPositiveButton("OK",null);
        return builder.create();
    }

    private EditText createExpansionEditText() {
        EditText editText = new EditText(getContext());
        editText.setHint(R.string.expansion_name);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        editText.setEms(10);
        return editText;
    }

    private void addNewScenario(final Scenario scenario) {
        scenario.game_id = mGame.id;
        MainActivity.mBoardGameDao.insertScenario(scenario);
        mScenarios.add(scenario);
        final ScenarioDisplayView scenarioDisplayView = new ScenarioDisplayView(getContext(),scenario);
        scenarioDisplayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditScenarioDialog(scenario, scenarioDisplayView);
            }
        });
        mScenariosView.addView(scenarioDisplayView);
        hideNoScenarioView();
    }

    private void addNewExpansion(String expansionName) {
        final Expansion expansion = new Expansion();
        expansion.name = expansionName;
        expansion.game_id = mGame.id;

        mExpansions.add(expansion);
        MainActivity.mBoardGameDao.insertExpansion(expansion);

        TextView expansionView = createExpansionTextView(expansion);
        mExpansionViews.addView(expansionView);
        setNoExpansionViewVisibility(false);
    }

    private TextView createExpansionTextView(final Expansion expansion) {
        TextView expansionView = new TextView(getContext());
        expansionView.setText(expansion.name);
        expansionView.setTextAppearance(R.style.PrimaryText);
        expansionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditExpansionDialog(expansion, (TextView) view);
            }
        });
        expansionView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
                builder.setMessage(getString(R.string.delete_expansion) + " " + expansion.name + "?")
                        .setNegativeButton(R.string.cancel,null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.mBoardGameDao.deleteExpansion(expansion);
                                mExpansions.remove(expansion);
                                mExpansionViews.removeView(view);
                                if(mExpansionViews.getChildCount() == 1)
                                    setNoExpansionViewVisibility(true);
                            }
                        })
                        .show();
                return true;
            }
        });
        return expansionView;
    }
}

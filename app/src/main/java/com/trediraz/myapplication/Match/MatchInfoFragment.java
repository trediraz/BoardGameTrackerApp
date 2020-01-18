package com.trediraz.myapplication.Match;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trediraz.myapplication.Database.Expansion;
import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.Database.Match;
import com.trediraz.myapplication.Database.MatchExpansion;
import com.trediraz.myapplication.Database.PlayedIn;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MatchInfoFragment extends Fragment {


    private Match mMatch;
    private LinearLayout expansionsListView;
    private List<Expansion> matchExpansions;

    public MatchInfoFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView gameName = Objects.requireNonNull(getView()).findViewById(R.id.game_name);
        TextView scenarioView = getView().findViewById(R.id.scenario);
        TextView comment = getView().findViewById(R.id.comments);
        TextView outcome = getView().findViewById(R.id.outcome);
        TextView date = getView().findViewById(R.id.date);

        int matchId = MatchInfoFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getMatchId();
        mMatch = MainActivity.mBoardGameDao.getMatchById(matchId);
        Game game = MainActivity.mBoardGameDao.getGameById(mMatch.game_id);
        Scenario scenario = MainActivity.mBoardGameDao.getScenarioById(mMatch.scenario_id);

        gameName.setText(game.name);

        LinearLayout scenarioLayout = getView().findViewById(R.id.scenario_layout);
        if(scenario.name.equals(Scenario.DEFAULT_NAME))
            scenarioLayout.setVisibility(View.GONE);
        else
            scenarioView.setText(scenario.name);
        comment.setText(mMatch.comments);
        outcome.setText(mMatch.outcome);
        date.setText(mMatch.date);

        LinearLayout players = getView().findViewById(R.id.players_list);
        List<PlayedIn> playedIns = MainActivity.mBoardGameDao.getAllPlayedInMatchById(mMatch.id);
        Collections.sort(playedIns,new SortPlayedIns());
        for (PlayedIn playedIn : playedIns) {
            String playerName = MainActivity.mBoardGameDao.getPlayerNameById(playedIn.player_id);
            if(scenario.type.equals(Scenario.COOP))
                addTextView(players,playerName);
            else {
                PlayerView playerView = new PlayerView(getContext(),playerName,playedIn);
                players.addView(playerView);
            }
        }

        expansionsListView = getView().findViewById(R.id.expansions_list);
        matchExpansions = MainActivity.mBoardGameDao.getExpansionsByMatchId(mMatch.id);
        for (Expansion expansion : matchExpansions) {
            addTextView(expansionsListView,expansion.name);
        }

        LinearLayout expansionsView = getView().findViewById(R.id.expansions_layout);
        ImageView expansionsButton = getView().findViewById(R.id.edit_expansion_button);
        List<Expansion> gameExpansions = MainActivity.mBoardGameDao.getExpansionNamesGameId(mMatch.game_id);
        if(gameExpansions.isEmpty())
            expansionsView.setVisibility(View.GONE);

        boolean[] checkedItems = createContainsBoolArr(matchExpansions,gameExpansions);
        expansionsButton.setOnClickListener(view -> showChooseExpansionsDialog(checkedItems,gameExpansions));

        setItemListVisibilityListener(R.id.game_title,R.id.game_name,R.id.game_divider);
        setItemListVisibilityListener(R.id.scenario_title,R.id.scenario,R.id.scenarios_divider);
        setItemListVisibilityListener(R.id.players_title,R.id.players_list,R.id.players_divider);
        setVisibilityListenerWithCondition(R.id.comments_title,R.id.comments,R.id.comments_divider, mMatch.comments.trim().equals(""));
        setVisibilityListenerWithCondition(R.id.expansions_title,R.id.expansions_list,R.id.game_divider,matchExpansions.size() == 0);

        View dataChangeButton = getView().findViewById(R.id.date_layout);
        dataChangeButton.setOnClickListener(view -> {
            DatePicker picker = createDatePicker();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
            builder.setNegativeButton(R.string.cancel,null)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        mMatch.date = getDateFromDatePicker(picker);
                        date.setText(mMatch.date);
                        MainActivity.mBoardGameDao.updateMatch(mMatch);
                    })
                    .setView(picker)
                    .show();
        });

        ImageView editCommentsButton = getView().findViewById(R.id.edit_comments_view);
        editCommentsButton.setOnClickListener(view -> {
            EditText textView = (EditText) View.inflate(getContext(),R.layout.comment_edit_text_layout ,null);
            textView.setText(mMatch.comments);
            FrameLayout frameLayout = new FrameLayout(Objects.requireNonNull(getContext()));
            frameLayout.setPadding(dpToPx(20),dpToPx(20),dpToPx(20),dpToPx(20));
            frameLayout.addView(textView, ViewGroup.LayoutParams.WRAP_CONTENT,dpToPx(200));
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
            builder.setTitle(R.string.comment_title)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        String newComments = textView.getText().toString().trim();
                        if(!mMatch.comments.equals(newComments)) {
                            mMatch.comments = newComments;
                            MainActivity.mBoardGameDao.updateMatch(mMatch);
                            comment.setText(mMatch.comments);
                            setVisibilityListenerWithCondition(R.id.comments_title,R.id.comments,R.id.comments_divider,mMatch.comments.isEmpty());
                        }
                    })
                    .setNegativeButton(R.string.cancel,null)
                    .setView(frameLayout)
                    .show();
        });
    }

    private DatePicker createDatePicker() {
        DatePicker picker = new DatePicker(getContext());
        String[] values = mMatch.date.split("-");
        picker.updateDate(Integer.parseInt(values[0]),Integer.parseInt(values[1])-1,Integer.parseInt(values[2]));
        picker.setMaxDate(new Date().getTime());
        return picker;
    }

    private void showChooseExpansionsDialog(boolean[] checkedItems, List<Expansion> gameExpansions) {
        boolean[] initialChecked = checkedItems.clone();
        String[] expansionNames = gameExpansions.stream().map(p -> p.name).toArray(String[]::new);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
        builder.setTitle(R.string.choose_expansions)
                .setMultiChoiceItems(expansionNames, checkedItems, (dialogInterface, i, b) -> {})
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    for(int j = 0; j < initialChecked.length; j++) {
                        if (initialChecked[j] != checkedItems[j]) {
                            Expansion expansion = gameExpansions.get(j);
                            if (checkedItems[j])
                                addExpansion(expansion);
                            else
                                deleteMatchExpansion(expansion);
                        }
                    }
                    setVisibilityListenerWithCondition(R.id.expansions_title,R.id.expansions_list,R.id.game_divider,matchExpansions.size() == 0);
                })
                .setNegativeButton(R.string.cancel,null)
                .show();
    }

    private void addExpansion(Expansion expansion) {
        matchExpansions.add(expansion);
        addMatchExpansion(expansion);
        addMatchTextView(expansion);
    }

    private void addMatchExpansion(Expansion expansion) {
        MatchExpansion mE = new MatchExpansion();
        mE.expansion_id = expansion.id;
        mE.match_id = mMatch.id;
        MainActivity.mBoardGameDao.insertMatchExpansion(mE);
    }

    private void addMatchTextView(Expansion expansion) {
        TextView textView = new TextView(getContext());
        textView.setTextAppearance(R.style.PrimaryText);
        textView.setText(expansion.name);
        for(int i = 0; i < expansionsListView.getChildCount(); i++){
            TextView child = (TextView) expansionsListView.getChildAt(i);
            if(child.getText().toString().compareTo(expansion.name) > 0){
                expansionsListView.addView(textView,i);
                return;
            }
        }
        expansionsListView.addView(textView);
    }

    private void deleteMatchExpansion(Expansion expansion) {
        matchExpansions.remove(expansion);
        MainActivity.mBoardGameDao.deleteMatchExpansion(mMatch.id,expansion.id);
        for (int i = 0; i < expansionsListView.getChildCount(); i++) {
            TextView textView = (TextView) expansionsListView.getChildAt(i);
            if (textView.getText().equals(expansion.name)){
                expansionsListView.removeView(textView);
                break;
            }
        }
    }

    private boolean[] createContainsBoolArr(List<Expansion> values, List<Expansion> items) {
        boolean[] result = new boolean[items.size()];
        for(int i = 0; i < items.size(); i++) {
            boolean contains = false;
            for(int j = 0; j < values.size(); j++)
               if(values.get(j).id == items.get(i).id){
                   contains = true;
                   break;
               }
            result[i] = contains;
        }
        return result;
    }

    private void setVisibilityListenerWithCondition(int titleId, int viewId, int divId, boolean condition) {
        View divider = Objects.requireNonNull(getView()).findViewById(viewId);
        View view = Objects.requireNonNull(getView()).findViewById(divId);
        View title = getView().findViewById(titleId);

        if(condition){
            hideItemList(view,divider);
            title.setClickable(false);
        } else {
            showItemList(view,divider);
            setItemListVisibilityListener(title,view,divider);
        }
    }


    private void addTextView(LinearLayout players, String playerName) {
        TextView textView = new TextView(getContext());
        textView.setText(playerName);
        textView.setTextAppearance(R.style.PrimaryText);
        players.addView(textView);
    }

    private void setItemListVisibilityListener(int titleId,int containerId, int dividerId){
        View title = Objects.requireNonNull(getView()).findViewById(titleId);
        View container = getView().findViewById(containerId);
        View divider = getView().findViewById(dividerId);
        setItemListVisibilityListener(title,container,divider);
    }


    private void setItemListVisibilityListener(View title, final View container, final View divider){
        title.setOnClickListener(view -> setItemListVisibility(container,divider));
    }

    private void setItemListVisibility(View layout, View divider) {
        if(layout.getVisibility() == View.VISIBLE) {
            hideItemList(layout, divider);
        } else {
            showItemList(layout, divider);
        }
    }

    private void showItemList(View layout, View divider) {
        layout.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);
    }

    private void hideItemList(View layout, View divider) {
        layout.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
    }

    class SortPlayedIns implements Comparator<PlayedIn>{

        @Override
        public int compare(PlayedIn playedIn, PlayedIn t1) {
            if(playedIn.role != null)
                if(playedIn.role.equals(Scenario.OVERLORD))
                    return -1;
            if(t1.role != null)
                if(t1.role.equals(Scenario.OVERLORD))
                    return 1;
            if(t1.place == PlayedIn.NO_PLACE)
                return -1;
            if(playedIn.place == PlayedIn.NO_PLACE)
                return 1;
            return playedIn.place - t1.place;
        }
    }

    private String getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    private int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}

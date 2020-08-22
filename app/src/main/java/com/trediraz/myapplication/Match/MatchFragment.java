package com.trediraz.myapplication.Match;


import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trediraz.myapplication.Database.Game;
import com.trediraz.myapplication.Database.Match;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MatchFragment extends Fragment implements MatchDialog.MatchDialogListener {

    private MatchListAdapter mAdapter;
    private Filters mFilters;
    private TextView noMatchesText;
    private ListView matchViews;
    private List<Match> mMatches;

    public MatchFragment() {
       this.setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFilters = MatchFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getFilters();

        mMatches = MainActivity.mBoardGameDao.getAllMatches();

        if(mFilters == null){
            mFilters = new Filters();
        }

        mMatches = mMatches.stream().filter(x -> mFilters.isRight(x)).collect(Collectors.toList());

        matchViews = Objects.requireNonNull(getView()).findViewById(R.id.match_list_view);
        mAdapter = new MatchListAdapter(getActivity(), mMatches);
        matchViews.setAdapter(mAdapter);
        matchViews.setOnItemClickListener((adapterView, view, i, l) -> {
            Match match = (Match) adapterView.getItemAtPosition(i);
            MatchFragmentDirections.GoToMachInfo action = MatchFragmentDirections.goToMachInfo(match.id);
            Navigation.findNavController(view).navigate(action);
        });
        matchViews.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Match match = (Match) adapterView.getItemAtPosition(i);
            showDeleteDialog(match);
            return true;
        });

        noMatchesText = getView().findViewById(R.id.no_matches_text);
        setNoMatchesTextVisibility();

        FloatingActionButton addNewMatchButton = Objects.requireNonNull(getView()).findViewById(R.id.add_match_button);
        addNewMatchButton.setOnClickListener(view -> showAddDialog());
    }

    private void showDeleteDialog(final Match match) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
        builder.setMessage(R.string.delete_match_dialog_message)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    MainActivity.mBoardGameDao.deleteMatch(match);
                    mAdapter.deleteMatch(match);
                    setNoMatchesTextVisibility();
                })
                .setNegativeButton(R.string.cancel,null)
                .show();
    }

    private void showAddDialog() {
        int numberOfGames = MainActivity.mBoardGameDao.countGames();
        int numberOfPlayers = MainActivity.mBoardGameDao.countPlayers();
        if(numberOfGames == 0 || numberOfPlayers ==0) {
            String toastMessage = (numberOfGames == 0) ? getString(R.string.no_games) : getString(R.string.no_players);
            Toast.makeText(getContext(),toastMessage,Toast.LENGTH_SHORT).show();
        } else {
            DialogFragment dialogName = new MatchDialog();
            dialogName.setCancelable(false);
            dialogName.show(Objects.requireNonNull(getChildFragmentManager()),"dialog");
        }
    }

    private void setNoMatchesTextVisibility() {
        if(mMatches.isEmpty()){
            matchViews.setVisibility(View.INVISIBLE);
            noMatchesText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.filter_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action){
            MatchFragmentDirections.GoToFilters action = MatchFragmentDirections.goToFilters(mFilters);
            Navigation.findNavController(getView()).navigate(action);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMatchAdded(Match match) {
        if(mFilters.isRight(match)){
            mAdapter.addMatch(match);
            matchViews.setVisibility(View.VISIBLE);
            noMatchesText.setVisibility(View.INVISIBLE);
        }
    }
}

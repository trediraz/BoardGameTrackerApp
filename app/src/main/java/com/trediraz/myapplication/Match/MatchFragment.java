package com.trediraz.myapplication.Match;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trediraz.myapplication.Database.Match;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.List;
import java.util.Objects;

public class MatchFragment extends Fragment implements MatchDialog.MatchDialogListener {

    private MatchListAdapter mAdapter;

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

        String gameName = MatchFragmentArgs.fromBundle(Objects.requireNonNull(getArguments())).getGameName();
        Toast.makeText(getContext(),gameName,Toast.LENGTH_SHORT).show();

        List<Match> mMatches = MainActivity.mBoardGameDao.getAllMatches();

        ListView matchViews = Objects.requireNonNull(getView()).findViewById(R.id.match_list_view);
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

        Button filterButton = getView().findViewById(R.id.filter_button);
        filterButton.setOnClickListener(view -> {
            Navigation.findNavController(getView()).navigate(R.id.go_to_filters);
        });

        FloatingActionButton addNewMatchButton = Objects.requireNonNull(getView()).findViewById(R.id.add_match_button);
        addNewMatchButton.setOnClickListener(view -> showAddDialog());
    }

    private void showDeleteDialog(final Match match) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogStyle);
        builder.setMessage(R.string.delete_match_dialog_message)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    MainActivity.mBoardGameDao.deleteMatch(match);
                    mAdapter.deleteMatch(match);
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

    @Override
    public void onMatchAdded(Match match) {
        mAdapter.addMatch(match);
    }
}

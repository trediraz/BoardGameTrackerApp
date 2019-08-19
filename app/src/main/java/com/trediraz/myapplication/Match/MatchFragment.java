package com.trediraz.myapplication.Match;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trediraz.myapplication.Database.BoardGameDao;
import com.trediraz.myapplication.Database.Match;
import com.trediraz.myapplication.Game.GameDialog;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.List;
import java.util.Objects;

public class MatchFragment extends Fragment {


    public MatchFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<Match> matches = MainActivity.mBoardGameDao.getAllMatches();

        ListView matchesView = Objects.requireNonNull(getView()).findViewById(R.id.match_list_view);
        MatchListAdapter adapter = new MatchListAdapter(getActivity(),matches);
        matchesView.setAdapter(adapter);

        FloatingActionButton addNewMatchButton = Objects.requireNonNull(getView()).findViewById(R.id.add_match_button);
        addNewMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: add no game or player validation:

                DialogFragment dialogName = new MatchDialog();
                dialogName.setCancelable(false);
                dialogName.show(Objects.requireNonNull(getChildFragmentManager()),"dialog");
            }
        });
    }
}

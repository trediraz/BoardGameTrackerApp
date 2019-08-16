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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trediraz.myapplication.Database.BoardGameDao;
import com.trediraz.myapplication.Game.GameDialog;
import com.trediraz.myapplication.R;

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

package com.trediraz.myapplication.Match;

import android.content.Context;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.trediraz.myapplication.Database.PlayedIn;
import com.trediraz.myapplication.R;

public class PlayerView extends ConstraintLayout {

    TextView name;
    TextView detailInfo;

    public PlayerView(Context context) {
        super(context);
        init(context);
    }

    public PlayerView(Context context, String name, PlayedIn playedIn){
        super(context);
        init(context);
        this.name.setText(name);
        setDetailInfo(playedIn);
    }

    private void init(Context context){
        inflate(context, R.layout.player_view,this);
        initComponents();
    }

    private void initComponents() {
        name = findViewById(R.id.player_name);
        name.setTextAppearance(R.style.PrimaryText);
        detailInfo = findViewById(R.id.detail_info);
        detailInfo.setTextAppearance(R.style.PrimaryText);
    }

    private void setDetailInfo(PlayedIn playedIn) {
        if(playedIn.place != PlayedIn.NO_PLACE)
            detailInfo.setText(getResources().getString(R.string.place,playedIn.place));
        else if(playedIn.role != null)
            detailInfo.setText(playedIn.role);
        else detailInfo.setVisibility(GONE);
    }

}

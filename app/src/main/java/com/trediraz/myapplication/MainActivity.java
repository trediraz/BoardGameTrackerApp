package com.trediraz.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.trediraz.myapplication.Database.AppDatabase;
import com.trediraz.myapplication.Database.BoardGameDao;

public class MainActivity extends AppCompatActivity {

    public static BoardGameDao mBoardGameDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavController navController = Navigation.findNavController(this,R.id.fragment);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.games_fragment, R.id.match_frament, R.id.players_fragment, R.id.stats_fragment)
                .build();

        NavigationUI.setupWithNavController(bottomNavigationView,navController);
        NavigationUI.setupActionBarWithNavController(this,navController, appBarConfiguration);

        AppDatabase db = Room.databaseBuilder(this,AppDatabase.class,"board-games").allowMainThreadQueries().build();
        mBoardGameDao = db.boardGameDao();
    }
}

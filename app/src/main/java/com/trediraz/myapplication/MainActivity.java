package com.trediraz.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.trediraz.myapplication.Database.AppDatabase;
import com.trediraz.myapplication.Database.BoardGameDao;

public class MainActivity extends AppCompatActivity {

    public static BoardGameDao mBoardGameDao;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavController navController = Navigation.findNavController(this,R.id.fragment);
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.games_fragment, R.id.match_frament, R.id.players_fragment, R.id.stats_fragment)
                .build();

        NavigationUI.setupWithNavController(bottomNavigationView,navController);
        NavigationUI.setupActionBarWithNavController(this,navController, appBarConfiguration);

        AppDatabase db = Room.databaseBuilder(this,AppDatabase.class,"board-games").allowMainThreadQueries().build();
        mBoardGameDao = db.boardGameDao();

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (isBottomNavDestination(destination.getId())){
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }else {
                    bottomNavigationView.setVisibility(View.GONE);
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,R.id.fragment);
        return NavigationUI.navigateUp(navController,appBarConfiguration) || super.onSupportNavigateUp();
    }

    private boolean isBottomNavDestination(int id) {
        return (id == R.id.players_fragment || id == R.id.games_fragment || id == R.id.match_frament || id == R.id.stats_fragment);
    }
}

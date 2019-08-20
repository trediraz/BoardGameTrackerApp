package com.trediraz.myapplication.Match;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.trediraz.myapplication.Database.Match;
import com.trediraz.myapplication.Database.Scenario;
import com.trediraz.myapplication.MainActivity;
import com.trediraz.myapplication.R;

import java.util.List;

public class MatchListAdapter extends BaseAdapter {

    private List<Match> mMatches;
    private Activity mActivity;

    static class ViewHolder{
        TextView gameName;
        TextView gameOutcome;
        TextView date;
        TextView scenario;
    }

    MatchListAdapter(Activity activity, List<Match> matches){
        mActivity = activity;
        mMatches = matches;
    }

    @Override
    public int getCount() {
        return mMatches.size();
    }

    @Override
    public Match getItem(int i) {
        return mMatches.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.match_list_view_item,viewGroup,false);
            final ViewHolder holder = new ViewHolder();
            holder.gameName = view.findViewById(R.id.game_name);
            holder.gameOutcome = view.findViewById(R.id.outcome);
            holder.date = view.findViewById(R.id.date);
            holder.scenario = view.findViewById(R.id.scenario);
            view.setTag(holder);
        }

        final Match match = getItem(i);
        final ViewHolder holder = (ViewHolder) view.getTag();

        String gameName = MainActivity.mBoardGameDao.getGameNameById(match.game_id);
        String scenarioName = MainActivity.mBoardGameDao.getScenarioNameById(match.scenario_id);
        if(scenarioName.equals(Scenario.DEFAULT_NAME))
            scenarioName = "-";
        holder.gameName.setText(gameName);
        holder.gameOutcome.setText(match.outcome);
        holder.date.setText(match.date);
        holder.scenario.setText(scenarioName);

        return view;
    }
}

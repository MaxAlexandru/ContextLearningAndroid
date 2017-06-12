package com.max.app.contextlearning;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

public class ActivitiesActivity extends AppCompatActivity {

    private ListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenarios);

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.scenario_file), Context.MODE_PRIVATE);

        Map<String, ?> scenarios = sharedPref.getAll();
        System.out.println(scenarios);
        ArrayList<String> dataSet = new ArrayList<>();

        for (Map.Entry<String, ?> entry : scenarios.entrySet())
            dataSet.add(entry.getKey() + "=" + entry.getValue());
        System.out.println(dataSet);

        ListAdapter scenarioAdapter = new ScenariosViewAdapter(this, dataSet);

        myListView = (ListView) findViewById(R.id.scenarios_view_list_view);
        myListView.setAdapter(scenarioAdapter);

    }
}

package com.example.max.contextlearning;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

public class KbActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kb);

        SharedPreferences sharedPref = getSharedPreferences("volKB", Context.MODE_PRIVATE);
        Map<String, ?> highScore = sharedPref.getAll();

        System.out.println(highScore);

        ArrayList<String> dataSet = new ArrayList<String>();
        for (Map.Entry<String, ?> pair : highScore.entrySet()) {
            dataSet.add(pair.getKey()+ ", " + pair.getValue());
        }

        ListAdapter kbAdapter = new KbAdapter(this, dataSet);

        ListView kbListView = (ListView) findViewById(R.id.list_view_kb);
        kbListView.setAdapter(kbAdapter);
    }
}

package com.example.max.contextlearning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class TrainingSetDb extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_set_db);

        TrainingSetDbHelper trainingSet = new TrainingSetDbHelper(this);
        ArrayList<String> dataSet = trainingSet.getAll();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, dataSet);
        ListView listView = (ListView) findViewById(R.id.training_set_list_view);
        listView.setAdapter(arrayAdapter);
    }
}

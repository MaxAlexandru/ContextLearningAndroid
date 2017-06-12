package com.max.app.contextlearning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.max.app.contextlearning.database.TrainingSetDbHelper;
import com.max.app.contextlearning.utilities.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class CurrentState extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_state);

        TrainingSetDbHelper trainingSet = new TrainingSetDbHelper(this);
        ArrayList<String> dataSet = trainingSet.getAll();

        LinkedHashMap<String, LinkedHashMap<String, String>> trainMap = new LinkedHashMap<>();
        Set<String> classes = new HashSet<>();
        LinkedHashMap<String, String []> attrVals = new LinkedHashMap<>();
        attrVals.putAll(Constants.SENSORS_VALUES);

        for (String s : dataSet) {
            String [] aux = s.split(" \\| ");
            String key = aux[0].split(" : ")[1];
            LinkedHashMap<String, String> values = new LinkedHashMap<>();

            classes.add(aux[1].split(" : ")[1]);

            for (int i = 1; i < aux.length; i++) {
                values.put(aux[i].split(" : ")[0], aux[i].split(" : ")[1]);
            }

            trainMap.put(key, values);
        }

        System.out.println(trainMap);
        System.out.println(classes);
        System.out.println(attrVals);

        LinkedHashMap<String, LinkedHashMap<String, String>> tree = new LinkedHashMap<>();


    }
}

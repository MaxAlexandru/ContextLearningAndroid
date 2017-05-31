package com.max.app.contextlearning.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.max.app.contextlearning.R;
import com.max.app.contextlearning.algorithms.DecisionTree;
import com.max.app.contextlearning.algorithms.DecisionTreeHelper;
import com.max.app.contextlearning.database.TrainingSetDbHelper;
import com.max.app.contextlearning.utilities.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ActivitiesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TrainingSetDbHelper trainingSet = new TrainingSetDbHelper(getActivity());
        ArrayList<String> dataSet = trainingSet.getAll();
        ArrayList<HashMap<String, String>> data = parseData(dataSet);
        HashSet<String> classes = getClasses(data);

        System.out.println(data);

        HashMap<String, DecisionTree> trees = new HashMap<>();
        for (String c : classes) {
            ArrayList<String> attributes = new ArrayList<>();
            attributes.addAll(Constants.SENSORS_VALUES.keySet());
            DecisionTreeHelper th = new DecisionTreeHelper(c);
            DecisionTree dt = th.id3(data, attributes);
            trees.put(c, dt);
        }

//        DecisionTreeHelper th = new DecisionTreeHelper("Using Bed");
//        DecisionTree dt = th.id3(data, Constants.SENSORS_VALUES.keySet());
//        System.out.println(dt.attr);

        for (String c : classes)
            System.out.println(trees.get(c).attr);
//        System.out.println(trees.get("Using Outside").children.get("1000,5000").attr);
    }

    public ArrayList<HashMap<String, String>> parseData(ArrayList<String> data) {
        ArrayList<HashMap<String, String>> newData = new ArrayList<>();

        for (String s : data) {
            HashMap<String, String> newEntry = new HashMap<>();
            String [] tokens = s.split(" \\| ");
            for (int i = 1; i < tokens.length; i++) {
                String [] entries = tokens[i].split(" : ");
                newEntry.put(entries[0], entries[1]);
            }
            newData.add(newEntry);
        }

        return newData;
    }

    public static HashSet<String> getClasses(ArrayList<HashMap<String, String>> data) {
        HashSet<String> classes = new HashSet<>();

        for (HashMap<String, String> s : data)
            classes.add(s.get("label"));

        return classes;
    }
}

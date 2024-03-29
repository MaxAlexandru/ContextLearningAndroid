/*******************************************************************************
 * Copyright (c) 2017 Maximilian Alexandru.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.max.app.contextlearning.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.max.app.contextlearning.R;
import com.max.app.contextlearning.algorithms.DecisionTree;
import com.max.app.contextlearning.algorithms.DecisionTreeHelper;
import com.max.app.contextlearning.database.DataSetDbHelper;
import com.max.app.contextlearning.utilities.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

        final DataSetDbHelper labeledDb = new DataSetDbHelper(getActivity());
        ArrayList<String> allDataSet = labeledDb.getAllLabeled();
        ArrayList<HashMap<String, String>> data = DecisionTreeHelper.parseData(allDataSet);
        HashSet<String> classes = DecisionTreeHelper.getClasses(data);
        final HashMap<String, DecisionTree> trees = new HashMap<>();
        for (String c : classes) {
            ArrayList<String> attributes = new ArrayList<>();
            attributes.addAll(Constants.SENSORS_VALUES.keySet());
            DecisionTreeHelper th = new DecisionTreeHelper(c);
            DecisionTree dt = th.id3(data, attributes, null, null);
            trees.put(c, dt);
        }

        ArrayList<String> spinnerClasses = new ArrayList<>();
        for (String s : classes)
            spinnerClasses.add(s);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerClasses);
        final Spinner classesSpinner = (Spinner) getActivity().findViewById(R.id.activities_classes_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classesSpinner.setAdapter(adapter);

        final EditText fromEditText = (EditText) getActivity().findViewById(R.id.activities_from_time);
        final EditText toEditText = (EditText) getActivity().findViewById(R.id.activities_to_time);
        final Button saveBtn = (Button) getActivity().findViewById(R.id.activities_save_btn);
        final Button viewBtn = (Button) getActivity().findViewById(R.id.activities_view_tree_btn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String from = fromEditText.getText().toString();
                String to = toEditText.getText().toString();
                DateFormat formatter = new SimpleDateFormat("HH:mm");
                Date fromDate = null, toDate = null;
                try {
                    fromDate = formatter.parse(from);
                    toDate = formatter.parse(to);
                } catch (ParseException e) {
                    Toast toast = Toast.makeText(getActivity(),
                            "Wrong time format! Use HH:MM format.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                if (fromDate != null && toDate != null) {
                    long fromNum = fromDate.getTime();
                    long toNum = toDate.getTime();
                    Log.i(Constants.TAG, String.valueOf(fromNum));
                    Log.i(Constants.TAG, String.valueOf(toNum));

                    DataSetDbHelper rawDb = new DataSetDbHelper(getActivity());
                    ArrayList<String> rawData = rawDb.getAllRawBetween(fromNum, toNum);
                    HashSet<String> labeledData = new HashSet<>();
                    for (String s : rawData) {
                        String rawValue = s.split("=")[1];
                        String [] values = rawValue.split(" ");
                        String newValues = "";
                        newValues += Constants.beautify(0, values[0]) + " ";
                        newValues += values[1] + " ";
                        newValues += Constants.beautify(2, values[2]) + " ";
                        newValues += values[3] + " ";
                        newValues += values[4] + " ";
                        newValues += Constants.beautify(5, values[5]);
                        labeledData.add(newValues);
                    }

                    EditText labelEditText = (EditText) getActivity().findViewById(R.id.edit_text_activity);
                    String label = labelEditText.getText().toString();
                    if (label.length() == 0) {
                        Toast toast = Toast.makeText(getActivity(), "No activity name!", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        for (String s : labeledData)
                            rawDb.addLabeled(s, label);
                        Toast toast = Toast.makeText(getActivity(), "Preference saved", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView treeView = (TextView) getActivity().findViewById(R.id.activities_tree_view);
                treeView.setText(DecisionTreeHelper.displayTree(trees.get(classesSpinner.getSelectedItem().toString())));
            }
        });

    }
}

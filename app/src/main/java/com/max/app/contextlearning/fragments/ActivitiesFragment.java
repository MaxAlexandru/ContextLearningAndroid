package com.max.app.contextlearning.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.max.app.contextlearning.R;
import com.max.app.contextlearning.algorithms.DecisionTree;
import com.max.app.contextlearning.algorithms.DecisionTreeHelper;
import com.max.app.contextlearning.database.DataSetDbHelper;
import com.max.app.contextlearning.database.TrainingSetDbHelper;
import com.max.app.contextlearning.utilities.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

        final EditText fromEditText = (EditText) getActivity().findViewById(R.id.activities_from_time);
        final EditText toEditText = (EditText) getActivity().findViewById(R.id.activities_to_time);
        Button saveBtn = (Button) getActivity().findViewById(R.id.activities_save_btn);

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
                }
                if (fromDate != null && toDate != null) {
                    Log.i(Constants.TAG, String.valueOf(fromDate.getTime()));
                    Log.i(Constants.TAG, String.valueOf(toDate.getTime()));

//                    DataSetDbHelper rawDb = new DataSetDbHelper(getActivity());
//                    ArrayList<String> rawData = rawDb.getAllRaw();
//                    for (String s : rawData) {
//                        Log.i(Constants.TAG, s.split("=")[0]);
//                    }
                }


            }
        });
    }
}

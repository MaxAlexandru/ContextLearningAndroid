/*******************************************************************************
 * Copyright (c) 2017 Maximilian Alexandru.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.max.app.contextlearning.fragments;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.max.app.contextlearning.algorithms.DecisionTreeHelper;
import com.max.app.contextlearning.database.DataSetDbHelper;
import com.max.app.contextlearning.utilities.Constants;
import com.max.app.contextlearning.adapters.HomeItemsAdapter;
import com.max.app.contextlearning.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class HomeFragment extends ListFragment {

    private BroadcastReceiver br;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Init home items array list and adapter. */
        final ArrayList<String> dataSet = new ArrayList<>();
        for (int i = 0; i < Constants.SENSORS.length; i++)
            dataSet.add("None");
        final ArrayAdapter arrayAdapter = new HomeItemsAdapter(getActivity(), dataSet);
        setListAdapter(arrayAdapter);

        /* Broadcast receiver for sensor data. */
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NumberFormat formatter0 = new DecimalFormat("#0");
                NumberFormat formatter1 = new DecimalFormat("#0.0");
                NumberFormat formatter2 = new DecimalFormat("#0.00");
                NumberFormat formatter3 = new DecimalFormat("#0.000");
                String action = intent.getAction();
                /* Light */
                if (action.equals(Constants.ACTIONS.get("Light"))) {
                    String value = intent.getStringExtra("LightSensor");
                    dataSet.set(0, formatter1.format(Float.parseFloat(value)));
                /* Proximity */
                } else if (action.equals(Constants.ACTIONS.get("Proximity"))) {
                    String value = intent.getStringExtra("ProximitySensor");
                    dataSet.set(1, formatter0.format(Float.parseFloat(value)));
                /* Noise */
                } else if (action.equals(Constants.ACTIONS.get("Noise"))) {
                    double value = intent.getDoubleExtra("NoiseSensor", 0.0);
                    dataSet.set(2, formatter1.format(value));
                /* Gravity */
                } else if (action.equals(Constants.ACTIONS.get("Gravity"))) {
                    String value = intent.getStringExtra("GravitySensor");
                    String [] sp = value.split(" ");
                    String data = "";
                    data += "X:" + String.valueOf(formatter2.format(Float.parseFloat(sp[0]))) + "  ";
                    data += "Y:" + String.valueOf(formatter2.format(Float.parseFloat(sp[1]))) + "  ";
                    data += "Z:" + String.valueOf(formatter2.format(Float.parseFloat(sp[2])));
                    dataSet.set(3, data);
                /* Acceleration */
                } else if (action.equals(Constants.ACTIONS.get("Acceleration"))) {
                    String value = intent.getStringExtra("AccelerationSensor");
                    String [] sp = value.split(" ");
                    String data = "";
                    data += "X:" + String.valueOf(formatter2.format(Float.parseFloat(sp[0]))) + "  ";
                    data += "Y:" + String.valueOf(formatter2.format(Float.parseFloat(sp[1]))) + "  ";
                    data += "Z:" + String.valueOf(formatter2.format(Float.parseFloat(sp[2])));
                    dataSet.set(4, data);
                /* Battery temperature */
                } else if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                    int t = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                    float ft = ((float) t) / 10;
                    dataSet.set(5, formatter1.format(ft));
                } else if (action.equals(Constants.ACTIONS.get("Location"))) {
                    String value = intent.getStringExtra("LocationSensor");
                    String [] sp = value.split(" ");
                    String data = "";
                    data += "LAT: " + String.valueOf(formatter3.format(Float.parseFloat(sp[0]))) + "  ";
                    data += "LONG: " + String.valueOf(formatter3.format(Float.parseFloat(sp[1])));
                    dataSet.set(6, data);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        };

        /* Add actions for receiver and register it. */
        IntentFilter intentFilter = new IntentFilter();
        for (Map.Entry<String, String> entry : Constants.ACTIONS.entrySet())
            intentFilter.addAction(entry.getValue());
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        getActivity().registerReceiver(br, intentFilter);

        final Spinner currentSpinner = (Spinner) getActivity().findViewById(R.id.home_activity_spinner);
        ArrayList<String> spinnerClasses = new ArrayList<>();
        final DataSetDbHelper labeledDb = new DataSetDbHelper(getActivity());
        ArrayList<String> allDataSet = labeledDb.getAllLabeled();
        ArrayList<HashMap<String, String>> data = DecisionTreeHelper.parseData(allDataSet);
        HashSet<String> classes = DecisionTreeHelper.getClasses(data);
        String lastActivity = labeledDb.getLastActivity();
        TextView currentActivity = (TextView) getActivity().findViewById(R.id.home_current_activity);
        currentActivity.setText(lastActivity);
        for (String s : classes)
            spinnerClasses.add(s);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerClasses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentSpinner.setAdapter(adapter);
        Button changeBtn = (Button) getActivity().findViewById(R.id.home_change_btn);
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String label = currentSpinner.getSelectedItem().toString();
                String lastRaw = labeledDb.getLastRaw();
                String rawValue = lastRaw.split("=")[1];
                String [] values = rawValue.split(" ");
                String newValues = "";
                newValues += Constants.beautify(0, values[0]) + " ";
                newValues += values[1] + " ";
                newValues += Constants.beautify(2, values[2]) + " ";
                newValues += values[3] + " ";
                newValues += values[4] + " ";
                newValues += Constants.beautify(5, values[5]);
                labeledDb.addLabeled(newValues, label);
                Toast toast = Toast.makeText(getActivity(), "Preference saved", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(br);
        super.onDestroyView();
    }
}

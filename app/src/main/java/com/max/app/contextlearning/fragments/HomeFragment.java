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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.max.app.contextlearning.utilities.Constants;
import com.max.app.contextlearning.database.TrainingSetDbHelper;
import com.max.app.contextlearning.adapters.HomeItemsAdapter;
import com.max.app.contextlearning.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
                } else if (action.equals(Constants.ACTIONS.get("Current"))) {
                    String value = intent.getStringExtra("CurrentActivity");
                    TextView current = (TextView) getActivity().findViewById(R.id.home_current_activities);
                    current.setText(value);
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

        /* Add current sensor data to database as training set. */
        final EditText name = (EditText) getActivity().findViewById(R.id.home_name);
        Button save = (Button) getActivity().findViewById(R.id.home_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TrainingSetDbHelper trainingSet = new TrainingSetDbHelper(getActivity());
                final Toast failToast = Toast.makeText(getActivity(),
                        "No label error!", Toast.LENGTH_SHORT);
                final Toast okToast = Toast.makeText(getActivity(),
                        "Entry added!", Toast.LENGTH_SHORT);

                ArrayList<String> newEntry = new ArrayList<>();

                if (name.getText().toString().trim().length() == 0)
                    failToast.show();
                else {
                    newEntry.add(name.getText().toString());
                    for (int i = 0; i < dataSet.size(); i++)
                        if (i == 1 || i == 3 || i == 4)
                            newEntry.add(Constants.beautify(i, dataSet.get(i)));
                        else
                            newEntry.add(dataSet.get(i));
                    trainingSet.add(newEntry);
                    okToast.show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(br);
        super.onDestroyView();
    }
}

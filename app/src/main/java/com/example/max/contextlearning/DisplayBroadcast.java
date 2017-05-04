package com.example.max.contextlearning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class DisplayBroadcast extends AppCompatActivity {

    private IntentFilter intentFilter;
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disp_bcast);

        final ListView listView = (ListView) findViewById(R.id.disp_bcast_list_view);
        final ArrayList<String> dataSet = new ArrayList<>();
        for (int i = 0; i < Constants.SENSORS.length; i++)
            dataSet.add("0");
        final ArrayAdapter arrayAdapter = new DisplayBroadcastArrayAdapter(this, dataSet);
        listView.setAdapter(arrayAdapter);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Constants.ACTIONS.get("Light"))) {
                    String value = intent.getStringExtra("LightSensor");
                    dataSet.set(0, value);
                } else if (action.equals(Constants.ACTIONS.get("Proximity"))) {
                    String value = intent.getStringExtra("ProximitySensor");
                    dataSet.set(1, value);
                } else if (action.equals(Constants.ACTIONS.get("Noise"))) {
                    double value = intent.getDoubleExtra("NoiseSensor", 0.0);
                    dataSet.set(2, String.valueOf(value));
                } else if (action.equals(Constants.ACTIONS.get("Gravity"))) {
                    String value = intent.getStringExtra("GravitySensor");
                    dataSet.set(3, value);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        };
        intentFilter = new IntentFilter();
        for (Map.Entry<String, String> entry : Constants.ACTIONS.entrySet())
            intentFilter.addAction(entry.getValue());
        this.registerReceiver(br, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(br, intentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(br);
        super.onPause();
    }

}

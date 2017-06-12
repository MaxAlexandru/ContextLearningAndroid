package com.max.app.contextlearning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.max.app.contextlearning.adapters.HomeItemsAdapter;
import com.max.app.contextlearning.database.TrainingSetDbHelper;
import com.max.app.contextlearning.utilities.Constants;

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
        final ArrayAdapter arrayAdapter = new HomeItemsAdapter(this, dataSet);
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
                } else if (action.equals(Constants.ACTIONS.get("Acceleration"))) {
                    String value = intent.getStringExtra("AccelerationSensor");
                    dataSet.set(4, value);
                } else if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                    int t = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                    float ft = ((float) t) / 10;
                    dataSet.set(5, String.valueOf(ft));
                }
                arrayAdapter.notifyDataSetChanged();
            }
        };
        intentFilter = new IntentFilter();
        for (Map.Entry<String, String> entry : Constants.ACTIONS.entrySet())
            intentFilter.addAction(entry.getValue());
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(br, intentFilter);

        final TrainingSetDbHelper trainingSet = new TrainingSetDbHelper(this);
        final Toast failToast = Toast.makeText(this, "No label error!", Toast.LENGTH_SHORT);
        final Toast okToast = Toast.makeText(this, "Entry added!", Toast.LENGTH_SHORT);
        Button saveButton = (Button) findViewById(R.id.disp_bcast_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> newEntry = new ArrayList<>();
                EditText editText = (EditText) findViewById(R.id.disp_bcast_label_edit_text);
                if (editText.getText().toString().trim().length() == 0)
                    failToast.show();
                else {
                    newEntry.add(editText.getText().toString());
                    for (int i = 0; i < dataSet.size(); i++)
                        newEntry.add(Constants.beautify(i, dataSet.get(i)));
                    trainingSet.add(newEntry);
                    okToast.show();
                }
            }
        });
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

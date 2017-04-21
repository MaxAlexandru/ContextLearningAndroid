package com.example.max.contextlearning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ContextReader extends AppCompatActivity {

    private SensorManager mySensorManager;
    private ArrayList<Sensor> sensorTypes;
    private ArrayList<MySensor> mySensors;
    private final String TAG = "Sensor";
    private ArrayList<TextView> accel, light, proximity, gravity;
    private TextView temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_reader);

        accel = new ArrayList<>();
        accel.add((TextView) findViewById(R.id.accel_tv_x));
        accel.add((TextView) findViewById(R.id.accel_tv_y));
        accel.add((TextView) findViewById(R.id.accel_tv_z));

        light = new ArrayList<>();
        light.add((TextView) findViewById(R.id.light_tv));

        proximity = new ArrayList<>();
        proximity.add((TextView) findViewById(R.id.proximity_tv));

        gravity = new ArrayList<>();
        gravity.add((TextView) findViewById(R.id.grav_tv_x));
        gravity.add((TextView) findViewById(R.id.grav_tv_y));
        gravity.add((TextView) findViewById(R.id.grav_tv_z));

        mySensorManager = (SensorManager) getSystemService(android.content.Context.SENSOR_SERVICE);

        sensorTypes = new ArrayList<>();
        sensorTypes.add(mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        sensorTypes.add(mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
        sensorTypes.add(mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
        sensorTypes.add(mySensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));

        mySensors = new ArrayList<>();
        mySensors.add(new MySensor(Sensor.TYPE_ACCELEROMETER, accel));
        mySensors.add(new MySensor(Sensor.TYPE_LIGHT, light));
        mySensors.add(new MySensor(Sensor.TYPE_PROXIMITY, proximity));
        mySensors.add(new MySensor(Sensor.TYPE_GRAVITY, gravity));

        temp = (TextView) findViewById(R.id.dev_temp_tv);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int t = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                float ft = ((float) t) / 10;
                temp.setText(ft + " \u2103");
            }
        }, ifilter);

    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        Log.i(TAG, "onResume");
        for (int i = 0; i < mySensors.size(); i++)
            mySensorManager.registerListener(mySensors.get(i), sensorTypes.get(i),
                    SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        Log.i(TAG, "onPause");
        for (MySensor s : mySensors)
            mySensorManager.unregisterListener(s);
    }

}

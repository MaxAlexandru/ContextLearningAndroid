package com.example.max.contextlearning;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ReadIlluminance extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mLight;
    private final String TAG = "readIlluminance";
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_illuminance);

        textView = (TextView) findViewById(R.id.light_text_view);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i(TAG, "onSensorChanged");
        System.out.println(sensorEvent.values[0]);
        textView.setText(sensorEvent.values[0] + " lx");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i(TAG, "onAccuracyChanged");
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        Log.i(TAG, "onResume");
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        Log.i(TAG, "onPause");
        mSensorManager.unregisterListener(this);
    }
}

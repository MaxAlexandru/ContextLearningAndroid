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

import java.text.DecimalFormat;
import java.util.Locale;

public class ReadMotion extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final String TAG = "readAccelerometer";
    TextView textViewX, textViewY, textViewZ, activityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_motion);

        textViewX = (TextView) findViewById(R.id.accelx_text_view);
        textViewY = (TextView) findViewById(R.id.accely_text_view);
        textViewZ = (TextView) findViewById(R.id.accelz_text_view);
        activityTextView = (TextView) findViewById(R.id.activity_text_view);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i(TAG, "onSensorChanged");
        System.out.println(sensorEvent.values[0]);

        String x = String.format(Locale.getDefault(), "%.4f", sensorEvent.values[0]);
        String y = String.format(Locale.getDefault(), "%.4f", sensorEvent.values[1]);
        String z = String.format(Locale.getDefault(), "%.4f", sensorEvent.values[2]);

        textViewX.setText(x);
        textViewY.setText(y);
        textViewZ.setText(z);

        if (Float.parseFloat(z) > 9.5)
            activityTextView.setText("On table");
        else if (Float.parseFloat(z) > 4 && Float.parseFloat(y) > 3)
            activityTextView.setText("In Hand");
        else
            activityTextView.setText("Unknown");

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
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        Log.i(TAG, "onPause");
        mSensorManager.unregisterListener(this);
    }
}

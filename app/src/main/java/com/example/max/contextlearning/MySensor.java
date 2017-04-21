package com.example.max.contextlearning;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Max on 4/19/2017.
 */

public class MySensor implements SensorEventListener {

    private int type;
    private ArrayList<TextView> textView;

    public MySensor(int type, ArrayList<TextView> textView) {
        this.type = type;
        this.textView = textView;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        for (int i = 0; i < textView.size(); i++) {
            String val = "";
            if (type == Sensor.TYPE_ACCELEROMETER || type == Sensor.TYPE_GRAVITY)
                val = String.format(Locale.getDefault(), "%.3f", sensorEvent.values[i]);
            else
                val = sensorEvent.values[i] + "";
            textView.get(i).setText(val);
        }

    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }
}

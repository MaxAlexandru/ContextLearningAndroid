package com.max.app.contextlearning.listeners;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.max.app.contextlearning.utilities.Constants;

import java.util.ArrayList;

public class MySensorListener implements SensorEventListener {

    private int type;
    private ArrayList<Float> light;
    private Context context;

    public MySensorListener(int type, Context context) {
        this.type = type;
        this.light = new ArrayList<>();
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        String value;
        Intent intent = new Intent();
        int last = 5;

        switch (type) {
            case Sensor.TYPE_LIGHT:
                if (light.size() > last)
                    light.remove(0);
                light.add(sensorEvent.values[0]);
                float average = 0;
                for (float i : light)
                    average += i;
                average /= light.size();
                value = String.valueOf(average);
                intent.setAction(Constants.ACTIONS.get("Light"));
                intent.putExtra("LightSensor", value);
                break;
            case Sensor.TYPE_PROXIMITY:
                value = sensorEvent.values[0] + "";
                intent.setAction(Constants.ACTIONS.get("Proximity"));
                intent.putExtra("ProximitySensor", value);
                break;
            case Sensor.TYPE_GRAVITY:
                value = sensorEvent.values[0] + " " +
                        sensorEvent.values[1] + " " +
                        sensorEvent.values[2];
                intent.setAction(Constants.ACTIONS.get("Gravity"));
                intent.putExtra("GravitySensor", value);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                value = sensorEvent.values[0] + " " +
                        sensorEvent.values[1] + " " +
                        sensorEvent.values[2];
                intent.setAction(Constants.ACTIONS.get("Acceleration"));
                intent.putExtra("AccelerationSensor", value);
                break;
        }
        context.sendBroadcast(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}

package com.example.max.contextlearning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DisplayBroadcast extends AppCompatActivity {

    private IntentFilter intentFilter;
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_illuminance);

        final TextView light = (TextView) findViewById(R.id.light_text_view);
        final TextView proximity = (TextView) findViewById(R.id.proximity_text_view);
        final TextView noise = (TextView) findViewById(R.id.noise_text_view);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Constants.ACTIONS.get("Light"))) {
                    String value = intent.getStringExtra("LightSensor");
                    light.setText(value);
                } else if (action.equals(Constants.ACTIONS.get("Proximity"))) {
                    String value = intent.getStringExtra("ProximitySensor");
                    proximity.setText(value);
                } else if (action.equals(Constants.ACTIONS.get("Noise"))) {
                    double value = intent.getDoubleExtra("NoiseSensor", 0.0);
                    noise.setText(String.valueOf((int) value) + "dB");
                }
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTIONS.get("Light"));
        intentFilter.addAction(Constants.ACTIONS.get("Proximity"));
        intentFilter.addAction(Constants.ACTIONS.get("Noise"));
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

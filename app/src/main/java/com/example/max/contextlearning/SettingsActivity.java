package com.example.max.contextlearning;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    public final String TAG = "SETTINGS ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final PackageManager pm  = this.getPackageManager();
        final ComponentName volReceiver = new ComponentName(this, VolumeReceiver.class);
        final ComponentName volService = new ComponentName(this, VolumeService.class);

        final SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.settings_filename),Context.MODE_PRIVATE);
        String notification = sharedPref.getString("Notifications", null);
        String autoVolume = sharedPref.getString("AutoVolume", null);
        String sensorsService = sharedPref.getString("SensorsService", null);
        final SharedPreferences.Editor editor = sharedPref.edit();

        Button defaultSettings = (Button) findViewById(R.id.default_settings_button);
        defaultSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref.edit().clear().apply();
                Log.i(TAG, "Settings set to default");
            }
        });

        Switch notifSwitch = (Switch) findViewById(R.id.notification_switch);
        if (notification != null && notification.equals("On"))
            notifSwitch.setChecked(true);
        else
            notifSwitch.setChecked(false);
        notifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    pm.setComponentEnabledSetting(volReceiver,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);

                    editor.putString("Notifications", "Off");
                } else {
                    pm.setComponentEnabledSetting(volReceiver,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                    editor.putString("Notifications", "On");
                }
                editor.apply();
            }
        });

        Switch volSwitch = (Switch) findViewById(R.id.auto_volume_switch);
        if (autoVolume != null && autoVolume.equals("On"))
            volSwitch.setChecked(true);
        else
            volSwitch.setChecked(false);
        volSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    editor.putString("AutoVolume", "Off");
                } else {
                    editor.putString("AutoVolume", "On");
                }
                editor.apply();
            }
        });

        Switch sensorsSwitch = (Switch) findViewById(R.id.sensors_service_switch);
        if (sensorsService != null && sensorsService.equals("On"))
            sensorsSwitch.setChecked(true);
        else
            sensorsSwitch.setChecked(false);
        sensorsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    editor.putString("SensorsService", "Off");
                } else {
                    editor.putString("SensorsService", "On");
                }
                editor.apply();
            }
        });
    }
}

package com.max.app.contextlearning.fragments;

import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.max.app.contextlearning.utilities.Constants;
import com.max.app.contextlearning.R;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.settings_filename), Context.MODE_PRIVATE);
        String sensorsService = sharedPref.getString("SensorsService", null);
        final SharedPreferences.Editor editor = sharedPref.edit();

        /* Sensor service switch */
        Switch sensorsSwitch = (Switch) getActivity().findViewById(R.id.settings_sensors_state_switch);
        if (sensorsService != null && sensorsService.equals("On")) {
            sensorsSwitch.setChecked(true);
        } else {
            sensorsSwitch.setChecked(false);
        }
        final int mId = 1;
        sensorsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    editor.putString("SensorsService", "Off");
                    Log.i(Constants.TAG, "[SettingsFragment]: Sensors service OFF");
                    NotificationManager mNotificationManager =
                            (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(mId);
                } else {
                    Log.i(Constants.TAG, "[SettingsFragment]: Sensors service ON");
                    editor.putString("SensorsService", "On");
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getActivity())
                                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                    .setContentTitle("Your current activity is")
                                    .setContentText("Loading")
                                    .setOngoing(true);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(mId, mBuilder.build());
                }
                editor.apply();
            }
        });
    }
}

package com.max.app.contextlearning.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.max.app.contextlearning.utilities.Constants;
import com.max.app.contextlearning.R;
import com.max.app.contextlearning.database.TrainingSetDbHelper;

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
//        String notification = sharedPref.getString("Notifications", null);
//        String autoVolume = sharedPref.getString("AutoVolume", null);
        String sensorsService = sharedPref.getString("SensorsService", null);
        String beautifyState = sharedPref.getString("Beautify", null);
        final SharedPreferences.Editor editor = sharedPref.edit();

        /* Sensor service switch */
        Switch sensorsSwitch = (Switch) getActivity().findViewById(R.id.settings_sensors_state_switch);
        if (sensorsService != null && sensorsService.equals("On"))
            sensorsSwitch.setChecked(true);
        else
            sensorsSwitch.setChecked(false);
        sensorsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    editor.putString("SensorsService", "Off");
                    Log.i(Constants.TAG, "[SettingsFragment]: Sensors service OFF");
                } else {
                    Log.i(Constants.TAG, "[SettingsFragment]: Sensors service ON");
                    editor.putString("SensorsService", "On");
                }
                editor.apply();
            }
        });

        /* Beautify switch */
        Switch beautifySwitch = (Switch) getActivity().findViewById(R.id.settings_beautify_state_switch);
        if (beautifyState != null && beautifyState.equals("On"))
            beautifySwitch.setChecked(false);
        else
            beautifySwitch.setChecked(true);
        beautifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    editor.putString("Beautify", "Off");
                    Log.i(Constants.TAG, "[SettingsFragment]: Beautify service OFF");
                } else {
                    Log.i(Constants.TAG, "[SettingsFragment]: Beautify service ON");
                    editor.putString("Beautify", "On");
                }
                editor.apply();
            }
        });

        /* Clear database */
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                TrainingSetDbHelper db = new TrainingSetDbHelper(getActivity());
                db.deleteAll();
                Toast toast = Toast.makeText(getActivity(), "Database deleted!", Toast.LENGTH_SHORT);
                toast.show();
                Log.i(Constants.TAG, "[SettingsFragment]: Database deleted");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(Constants.TAG, "[SettingsFragment]: Database deleted canceled");
            }
        });
        builder.create();
        Button deleteDatabase = (Button) getActivity().findViewById(R.id.settings_clear_db_button);
        deleteDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Constants.TAG, "[SettingsFragment]: Database deleted started");
                builder.show();
            }
        });
    }
}

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.max.app.contextlearning.algorithms.DecisionTreeHelper;
import com.max.app.contextlearning.database.DataSetDbHelper;
import com.max.app.contextlearning.utilities.Constants;
import com.max.app.contextlearning.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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


        final DataSetDbHelper labeledDb = new DataSetDbHelper(getActivity());
        ArrayList<String> allDataSet = labeledDb.getAllLabeled();
        ArrayList<HashMap<String, String>> data = DecisionTreeHelper.parseData(allDataSet);
        HashSet<String> classes = DecisionTreeHelper.getClasses(data);
        ArrayList<String> spinnerClasses = new ArrayList<>();
        for (String s : classes)
            spinnerClasses.add(s);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerClasses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner = (Spinner) getActivity().findViewById(R.id.settings_spinner);
        spinner.setAdapter(adapter);

        final SeekBar seekbar = (SeekBar) getActivity().findViewById(R.id.settings_seekbar);

        final SharedPreferences volSharedPref = getActivity().getSharedPreferences(
                getString(R.string.volume_filename), Context.MODE_PRIVATE);
        final SharedPreferences.Editor volEditor = volSharedPref.edit();

        Map<String, ?> volValues = volSharedPref.getAll();
        final ArrayList<String> volAdapterData = new ArrayList<>();
        System.out.println(volValues);
        for (String s : volValues.keySet())
            volAdapterData.add(s + " volume level is " + volValues.get(s));
        final ListView listView = (ListView) getActivity().findViewById(R.id.settings_list_view);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, volAdapterData);
        listView.setAdapter(arrayAdapter);

        Button button = (Button) getActivity().findViewById(R.id.settings_save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int level = seekbar.getProgress();
                String activity = (String)spinner.getSelectedItem();
                volEditor.putString(activity, String.valueOf(level));
                volEditor.apply();
                Toast toast = Toast.makeText(getActivity(), "Preference saved", Toast.LENGTH_SHORT);
                toast.show();
                Map<String, ?> volValues = volSharedPref.getAll();
                ArrayList<String> volAdapterData = new ArrayList<>();
                System.out.println(volValues);
                for (String s : volValues.keySet())
                    volAdapterData.add(s + " volume level is " + volValues.get(s));
                final ListView listView = (ListView) getActivity().findViewById(R.id.settings_list_view);
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1, volAdapterData);
                listView.setAdapter(arrayAdapter);
            }
        });

        Button clearButton = (Button) getActivity().findViewById(R.id.settings_clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volEditor.clear();
                volEditor.apply();
                Map<String, ?> volValues = volSharedPref.getAll();
                ArrayList<String> volAdapterData = new ArrayList<>();
                System.out.println(volValues);
                for (String s : volValues.keySet())
                    volAdapterData.add(s + " volume level is " + volValues.get(s));
                final ListView listView = (ListView) getActivity().findViewById(R.id.settings_list_view);
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1, volAdapterData);
                listView.setAdapter(arrayAdapter);
                Toast toast = Toast.makeText(getActivity(), "Preferences deleted", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}

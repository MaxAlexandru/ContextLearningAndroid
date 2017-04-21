package com.example.max.contextlearning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Max on 4/21/2017.
 */

public class ScenarioAdapter extends ArrayAdapter {

    private TextView name;
    private Spinner values;
    private CheckBox ok;

    public ScenarioAdapter(Context context, ArrayList<String> resource) {
        super(context, R.layout.scenario_item, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View myView;
        if (convertView == null)
            myView = LayoutInflater.from(getContext()).inflate(R.layout.scenario_item, parent, false);
        else
            myView = convertView;

        name = (TextView) myView.findViewById(R.id.scenario_item_name);
        values = (Spinner) myView.findViewById(R.id.scenario_item_values);
        ok = (CheckBox) myView.findViewById(R.id.scenario_item_ok);

        name.setText(getItem(position) + "");
        ArrayList<String> dataSet = new ArrayList<>();
        Collections.addAll(dataSet, Constants.SENSORS_VALUES.get(getItem(position) + ""));
        ArrayAdapter<String> valuesAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item);
        valuesAdapter.addAll(dataSet);
        values.setAdapter(valuesAdapter);

        return myView;
    }

}

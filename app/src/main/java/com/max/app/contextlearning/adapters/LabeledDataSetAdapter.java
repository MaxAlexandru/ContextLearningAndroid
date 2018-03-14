/*******************************************************************************
 * Copyright (c) 2017 Maximilian Alexandru.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.max.app.contextlearning.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.max.app.contextlearning.R;

import java.util.ArrayList;
import java.util.Calendar;

public class LabeledDataSetAdapter extends ArrayAdapter {

    public LabeledDataSetAdapter(Context context, ArrayList<String> resource) {
        super(context, R.layout.adapter_labeled_db, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View myView;
        if (convertView == null)
            myView = LayoutInflater.from(getContext()).inflate(
                    R.layout.adapter_labeled_db, parent, false);
        else
            myView = convertView;

        String item = getItem(position).toString();

        TextView sensors = (TextView) myView.findViewById(R.id.adapter_labeled_db_sensors);
        TextView label = (TextView) myView.findViewById(R.id.adapter_labeled_db_label);

        sensors.setText(item.split("=")[0]);
        label.setText(item.split("=")[1]);

        return myView;
    }
}

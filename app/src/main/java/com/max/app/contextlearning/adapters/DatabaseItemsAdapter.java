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

public class DatabaseItemsAdapter extends ArrayAdapter {

    public DatabaseItemsAdapter(Context context, ArrayList<String> resource) {
        super(context, R.layout.adapter_database_items, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View myView;
        if (convertView == null)
            myView = LayoutInflater.from(getContext()).inflate(
                    R.layout.adapter_database_items, parent, false);
        else
            myView = convertView;

        String item = getItem(position).toString();

        TextView name = (TextView) myView.findViewById(R.id.database_item_name);
        TextView light = (TextView) myView.findViewById(R.id.database_item_light);
        TextView proximity = (TextView) myView.findViewById(R.id.database_item_proximity);
        TextView noise = (TextView) myView.findViewById(R.id.database_item_noise);
        TextView gravity = (TextView) myView.findViewById(R.id.database_item_gravity);
        TextView acceleration = (TextView) myView.findViewById(R.id.database_item_acceleration);
        TextView temperature = (TextView) myView.findViewById(R.id.database_item_temperature);
        TextView location = (TextView) myView.findViewById(R.id.database_item_location);

        String [] tokens = item.split(" \\| ");
        String label = tokens[0].split(" : ")[1] + ". " + tokens[1].split(" : ")[1];
        name.setText(label);
        light.setText(tokens[2].split(" : ")[1]);
        proximity.setText(tokens[3].split(" : ")[1]);
        noise.setText(tokens[4].split(" : ")[1]);
        gravity.setText(tokens[5].split(" : ")[1]);
        acceleration.setText(tokens[6].split(" : ")[1]);
        temperature.setText(tokens[7].split(" : ")[1]);
        location.setText(tokens[8].split(" : ")[1]);

        return myView;
    }

}

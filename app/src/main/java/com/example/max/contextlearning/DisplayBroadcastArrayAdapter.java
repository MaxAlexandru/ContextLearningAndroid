package com.example.max.contextlearning;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Max on 5/4/2017.
 */

public class DisplayBroadcastArrayAdapter extends ArrayAdapter {

    public DisplayBroadcastArrayAdapter(Context context, ArrayList<String> resource) {
        super(context, R.layout.activity_disp_bcast_list_view_item, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View myView;
        if (convertView == null)
            myView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_disp_bcast_list_view_item, parent, false);
        else
            myView = convertView;

        String item = getItem(position).toString();

        TextView name = (TextView) myView.findViewById(R.id.sensor_name_text_view);
        TextView value = (TextView) myView.findViewById(R.id.sensor_value_text_view);

        name.setText(Constants.SENSORS[position]);
        if (position == 2)
            value.setText(item + "Db");
        else if (position == 5)
            value.setText(item + " \u2103");
        else
            value.setText(item);

        return myView;
    }
}

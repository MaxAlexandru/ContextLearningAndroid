package com.max.app.contextlearning.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.max.app.contextlearning.utilities.Constants;
import com.max.app.contextlearning.R;

import java.util.ArrayList;

public class HomeItemsAdapter extends ArrayAdapter {

    public HomeItemsAdapter(Context context, ArrayList<String> resource) {
        super(context, R.layout.adapter_home_items, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View myView;
        if (convertView == null)
            myView = LayoutInflater.from(getContext()).inflate(
                    R.layout.adapter_home_items, parent, false);
        else
            myView = convertView;

        String item = getItem(position).toString();

        TextView name = (TextView) myView.findViewById(R.id.sensor_name_text_view);
        TextView value = (TextView) myView.findViewById(R.id.sensor_value_text_view);
        TextView unit = (TextView) myView.findViewById(R.id.sensor_unit_text_view);

        name.setText(Constants.SENSORS[position]);
        unit.setText("");

        if (position == 1 || position == 3 || position == 4)
            item = Constants.beautify(position, item);
        else if (position == 0 && !item.equals("None"))
            unit.setText("lux");
        else if (position == 2 && !item.equals("None"))
            unit.setText("Db");
        else if (position == 5 && !item.equals("None"))
            unit.setText("\u2103");
        else if (position == 6 && !item.equals("None")) {
            item = item.split(" ")[1] + "/" + item.split(" ")[4];
            unit.setText("lat/long");
        }
        value.setText(item);

        return myView;
    }
}

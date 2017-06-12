package com.max.app.contextlearning;

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
 * Created by Max on 4/21/2017.
 */

public class ScenariosViewAdapter extends ArrayAdapter {

    public ScenariosViewAdapter(Context context, ArrayList<String> resource) {
        super(context, R.layout.scenarios_view_item, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View myView;
        if (convertView == null)
            myView = LayoutInflater.from(getContext()).inflate(R.layout.scenarios_view_item, parent, false);
        else
            myView = convertView;

        String item = getItem(position).toString();

        TextView name = (TextView) myView.findViewById(R.id.scenarios_view_name);
        TextView values = (TextView) myView.findViewById(R.id.scenarios_view_values);

        String [] aux = item.split("=");
        name.setText(aux[0]);
        String [] aux2 = aux[1].split(" ");
        String aux3 = "";
        for (String s : aux2)
            aux3 += s + "\n";
        values.setText(aux3);

        return myView;
    }
}

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
import com.max.app.contextlearning.utilities.Constants;

import java.util.ArrayList;
import java.util.Calendar;

public class RawDataSetAdapter extends ArrayAdapter {

    public RawDataSetAdapter(Context context, ArrayList<String> resource) {
        super(context, R.layout.adapter_raw_db, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View myView;
        if (convertView == null)
            myView = LayoutInflater.from(getContext()).inflate(
                    R.layout.adapter_raw_db, parent, false);
        else
            myView = convertView;

        String item = getItem(position).toString();

        TextView time = (TextView) myView.findViewById(R.id.adapter_raw_db_time);
        TextView sensors = (TextView) myView.findViewById(R.id.adapter_raw_db_sensors);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(item.split("=")[0]));
        int h = calendar.get(Calendar.HOUR);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        time.setText(String.valueOf(h) + ":" + String.valueOf(m) + ":" + String.valueOf(s));
        sensors.setText(item.split("=")[1]);

        return myView;
    }

}

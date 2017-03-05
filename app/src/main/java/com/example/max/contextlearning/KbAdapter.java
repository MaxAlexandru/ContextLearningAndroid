package com.example.max.contextlearning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Max on 2/27/2017.
 */

public class KbAdapter extends ArrayAdapter {

    public KbAdapter(Context context, ArrayList<String> resource) {
        super(context, R.layout.kb_item, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View kbItemView;
        if (convertView == null)
            kbItemView = LayoutInflater.from(getContext()).inflate(R.layout.kb_item, parent, false);
        else
            kbItemView = convertView;

        TextView date = (TextView) kbItemView.findViewById(R.id.date_text_view);
        TextView time = (TextView) kbItemView.findViewById(R.id.time_text_view);
        TextView noiseInt = (TextView) kbItemView.findViewById(R.id.noise_int_text_view);
        TextView volLvl = (TextView) kbItemView.findViewById(R.id.vol_lvl_text_view);

        String data = getItem(position).toString();
        String[] text = data.split("\\|");

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.parseLong(text[0]));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yy hh:mm:ss");
        String[] dateSplit = sdf.format(c.getTime()).split(" ");

        date.setText(dateSplit[0]);
        time.setText(dateSplit[1]);
        noiseInt.setText(text[1]);
        volLvl.setText(text[2]);

        return kbItemView;
    }
}

package com.example.max.contextlearning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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

        String data = getItem(position).toString();
        String[] text = data.split(",");

        TextView volLvl = (TextView) kbItemView.findViewById(R.id.text_view_vol_lvl);
        TextView noiseInt = (TextView) kbItemView.findViewById(R.id.text_view_noise_int);

        volLvl.setText(text[0]);
        noiseInt.setText(text[1]);

        return kbItemView;
    }
}

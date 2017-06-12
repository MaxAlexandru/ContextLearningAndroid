package com.max.app.contextlearning.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.max.app.contextlearning.R;
import com.max.app.contextlearning.database.DataSetDbHelper;
import com.max.app.contextlearning.database.TrainingSetDbHelper;
import com.max.app.contextlearning.adapters.DatabaseItemsAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class DatabaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_database, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView rawDb = (TextView) getActivity().findViewById(R.id.database_to_raw_db);
        rawDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                RawDatabaseFragment rawDbFragment = new RawDatabaseFragment();
                fragmentTransaction.replace(R.id.main_fragment, rawDbFragment);
                fragmentTransaction.commit();
            }
        });

        TextView labeledDb = (TextView) getActivity().findViewById(R.id.database_to_labeled_db);
        labeledDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                LabeledDatabaseFragment labeledDbFragment = new LabeledDatabaseFragment();
                fragmentTransaction.replace(R.id.main_fragment, labeledDbFragment);
                fragmentTransaction.commit();
            }
        });
    }

}

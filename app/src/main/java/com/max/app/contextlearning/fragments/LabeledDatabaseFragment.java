package com.max.app.contextlearning.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.max.app.contextlearning.R;
import com.max.app.contextlearning.adapters.LabeledDataSetAdapter;
import com.max.app.contextlearning.database.DataSetDbHelper;

import java.util.ArrayList;

public class LabeledDatabaseFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_labeled_database, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DataSetDbHelper labeledDb = new DataSetDbHelper(getActivity());
        ArrayList<String> labeledDataSet = labeledDb.getAllLabeled();

        final LabeledDataSetAdapter dataAdapter = new LabeledDataSetAdapter(getActivity(), labeledDataSet);
        setListAdapter(dataAdapter);

        Button back = (Button) getActivity().findViewById(R.id.database_to_db);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                DatabaseFragment dbFragment = new DatabaseFragment();
                fragmentTransaction.replace(R.id.main_fragment, dbFragment);
                fragmentTransaction.commit();
            }
        });

        Button clear = (Button) getActivity().findViewById(R.id.database_clear_labeled_db);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSetDbHelper rawDb = new DataSetDbHelper(getActivity());
                rawDb.deleteAllRaw();
                Toast done = Toast.makeText(getActivity(), "Labeled DataSet deleted!", Toast.LENGTH_SHORT);
                done.show();
                dataAdapter.notifyDataSetChanged();
            }
        });
    }
}

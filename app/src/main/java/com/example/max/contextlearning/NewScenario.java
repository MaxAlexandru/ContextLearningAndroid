package com.example.max.contextlearning;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class NewScenario extends AppCompatActivity {

    private ListView myListView;
    private Button saveButton;
    private EditText nameEditText;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scenario);

        myListView = (ListView) findViewById(R.id.new_scenario_list_view);
        saveButton = (Button) findViewById(R.id.save_scenario_button);
        nameEditText = (EditText) findViewById(R.id.scenario_name_edit_text);

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.scenario_file), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        ArrayList<String> dataSet = new ArrayList<>();
        Collections.addAll(dataSet, Constants.SENSORS);
        ListAdapter scenarioAdapter = new ScenarioAdapter(this, dataSet);

        myListView.setAdapter(scenarioAdapter);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameEditText.getText().toString().equals(""))
                    return;

                String key = nameEditText.getText().toString();
                String val = "";

                for (int i = 0; i < myListView.getCount(); i++) {
                    View adapterView = myListView.getChildAt(i);

                    TextView name = (TextView) adapterView.findViewById(R.id.scenario_item_name);
                    Spinner value = (Spinner) adapterView.findViewById(R.id.scenario_item_values);
                    CheckBox ok = (CheckBox) adapterView.findViewById(R.id.scenario_item_ok);

                    if (ok.isChecked())
                        val += name.getText().toString() + ":" +
                                value.getSelectedItem().toString() + " ";
                }

                editor.putString(key, val);
                editor.apply();

                Toast toast = Toast.makeText(context, key + " scenario saved.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
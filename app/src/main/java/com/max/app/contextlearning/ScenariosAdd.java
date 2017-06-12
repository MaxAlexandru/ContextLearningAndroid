package com.max.app.contextlearning;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.max.app.contextlearning.utilities.Constants;

import java.util.ArrayList;
import java.util.Collections;

public class ScenariosAdd extends AppCompatActivity {

    private ListView myListView;
    private Button saveButton;
    private EditText nameEditText;
    private Spinner tasksSpinner;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scenario);

        myListView = (ListView) findViewById(R.id.new_scenario_list_view);
        saveButton = (Button) findViewById(R.id.save_scenario_button);
        nameEditText = (EditText) findViewById(R.id.scenario_name_edit_text);
        tasksSpinner = (Spinner) findViewById(R.id.tasks_spinner);

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.scenario_file), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        ArrayList<String> dataSet = new ArrayList<>();
        Collections.addAll(dataSet, Constants.SENSORS);
        ListAdapter scenarioAdapter = new ScenariosAddAdapter(this, dataSet);
        myListView.setAdapter(scenarioAdapter);

        ArrayAdapter<String> tasksAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        tasksAdapter.addAll(Constants.TASKS);
        tasksSpinner.setAdapter(tasksAdapter);

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

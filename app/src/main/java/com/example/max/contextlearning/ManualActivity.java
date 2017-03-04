package com.example.max.contextlearning;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

public class ManualActivity extends AppCompatActivity {

    private EditText volText;
    private EditText noiseText;
    private Button saveButton;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        volText = (EditText) findViewById(R.id.edit_text_vol_lvl);
        noiseText = (EditText) findViewById(R.id.edit_text_noise_int);
        saveButton = (Button) findViewById(R.id.button_save);
        context = this;

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!volText.getText().toString().equals("") && !noiseText.getText().toString().equals("")) {
                    SharedPreferences sharedPref = getSharedPreferences("volKB", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(volText.getText().toString(), noiseText.getText().toString());
                    editor.apply();
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

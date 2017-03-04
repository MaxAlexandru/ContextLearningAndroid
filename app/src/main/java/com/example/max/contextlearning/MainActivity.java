package com.example.max.contextlearning;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private String [] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int REQUEST_PERMISSION = 200;
    private boolean permissionsAccepted = false;

    private final Context context = this;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION:
                permissionsAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionsAccepted ) finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);

        String[] menuItems = {
                "Read Environment",
                "Knowledge Base",
                "Manual Mode"
        };

        ListAdapter menuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menuItems);

        ListView menuListView = (ListView) findViewById(R.id.list_view_menu);
        menuListView.setAdapter(menuAdapter);
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intentKb = new Intent(context, KbActivity.class);
                Intent intentManual = new Intent(context, ManualActivity.class);
                Intent intentReadEnv = new Intent(context, ReadEnvActivity.class);

                switch (i) {
                    case 0:
                        startActivity(intentReadEnv);
                        break;
                    case 1:
                        startActivity(intentKb);
                        break;
                    case 2:
                        startActivity(intentManual);
                        break;
                }
            }
        });
    }
}

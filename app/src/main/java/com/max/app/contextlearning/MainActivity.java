package com.max.app.contextlearning;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.max.app.contextlearning.fragments.ActivitiesFragment;
import com.max.app.contextlearning.fragments.DatabaseFragment;
import com.max.app.contextlearning.fragments.HomeFragment;
import com.max.app.contextlearning.fragments.SettingsFragment;
import com.max.app.contextlearning.services.SensorService;
import com.max.app.contextlearning.utilities.Constants;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String [] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int REQUEST_PERMISSION = 200;
    private boolean permissionsAccepted = false;

    private final Context context = this;
    private BottomNavigationView navBar;

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

        Log.i(Constants.TAG, "[MainActivity]: onCreate()");

        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);

        Intent sensorServiceIntent = new Intent(this, SensorService.class);
        startService(sensorServiceIntent);

        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.settings_filename), Context.MODE_PRIVATE);
        Map<String, ?> settings = sharedPref.getAll();
        Log.i(Constants.TAG, "[MainActivity]: Settings = " + settings);
        if (settings.isEmpty()) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("SensorsService", "Off");
            editor.apply();
        } else {
            String sensorsService = sharedPref.getString("SensorsService", null);
            if (sensorsService != null && sensorsService.equals("On")) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                .setContentTitle("Your current activity is")
                                .setContentText("Hello World!")
                                .setOngoing(true);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                int mId = 1;
                mNotificationManager.notify(mId, mBuilder.build());
            }
        }

        navBar = (BottomNavigationView) findViewById(R.id.navigation_bar);
        navBar.setOnNavigationItemSelectedListener(new NavigationListener());

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        HomeFragment fragment = new HomeFragment();
        fragmentTransaction.add(R.id.main_fragment, fragment);
        fragmentTransaction.commit();
    }

    /* Navigation bar listener class. */
    private class NavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Log.i(Constants.TAG, "[MainActivity]: Home Activity");
                    HomeFragment homeFragment = new HomeFragment();
                    fragmentTransaction.replace(R.id.main_fragment, homeFragment);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_activities:
                    Log.i(Constants.TAG, "[MainActivity]: Activities Activity");
                    ActivitiesFragment activitiesFragment = new ActivitiesFragment();
                    fragmentTransaction.replace(R.id.main_fragment, activitiesFragment);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_database:
                    Log.i(Constants.TAG, "[MainActivity]: Database Activity");
                    DatabaseFragment databaseFragment = new DatabaseFragment();
                    fragmentTransaction.replace(R.id.main_fragment, databaseFragment);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_settings:
                    Log.i(Constants.TAG, "[MainActivity]: Settings Activity");
                    SettingsFragment settingsFragment = new SettingsFragment();
                    fragmentTransaction.replace(R.id.main_fragment, settingsFragment);
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }
    }
}

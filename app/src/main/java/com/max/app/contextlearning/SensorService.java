package com.max.app.contextlearning;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;

public class SensorService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /* Sensor Listener Class. */
    private class MySensorListener implements SensorEventListener {

        private final int last = 5;
        private int type;
        private ArrayList<Float> light;

        private MySensorListener(int type) {
            this.type = type;
            this.light = new ArrayList<>();
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            String value;
            Intent intent = new Intent();
            switch (type) {
                case Sensor.TYPE_LIGHT:
                    if (light.size() > last)
                        light.remove(0);
                    light.add(sensorEvent.values[0]);
                    float average = 0;
                    for (float i : light)
                        average += i;
                    average /= light.size();
                    value = String.valueOf(average);
                    Log.i(Constants.TAG, value);
                    intent.setAction(Constants.ACTIONS.get("Light"));
                    intent.putExtra("LightSensor", value);
                    break;
                case Sensor.TYPE_PROXIMITY:
                    value = sensorEvent.values[0] + "";
                    Log.i("SensorService", value);
                    intent.setAction(Constants.ACTIONS.get("Proximity"));
                    intent.putExtra("ProximitySensor", value);
                    break;
                case Sensor.TYPE_GRAVITY:
                    value = sensorEvent.values[0] + " " +
                            sensorEvent.values[1] + " " +
                            sensorEvent.values[2];
                    Log.i("SensorService", value);
                    intent.setAction(Constants.ACTIONS.get("Gravity"));
                    intent.putExtra("GravitySensor", value);
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    value = sensorEvent.values[0] + " " +
                            sensorEvent.values[1] + " " +
                            sensorEvent.values[2];
                    Log.i("SensorService", value);
                    intent.setAction(Constants.ACTIONS.get("Acceleration"));
                    intent.putExtra("AccelerationSensor", value);
                    break;
            }
            sendBroadcast(intent);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        lastLocation = mLastLocation.getLatitude() + " " + mLastLocation.getLongitude();

        Log.i(Constants.TAG, "[SensorService]: " +
                mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());
    }

    @Override
    public void onConnectionSuspended(int i) {}

    public SensorManager mySensorManager;
    public MySensorListener lightListener;
    public MySensorListener proximityListener;
    public MySensorListener gravityListener;
    public MySensorListener accelerationListener;
    public AudioRecord audioRecord;
    public int buffSize;
    public SharedPreferences sharedPref;
    private final String TAG = "VOLUME SERVICE";
    private final int SAMPLE_RATE = 44100;
    private Runnable r;
    private Thread thread;
    public GoogleApiClient mGoogleApiClient;
    public String lastLocation;

    public SensorService() {
        lightListener = new MySensorListener(Sensor.TYPE_LIGHT);
        proximityListener = new MySensorListener(Sensor.TYPE_PROXIMITY);
        gravityListener = new MySensorListener(Sensor.TYPE_GRAVITY);
        accelerationListener = new MySensorListener(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(Constants.TAG, "[SensorService]: onStartCommand()");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.disconnect();

        mySensorManager = (SensorManager) getSystemService(android.content.Context.SENSOR_SERVICE);
        sharedPref = getSharedPreferences(
                getString(R.string.settings_filename),
                Context.MODE_PRIVATE);

        if (r == null) {
            r = new Runnable(){
                private String sensorsServiceStatus;
                private int prevSensorsStatus;

                public void run() {
                    Log.i(Constants.TAG, "[SensorServiceRunnable]: run()");

                    /* Registering sensors listeners. */
                    sensorsServiceStatus = sharedPref.getString("SensorsService", null);
                    Log.i(Constants.TAG, "[SensorServiceRunnable]: " + sensorsServiceStatus);
                    prevSensorsStatus = 0;
                    if (sensorsServiceStatus != null && sensorsServiceStatus.equals("On")) {
                        prevSensorsStatus = 1;
                        registerSensors();
                        Log.i(TAG, "Sensor listeners registered");
                    }
                    /* Reading sensors in background. */
                    while (true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        readSensors();
                    }
                }

                public void readSensors() {
                    sensorsServiceStatus = sharedPref.getString("SensorsService", null);
                    if (sensorsServiceStatus != null && sensorsServiceStatus.equals("Off")) {
                        if (prevSensorsStatus == 1) {
                            unregisterSensors();
                            Log.i(TAG, "Sensor listeners unregistered");
                            prevSensorsStatus = 0;
                        }
                    } else if (sensorsServiceStatus != null && sensorsServiceStatus.equals("On")) {
                        if (prevSensorsStatus == 0) {
                            registerSensors();
                            Log.i(TAG, "Sensor listeners registered");
                            prevSensorsStatus = 1;
                        }
                        /* Read noise level */
                        short [] buffer = new short[buffSize / 2];
                        audioRecord.read(buffer, 0, buffSize / 2);
                        double rms = 0;
                        for (int i : buffer) {
                            rms += i * i;
                        }
                        rms = Math.sqrt(rms / buffer.length);
                        double db = 20 * Math.log10(rms);
                        Log.i(TAG, String.valueOf(db));
                        Intent noiseIntent = new Intent();
                        noiseIntent.setAction(Constants.ACTIONS.get("Noise"));
                        noiseIntent.putExtra("NoiseSensor", db);
                        sendBroadcast(noiseIntent);

                        Intent locationIntent = new Intent();
                        locationIntent.setAction(Constants.ACTIONS.get("Location"));
                        locationIntent.putExtra("LocationSensor", lastLocation);
                        sendBroadcast(locationIntent);
                    }
                }
            };
            thread = new Thread(r);
            thread.start();
        }
        return Service.START_STICKY;
    }

    public void registerSensors() {
        mySensorManager.registerListener(
                lightListener,
                mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(
                proximityListener,
                mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(
                gravityListener,
                mySensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_NORMAL);
        mySensorManager.registerListener(
                accelerationListener,
                mySensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        buffSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffSize);
        audioRecord.startRecording();
        mGoogleApiClient.connect();
    }

    public void unregisterSensors() {
        mySensorManager.unregisterListener(lightListener);
        mySensorManager.unregisterListener(proximityListener);
        mySensorManager.unregisterListener(gravityListener);
        mySensorManager.unregisterListener(accelerationListener);
        try {
            audioRecord.stop();
            audioRecord.release();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        mGoogleApiClient.disconnect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterSensors();
        Log.d(TAG, "Sensor listeners unregistered");
        super.onDestroy();
    }
}

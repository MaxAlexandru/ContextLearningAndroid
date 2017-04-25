package com.example.max.contextlearning;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Max on 3/6/2017.
 */

public class VolumeService extends Service {

    public class MySensorListener implements SensorEventListener {

        private int type;

        private MySensorListener(int type) {
            this.type = type;
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            String value = sensorEvent.values[0] + "";
            Log.i("SensorService", value);

            Intent intent = new Intent();
            switch (type) {
                case Sensor.TYPE_LIGHT:
                    intent.setAction(Constants.ACTIONS.get("Light"));
                    intent.putExtra("LightSensor", value);
                    break;
                case Sensor.TYPE_PROXIMITY:
                    intent.setAction(Constants.ACTIONS.get("Proximity"));
                    intent.putExtra("ProximitySensor", value);
                    break;
            }
            sendBroadcast(intent);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    public SensorManager mySensorManager;
    public MySensorListener lightListener, proximityListener;
    private final String TAG = "VOLUME SERVICE";

    public VolumeService() {
        lightListener = new MySensorListener(Sensor.TYPE_LIGHT);
        proximityListener = new MySensorListener(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Service has started");

        final Context context = this;
        final SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.settings_filename), Context.MODE_PRIVATE);
        final String mFileName = getExternalCacheDir().getAbsolutePath() + "/audiorecordtest.3gp";

        mySensorManager = (SensorManager) getSystemService(android.content.Context.SENSOR_SERVICE);

        Runnable r = new Runnable(){
            public void run() {
                /* Registering sensors listeners. */
                String sensorsServiceStatus = sharedPref.getString("SensorsService", null);
//                Log.i(TAG, sensorsServiceStatus);
                int prevSensorsStatus = 0;
                if (sensorsServiceStatus != null && sensorsServiceStatus.equals("On")) {
                    prevSensorsStatus = 1;
                    mySensorManager.registerListener(
                            lightListener,
                            mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                            SensorManager.SENSOR_DELAY_NORMAL);
                    mySensorManager.registerListener(
                            proximityListener,
                            mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                            SensorManager.SENSOR_DELAY_NORMAL);
                    Log.i(TAG, "Sensor listeners registered");
                }

                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    sensorsServiceStatus = sharedPref.getString("SensorsService", null);
                    if (sensorsServiceStatus != null && sensorsServiceStatus.equals("Off")) {
                        if (prevSensorsStatus == 1) {
                            mySensorManager.unregisterListener(lightListener);
                            mySensorManager.unregisterListener(proximityListener);
                            Log.i(TAG, "Sensor listeners unregistered");
                            prevSensorsStatus = 0;
                        }
                    } else if (sensorsServiceStatus != null && sensorsServiceStatus.equals("On")) {
                        if (prevSensorsStatus == 0) {
                            mySensorManager.registerListener(
                                    lightListener,
                                    mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                                    SensorManager.SENSOR_DELAY_NORMAL);
                            mySensorManager.registerListener(
                                    proximityListener,
                                    mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                                    SensorManager.SENSOR_DELAY_NORMAL);
                            Log.i(TAG, "Sensor listeners registered");
                            prevSensorsStatus = 1;
                        }
                    }

//                    String autoVolume = sharedPref.getString("AutoVolume", null);
//                    if (autoVolume != null && autoVolume.equals("On")) {
//
//                    /* Starts recording for 5 seconds. */
//                    final MediaRecorder mRecorder = new MediaRecorder();
//                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                    mRecorder.setOutputFile(mFileName);
//                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                    mRecorder.setMaxDuration(1000);
//
//                    try {
//                        mRecorder.prepare();
//                    } catch (IOException e) {
//                        Log.e("Error", "prepare() failed");
//                    }
//
//                    mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
//                        @Override
//                        public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
//                            int amp = mRecorder.getMaxAmplitude();
//
//                            mRecorder.stop();
//                            mRecorder.release();
//
//                            ContextDbHelper contextDb = new ContextDbHelper(context);
//                            ArrayList<String> data = contextDb.getAll();
//
//                            int vol = 1;
//                            int dist = 50000;
//                            for (String item : data) {
//                                String[] cols = item.split("\\|");
//                                int noise = Integer.parseInt(cols[1]);
//                                int prevVol = Integer.parseInt(cols[2]);
//                                int newDist = Math.abs(amp - noise);
//                                if (newDist < dist) {
//                                    dist = newDist;
//                                    vol = prevVol;
//                                }
//                            }
//                            AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
//                            am.setStreamVolume(AudioManager.STREAM_RING, vol, 0);
//
//                            Log.i("VOLSERVICE", amp + "");
//                        }
//                    });
//                    mRecorder.start();
//                    mRecorder.getMaxAmplitude();
//                    }
                }
            }
        };
        Thread volThread = new Thread(r);
        volThread.start();

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mySensorManager.unregisterListener(lightListener);
        mySensorManager.unregisterListener(proximityListener);
        Log.i(TAG, "Sensor listeners unregistered");
        super.onDestroy();
    }
}

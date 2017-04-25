package com.example.max.contextlearning;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.provider.MediaStore;
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
    private final int SAMPLE_RATE = 44100;

    public VolumeService() {
        lightListener = new MySensorListener(Sensor.TYPE_LIGHT);
        proximityListener = new MySensorListener(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Service has started");

        final SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.settings_filename), Context.MODE_PRIVATE);

        mySensorManager = (SensorManager) getSystemService(android.content.Context.SENSOR_SERVICE);

        final int buffSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        final AudioRecord audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffSize);

        Runnable r = new Runnable(){
            private String sensorsServiceStatus;
            private int prevSensorsStatus;

            public void run() {
                /* Registering sensors listeners. */
                sensorsServiceStatus = sharedPref.getString("SensorsService", null);
                prevSensorsStatus = 0;
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

                audioRecord.startRecording();

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
                        mySensorManager.unregisterListener(lightListener);
                        mySensorManager.unregisterListener(proximityListener);
                        audioRecord.stop();
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
                        audioRecord.startRecording();
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

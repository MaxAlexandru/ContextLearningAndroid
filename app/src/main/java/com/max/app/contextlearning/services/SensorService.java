package com.max.app.contextlearning.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.max.app.contextlearning.R;
import com.max.app.contextlearning.algorithms.DecisionTree;
import com.max.app.contextlearning.algorithms.DecisionTreeHelper;
import com.max.app.contextlearning.database.DataSetDbHelper;
import com.max.app.contextlearning.database.TrainingSetDbHelper;
import com.max.app.contextlearning.listeners.MySensorListener;
import com.max.app.contextlearning.utilities.Constants;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SensorService extends Service {

    public SensorManager mySensorManager;
    public MySensorListener lightListener;
    public MySensorListener proximityListener;
    public MySensorListener gravityListener;
    public MySensorListener accelerationListener;
    public AudioRecord audioRecord;
    public int buffSize;
    public SharedPreferences sharedPref;
    private final int SAMPLE_RATE = 44100;
    private Runnable r, r2;
    public BroadcastReceiver br;

    public SensorService() {
        Log.i(Constants.TAG, "[SensorService]: SensorService()");
        lightListener = new MySensorListener(Sensor.TYPE_LIGHT, this);
        proximityListener = new MySensorListener(Sensor.TYPE_PROXIMITY, this);
        gravityListener = new MySensorListener(Sensor.TYPE_GRAVITY, this);
        accelerationListener = new MySensorListener(Sensor.TYPE_LINEAR_ACCELERATION, this);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(Constants.TAG, "[SensorService]: onStartCommand()");

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

                private void readSensors() {
                    sensorsServiceStatus = sharedPref.getString("SensorsService", null);
                    if (sensorsServiceStatus != null && sensorsServiceStatus.equals("Off")) {
                        if (prevSensorsStatus == 1) {
                            unregisterSensors();
                            prevSensorsStatus = 0;
                        }
                    } else if (sensorsServiceStatus != null && sensorsServiceStatus.equals("On")) {
                        if (prevSensorsStatus == 0) {
                            registerSensors();
                            prevSensorsStatus = 1;
                        }
                        /* Read noise level */
                        short [] buffer = new short[buffSize / 2];
                        audioRecord.read(buffer, 0, buffSize / 2);
                        double rms = 0;
                        for (int i : buffer)
                            rms += i * i;
                        rms = Math.sqrt(rms / buffer.length);
                        double db = 20 * Math.log10(rms);
                        Intent noiseIntent = new Intent();
                        noiseIntent.setAction(Constants.ACTIONS.get("Noise"));
                        noiseIntent.putExtra("NoiseSensor", db);
                        sendBroadcast(noiseIntent);
                    }
                }
            };
            Thread thread = new Thread(r);
            thread.start();
        }

        if (r2 == null) {
            r2 = new Runnable() {
                @Override
                public void run() {
                    final ArrayList<String> dataSet = new ArrayList<>();
                    for (int i = 0; i < Constants.SENSORS.length; i++)
                        dataSet.add("None");
                    br = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            NumberFormat formatter1 = new DecimalFormat("#0.0");
                            NumberFormat formatter2 = new DecimalFormat("#0.00");
                            String action = intent.getAction();
                            /* Light */
                            if (action.equals(Constants.ACTIONS.get("Light"))) {
                                String value = intent.getStringExtra("LightSensor");
                                dataSet.set(0, formatter1.format(Float.parseFloat(value)));
                            /* Proximity */
                            } else if (action.equals(Constants.ACTIONS.get("Proximity"))) {
                                String value = intent.getStringExtra("ProximitySensor");
                                dataSet.set(1, Constants.beautify(1, value));
                            /* Noise */
                            } else if (action.equals(Constants.ACTIONS.get("Noise"))) {
                                double value = intent.getDoubleExtra("NoiseSensor", 0.0);
                                dataSet.set(2, formatter1.format(value));
                            /* Gravity */
                            } else if (action.equals(Constants.ACTIONS.get("Gravity"))) {
                                String value = intent.getStringExtra("GravitySensor");
                                String [] sp = value.split(" ");
                                String data = "";
                                data += "X:" + String.valueOf(formatter2.format(Float.parseFloat(sp[0]))) + "  ";
                                data += "Y:" + String.valueOf(formatter2.format(Float.parseFloat(sp[1]))) + "  ";
                                data += "Z:" + String.valueOf(formatter2.format(Float.parseFloat(sp[2])));
                                dataSet.set(3, Constants.beautify(3, data));
                            /* Acceleration */
                            } else if (action.equals(Constants.ACTIONS.get("Acceleration"))) {
                                String value = intent.getStringExtra("AccelerationSensor");
                                String [] sp = value.split(" ");
                                String data = "";
                                data += "X:" + String.valueOf(formatter2.format(Float.parseFloat(sp[0]))) + "  ";
                                data += "Y:" + String.valueOf(formatter2.format(Float.parseFloat(sp[1]))) + "  ";
                                data += "Z:" + String.valueOf(formatter2.format(Float.parseFloat(sp[2])));
                                dataSet.set(4, Constants.beautify(4, data));
                            /* Battery temperature */
                            } else if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                                int t = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                                float ft = ((float) t) / 10;
                                dataSet.set(5, formatter1.format(ft));
                            }
                        }
                    };
                    IntentFilter intentFilter = new IntentFilter();
                    for (Map.Entry<String, String> entry : Constants.ACTIONS.entrySet())
                        intentFilter.addAction(entry.getValue());
                    intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
                    registerReceiver(br, intentFilter);

                    int currentDay, prevDay;
                    currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    while (true) {
                        try {
                            Thread.sleep(Constants.READ_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        /* Save reading to rawDb */
                        DataSetDbHelper rawDb = new DataSetDbHelper(getApplicationContext());
                        String sensorsServiceStatus = sharedPref.getString("SensorsService", null);
                        if (sensorsServiceStatus != null && sensorsServiceStatus.equals("On")) {
                            Calendar calendar = Calendar.getInstance();
                            int h = calendar.get(Calendar.HOUR_OF_DAY);
                            int m = calendar.get(Calendar.MINUTE);
                            int s = calendar.get(Calendar.SECOND);
                            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                            Date currentDate = null;
                            try {
                                currentDate = formatter.parse(String.valueOf(h) + ":" +
                                        String.valueOf(m) + ":" + String.valueOf(s));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long time = currentDate.getTime();
                            String codedData = Constants.codeData(dataSet);
                            rawDb.addRaw(time, codedData);
                            Log.i(Constants.TAG, "[SensorServiceRunnable]: Saved");
                        }

                        /* Clear rawDb at the end of the day */
                        prevDay = currentDay;
                        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                        if (prevDay != currentDay)
                            rawDb.deleteAllRaw();
                    }
                }
            };
            Thread thread = new Thread(r2);
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
        Log.i(Constants.TAG, "[SensorServiceRunnable]: Sensor listeners registered");
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
            Log.e(Constants.TAG, "[SensorServiceRunnable]: " + e.toString());
        }
        Log.i(Constants.TAG, "[SensorServiceRunnable]: Sensor listeners unregistered");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterSensors();
        if (br != null)
            unregisterReceiver(br);
        super.onDestroy();
    }
}

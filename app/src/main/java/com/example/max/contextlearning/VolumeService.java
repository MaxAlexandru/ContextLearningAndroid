package com.example.max.contextlearning;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    public VolumeService() {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("VOLSERVICE", "Service has started");

        final Context context = this;
        final SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.settings_filename),Context.MODE_PRIVATE);
        final String mFileName = getExternalCacheDir().getAbsolutePath() + "/audiorecordtest.3gp";

        Runnable r = new Runnable(){
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String autoVolume = sharedPref.getString("AutoVolume", null);
                    if (autoVolume != null && autoVolume.equals("On")) {

                    /* Starts recording for 5 seconds. */
                    final MediaRecorder mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setOutputFile(mFileName);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mRecorder.setMaxDuration(5000);

                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        Log.e("Error", "prepare() failed");
                    }

                    mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                        @Override
                        public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
                            int amp = mRecorder.getMaxAmplitude();

                            mRecorder.stop();
                            mRecorder.release();

                            ContextDbHelper contextDb = new ContextDbHelper(context);
                            ArrayList<String> data = contextDb.getAll();

                            int vol = 1;
                            int dist = 50000;
                            for (String item : data) {
                                String[] cols = item.split("\\|");
                                int noise = Integer.parseInt(cols[1]);
                                int prevVol = Integer.parseInt(cols[2]);
                                int newDist = Math.abs(amp - noise);
                                if (newDist < dist) {
                                    dist = newDist;
                                    vol = prevVol;
                                }
                            }
                            AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
                            am.setStreamVolume(AudioManager.STREAM_RING, vol, 0);

                            Log.i("VOLSERVICE", amp + "");
                        }
                    });
                    mRecorder.start();
                    mRecorder.getMaxAmplitude();
                    }
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
}

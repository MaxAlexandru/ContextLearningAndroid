package com.example.max.contextlearning;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Max on 3/6/2017.
 */

public class VolumeService extends IntentService {

    public VolumeService() {
        super("VolumeService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        System.out.println("Service has started");

        System.out.println("Here");

        final Context context = this;

        new Thread(new Runnable(){
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //REST OF CODE HERE//
                    String mFileName = getExternalCacheDir().getAbsolutePath();
                    mFileName += "/audiorecordtest.3gp";

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

                            System.out.println(amp);
                        }
                    });
                    mRecorder.start();
                    mRecorder.getMaxAmplitude();
                }
            }
        }).start();
    }
}

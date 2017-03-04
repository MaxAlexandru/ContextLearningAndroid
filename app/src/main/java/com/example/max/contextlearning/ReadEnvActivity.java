package com.example.max.contextlearning;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class ReadEnvActivity extends AppCompatActivity {

    private MediaRecorder mRecorder;
    private String mFileName;
    private int amp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_env);

        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int volLvl = am.getStreamVolume(AudioManager.STREAM_RING);
        System.out.println("Vol: " + volLvl);

        SeekBar volumeSeekBar = (SeekBar) findViewById(R.id.vol_seek_bar);
        volumeSeekBar.setMax(7);
        volumeSeekBar.setProgress(volLvl);

        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        System.out.println(mFileName);

        final Button button = (Button) findViewById(R.id.record_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setOutputFile(mFileName);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                try {
                    mRecorder.prepare();
                } catch (IOException e) {
                    Log.e("Error", "prepare() failed");
                }
                mRecorder.start();
                System.out.println("Start");
            }
        });

        final Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Stop");
                mRecorder.stop();
                mRecorder.release();

                MediaPlayer mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(mFileName);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    Log.e("Play", "prepare() failed");
                }
//                mPlayer.release();

//                amp = mRecorder.getMaxAmplitude();
//                mRecorder.release();
//                System.out.println(amp);

//                TextView amp_text_view = (TextView) findViewById(R.id.amp_text_view);
//                amp_text_view.setText(amp);
            }
        });
    }
}

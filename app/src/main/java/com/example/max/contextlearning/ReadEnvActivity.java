package com.example.max.contextlearning;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

public class ReadEnvActivity extends AppCompatActivity {

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
    }
}

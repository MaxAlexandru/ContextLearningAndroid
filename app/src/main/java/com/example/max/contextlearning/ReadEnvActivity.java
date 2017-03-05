package com.example.max.contextlearning;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static java.lang.System.currentTimeMillis;

public class ReadEnvActivity extends AppCompatActivity {

    private TextView recStateTextView;
    private ListView resultTextView;
    private SeekBar volumeSeekBar;
    private Button saveButton;
    private Button recordButton;

    private MediaRecorder mRecorder;
    private String mFileName;
    private int amp;
    private AudioManager am;

    private ContextDbHelper contextDb;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_env);

        context = this;
        contextDb = new ContextDbHelper(this);

        recStateTextView = (TextView) findViewById(R.id.amp_text_view);
        resultTextView = (ListView) findViewById(R.id.result_list_view);
        recordButton = (Button) findViewById(R.id.record_button);
        saveButton = (Button) findViewById(R.id.save_button);
        volumeSeekBar = (SeekBar) findViewById(R.id.vol_seek_bar);

        volumeSeekBar.setMax(7);
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        volumeSeekBar.setProgress(am.getStreamVolume(AudioManager.STREAM_RING));

        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        /* Starts recording for 5 seconds. */
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecorder = new MediaRecorder();
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
                mRecorder.start();
                mRecorder.getMaxAmplitude();
                recStateTextView.setText("Recording...");
                mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                    @Override
                    public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
                        amp = mRecorder.getMaxAmplitude();

                        mRecorder.stop();
                        mRecorder.release();

                        recStateTextView.setText("Finished. Noise level: " + amp + "/32768");
                        saveButton.setEnabled(true);
                    }
                });
            }
        });

        /* Saves recording amplitude, volume level and current time to sql. */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = currentTimeMillis() + "";
                contextDb.add(date, amp, volumeSeekBar.getProgress());

                saveButton.setEnabled(false);

                ArrayList<String> items = contextDb.getAll();
                if (items != null) {
                    ArrayAdapter<String> kbAdapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_list_item_1, items);

                    ListView kbListView = (ListView) findViewById(R.id.result_list_view);
                    kbListView.setAdapter(kbAdapter);
                }
            }
        });
    }
}

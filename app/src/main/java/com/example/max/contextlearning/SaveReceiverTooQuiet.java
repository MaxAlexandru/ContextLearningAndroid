package com.example.max.contextlearning;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by Max on 3/3/2017.
 */

public class SaveReceiverTooQuiet extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Save", "Too Quiet");

        AudioManager am = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        int currentVolume = am.getStreamVolume(AudioManager.STREAM_RING);

        ContextDbHelper contextDb = new ContextDbHelper(context);

        String latest = contextDb.getLatest();
        String[] cols = latest.split("\\|");

        if (currentVolume < 7)
            contextDb.updateOnVol(currentVolume + 1, cols[0], Integer.parseInt(cols[1]));

        NotificationManager manager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        manager.cancel(1);

    }
}

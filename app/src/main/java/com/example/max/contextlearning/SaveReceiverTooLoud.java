package com.example.max.contextlearning;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Max on 3/3/2017.
 */

public class SaveReceiverTooLoud extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Save", "Too Loud");

        ContextDbHelper contextDb = new ContextDbHelper(context);

        String latest = contextDb.getLatest();
        String[] cols = latest.split("\\|");

        int volLvl = Integer.parseInt(cols[2]);
        if (volLvl > 0)
            contextDb.updateOnVol(Integer.parseInt(cols[2]) - 1, cols[0], Integer.parseInt(cols[1]));

        NotificationManager manager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        manager.cancel(1);
    }
}

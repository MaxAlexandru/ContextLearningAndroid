package com.example.max.contextlearning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Max on 3/3/2017.
 */

public class SaveReceiverTooQuiet extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Save", "Too Quiet");
    }
}

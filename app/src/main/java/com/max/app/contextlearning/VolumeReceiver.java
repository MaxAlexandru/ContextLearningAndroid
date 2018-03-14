/*******************************************************************************
 * Copyright (c) 2017 Maximilian Alexandru.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.max.app.contextlearning;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Max on 2/21/2017.
 */

public class VolumeReceiver extends BroadcastReceiver {

    public Context mContext;
    private static int prev_state, prev_prev_state;
    private static final String TAG = "CustomPhoneStateListen";

    public void onReceive(Context context, Intent intent) {
        mContext = context;

        TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int state = tmgr.getCallState();

        switch(state){
            case TelephonyManager.CALL_STATE_RINGING:
                Log.d(TAG, "CALL_STATE_RINGING");
                prev_prev_state = prev_state;
                prev_state=state;
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.d(TAG, "CALL_STATE_OFFHOOK");
                prev_prev_state = prev_state;
                prev_state=state;
                break;

            case TelephonyManager.CALL_STATE_IDLE:
                Log.d(TAG, "CALL_STATE_IDLE");
                if((prev_state == TelephonyManager.CALL_STATE_OFFHOOK) &&
                        (prev_prev_state == TelephonyManager.CALL_STATE_RINGING)){
                    prev_prev_state = prev_state;
                    prev_state=state;
                    //Answered Call which is ended
                }
                if((prev_state == TelephonyManager.CALL_STATE_RINGING)){
                    prev_prev_state = prev_state;
                    prev_state=state;
                    //Rejected or Missed call
                }
                break;
        }
    }
}

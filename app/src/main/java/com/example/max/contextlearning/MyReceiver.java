package com.example.max.contextlearning;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Max on 2/21/2017.
 */

public class MyReceiver extends BroadcastReceiver {

    public Context mContext;
    public String incoming_number;
    private int prev_state;

    public void onReceive(Context context, Intent intent) {
        mContext = context;
        try {
            TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
            tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }

    }

    private class MyPhoneStateListener extends PhoneStateListener {

        private static final String TAG = "CustomPhoneStateListen";

        public void onCallStateChanged(int state, String incomingNumber) {

            if( incomingNumber != null && incomingNumber.length() > 0 )
                incoming_number = incomingNumber;

            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d(TAG, "CALL_STATE_RINGING");
                    prev_state=state;
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(TAG, "CALL_STATE_OFFHOOK");
                    prev_state=state;
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(TAG, "CALL_STATE_IDLE==>"+incoming_number);
                    if((prev_state == TelephonyManager.CALL_STATE_OFFHOOK)){
                        prev_state=state;
                        //Answered Call which is ended
                        addNotification();
                    }
                    if((prev_state == TelephonyManager.CALL_STATE_RINGING)){
                        prev_state=state;
                        //Rejected or Missed call
                        addNotification();
                    }
                    break;
            }
        }

        private void addNotification() {
            /* Perfect Intent */
            Intent intent1 = new Intent(mContext, SaveReceiverPerfect.class);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(mContext, 0, intent1,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            /* Too Quiet Intent */
            Intent intent2 = new Intent(mContext, SaveReceiverTooQuiet.class);
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(mContext, 0, intent2,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            /* Too Loud Intent */
            Intent intent3 = new Intent(mContext, SaveReceiverTooLoud.class);
            PendingIntent pendingIntent3 = PendingIntent.getBroadcast(mContext, 0, intent3,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            /* https://creativecommons.org/licenses/by/3.0/ */
            NotificationCompat.Builder notif = new NotificationCompat.Builder(mContext);
            notif.setAutoCancel(true);
            notif.setSmallIcon(R.drawable.notification);
            notif.setContentTitle("Volume level");
            notif.setContentText("How was the volume level of your last call?");
            notif.addAction(0, "Perfect", pendingIntent1);
            notif.addAction(0, "Too Quiet", pendingIntent2);
            notif.addAction(0, "Too Loud", pendingIntent3);

            // Add as notification
            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, notif.build());
        }
    }
}

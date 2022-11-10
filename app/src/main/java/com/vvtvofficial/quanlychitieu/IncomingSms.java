package com.vvtvofficial.quanlychitieu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class IncomingSms extends BroadcastReceiver {
    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (Object o : pdusObj) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) o);

                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);


                    // Show Alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,
                            "senderNum: "+ senderNum + ", message: " + message, duration);
                    toast.show();
                    Intent msgrcv = new Intent("Msg");
                    msgrcv.putExtra("package", "");
                    msgrcv.putExtra("ticker", senderNum);
                    msgrcv.putExtra("title", senderNum);
                    msgrcv.putExtra("text", message);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
                } // end for loop
            } // bundle is null
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
        }
    }
}

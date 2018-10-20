package com.example.patrick.firenav;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    public static final String SMS_BUNDLE = "pdus";
    public static final String SpecificNum = "+38640151281";
    public String address;
    private static String TAG = "SMSBROADCAST";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        Log.i(TAG, "onreceive");

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            Log.i(TAG, sms.toString());
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);

                address = smsMessage.getOriginatingAddress();
                String smsBody = smsMessage.getMessageBody().toString();
                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
                Log.d("DREK", "TEST2");
            }
            Toast.makeText(context, "Message Received!", Toast.LENGTH_SHORT).show();
            if (MainActivity.active) {
                if(address.equals(SpecificNum)){
                    MainActivity inst = MainActivity.instance();
                    inst.refreshSmsInbox();
                }
            } else if(address.equals(SpecificNum)) {
                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}

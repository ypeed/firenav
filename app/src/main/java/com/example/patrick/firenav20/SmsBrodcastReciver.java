package com.example.patrick.firenav20;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by Patrick on 18. 12. 2017.
 */
public class SmsBrodcastReciver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";
    public static final String SpecificNum = "+38629290543";
    public String address;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                String format = intentExtras.getString("format");
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);

                address = smsMessage.getOriginatingAddress();
                String smsBody = smsMessage.getMessageBody().toString();
                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
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

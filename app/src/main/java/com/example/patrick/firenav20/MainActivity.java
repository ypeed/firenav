package com.example.patrick.firenav20;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    SmsManager smsManager = SmsManager.getDefault();
    private static MainActivity inst;
    public static boolean active = false;
    TextView SMS;
    EditText editText;
    Button btnIsci;
    ListView listView;
    ArrayList<String> arrayNaslovi = new ArrayList<>();
    CustomAdapter adapter;
    public static MainActivity instance() {
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SMS = findViewById(R.id.smsSporocilo);
        listView = findViewById(R.id.listView);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            refreshSmsInbox();
        }

        //GUMB ISCI.............................................
        editText = findViewById(R.id.editText);
        btnIsci = findViewById(R.id.btnIsci);
        btnIsci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMaps(editText.getText().toString());
            }
        });
        //GUMB ISCI.............................................


        String parts[] = refreshSmsInbox().split(":");
        String part1 = parts[0].toLowerCase();

        String sporocilo = findNumber(part1);
        if(!sporocilo.equals("NAPAKA")){

            final String arrNas[] = sporocilo.split(" ");
            String dela = "";

            for(int i=arrNas.length-1; i>0; i--) {
                dela = " " + arrNas[i] + dela;
                arrayNaslovi.add(dela);
            }

            adapter = new CustomAdapter(this,arrayNaslovi);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    openMaps((String) listView.getItemAtPosition(i));
                }
            });

        }else{
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
            builder.setTitle("Neveljavni naslov");
            builder.setMessage(part1)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog aler = builder.create();
            aler.show();
        }
    }

    public String findNumber(String sporocilo){
        int x = 0;
        int y = 0;
        String sms;
        for(int i=0; i<sporocilo.length(); i++){
            if(Character.isDigit(sporocilo.charAt(i))) {
                x = i;
            }
        }
        if(x != 0){
            for(int j=x; j<sporocilo.length(); j++){
                if(sporocilo.charAt(j) == ' '){
                    y=j;
                    break;
                }
            }
        }else if (x == 0){
            sms = "NAPAKA";
            return sms;
        }
        sms = sporocilo.substring(0,y);

        return sms;
    }

    public String createUrl(String naslov) {
        String UrlNaslov = naslov.replace(' ', '+');
        System.out.println(UrlNaslov);
        String delaj = "https://www.google.com/maps/dir/?api=1&destination=Lovrenc+na+Pohorju+," + UrlNaslov + "&travelmode=driving";
        return delaj;
    }

    public void openMaps(String naslov){
        Uri gmmIntentUri = Uri.parse(createUrl(naslov));
        System.out.println(gmmIntentUri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    READ_SMS_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                refreshSmsInbox();
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public String refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        //ADDRESS: int indexAddress = smsInboxCursor.getColumnIndex("address");
        smsInboxCursor.moveToFirst();
        String str = smsInboxCursor.getString(indexBody) + "\n";
        Log.d("DELA",str);
        SMS.setText(str);
        return str;

    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

}

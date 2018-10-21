package com.example.patrick.firenav2;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    public EditText editText;
    public Button btnIsci;
    public ListView listView;
    public ArrayList<String> arrayNaslovi = new ArrayList<>();
    CustomAdapter adapter;

    public TextView SMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SMS = findViewById(R.id.smsSporocilo);
        listView = findViewById(R.id.listView);
        editText = findViewById(R.id.editText);
        btnIsci = findViewById(R.id.btnIsci);

        //pogledamo za permissne
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            //prebere inbox
            readFromSmsInbox();


            //NAVIGATION
            BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            //Toast.makeText(MyActivity.this,"HOME", Toast.LENGTH_LONG).show();
                            return true;
                        case R.id.navigation_pridejo:
                            //Intent intent = new Intent(MyActivity.this,Pridejo.class);
                            //startActivity(intent);
                            return true;
                        case R.id.navigation_nepridejo:
                            //Toast.makeText(MyActivity.this,"NE PRIDEJO", Toast.LENGTH_LONG).show();
                            return true;
                    }
                    return false;
                }
            });


            //gumb isci
            btnIsci.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMaps(editText.getText().toString());
                }
            });

            String parts[] = readFromSmsInbox().split(":");
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
                final AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this, R.style.Theme_AppCompat_Dialog);
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

    public String readFromSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = cursor.getColumnIndex("body");
        String sms = new String();
        if (indexBody < 0 || !cursor.moveToFirst())  return null;
        if(cursor.moveToFirst()) {
            sms = cursor.getString(indexBody).toString();
        }
        cursor.close();
        SMS.setText(sms);
        return sms;
    }
}
package com.example.amanda.howler;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.example.amanda.howler.database.Contact;
import com.example.amanda.howler.database.DatabaseHelper;
import com.example.amanda.howler.aws.AWSHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner spinner1, spinner2;
    private Button startTrackingButton;
    private EditText startLocation;
    private EditText endLocation;
    private EditText timerHoursEditText;
    private EditText timerMinutesEditText;
    private int timerStart;
    private int timerHours;
    private int timerMinutes;
    private ImageButton addContactsButton;
    public DatabaseHelper db;
    Intent i;
    public String userFullName;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private HandlerThread mBackgroundThread; // An additional thread for running tasks that shouldn't block the UI.
    private Handler mBackgroundHandler; // A Handler for running tasks in the background.

    public String mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkLocationPermission();

        //Widget References
        startTrackingButton = (Button) findViewById(R.id.startTrackingButton);
        startLocation = (EditText) findViewById(R.id.startLocationEditText);
        endLocation = (EditText) findViewById(R.id.endLocationEditText);
        timerHoursEditText = (EditText) findViewById(R.id.hourEditText);
        timerMinutesEditText = (EditText) findViewById(R.id.minuteEditText);
        addContactsButton = (ImageButton) findViewById(R.id.addContactsButton);

        //Listeners
        startTrackingButton.setOnClickListener(this);
        addContactsButton.setOnClickListener(this);


        //Variables
        timerHours = 0;
        timerMinutes = 0;

        //Database
        db = new DatabaseHelper(this);



//
//        userFullName = getUserFullName();
//        Log.d("getUserFullName:Main", userFullName);




        //Functions
//        addSpinnerItems();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d("LOCATION", "onResume: PERMISSION GRANTED");
            }
        }

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("TITLE")
                        .setMessage("MESSAGE")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {
                    checkLocationPermission();
                }
                return;
            }

        }
    }

    //Adds contacts to dropdown
    public void addSpinnerItems() {
        //Get spinners
//        spinner1 = (Spinner) findViewById(R.id.contactListSpinner1);
//        spinner2 = (Spinner) findViewById(R.id.contactListSpinner2);

        //Create contact list 1
        List<Contact> contacts = db.getAllContacts();
        List<String> contactList1 = new ArrayList<String>();
        contactList1.add("-- Select Primary Contact --");
        for (Contact cn : contacts) {
            contactList1.add(cn.getName());
        }

        //Create contact list 2
        final List<String> contactList2 = new ArrayList<>();
        contactList2.add("-- Select Secondary Contact --");
        for (Contact cn : contacts) {
//            if (!cn.getName().equals(spinner1.getSelectedItem().toString())) {
            contactList2.add(cn.getName());
//            }
        }

        //Create array adapters
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, contactList1);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, contactList2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set spinner items
        spinner1.setAdapter(dataAdapter1);
        spinner2.setAdapter(dataAdapter2);

    }

    //OnClick Functions
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startTrackingButton:
                //Compute timer time
                timerHours = Integer.parseInt(timerHoursEditText.getText().toString());
                timerMinutes = Integer.parseInt(timerMinutesEditText.getText().toString());
                timerStart = timerHours * 3600000 + timerMinutes * 60000;
                //Send timer time to next activity
                i = new Intent(getApplicationContext(), TrackingTimer.class);
                i.putExtra("TIMER_VAL", timerStart);
                i.putExtra("USER_NAME", userFullName);
                startActivity(i);
                break;
            case R.id.addContactsButton:
                i = new Intent(getApplicationContext(), ContactList.class);
                startActivity(i);
                break;
        }
    }


}

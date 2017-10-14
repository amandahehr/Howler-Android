package com.example.amanda.howler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Widget References
        startTrackingButton = (Button) findViewById(R.id.startTrackingButton);
        startLocation = (EditText) findViewById(R.id.startLocationEditText);
        endLocation = (EditText) findViewById(R.id.endLocationEditText);
        timerHoursEditText = (EditText) findViewById(R.id.hourEditText);
        timerMinutesEditText = (EditText) findViewById(R.id.minuteEditText);

        //Listeners
        startTrackingButton.setOnClickListener(this);

        //Variables
        timerHours = 0;
        timerMinutes = 0;

        //Functions
        addSpinnerItems();
    }

    //Adds contacts to dropdown
    public void addSpinnerItems() {
        //Get spinners
        spinner1 = (Spinner) findViewById(R.id.contactListSpinner1);
        spinner2 = (Spinner) findViewById(R.id.contactListSpinner2);

        //Create contact list
        List<String> contactList1 = new ArrayList<String>();
        contactList1.add("-- Select Primary Contact --");
        contactList1.add("Mom");
        contactList1.add("Dad");
        contactList1.add("Sarah");
        contactList1.add("Joe");

        //Create array adapters
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, contactList1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set spinner items
        spinner1.setAdapter(dataAdapter);
    }

    //OnClick Functions
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startTrackingButton:
                //Compute timer time
                timerHours = Integer.parseInt(timerHoursEditText.getText().toString());
                timerMinutes = Integer.parseInt(timerMinutesEditText.getText().toString());
                timerStart = timerHours*3600000 + timerMinutes*60000;
                //Send timer time to next activity
                Intent i = new Intent(getApplicationContext(), TrackingTimer.class);
                i.putExtra("TIMER_VAL", timerStart);
                startActivity(i);
                break;
        }
    }
}

package com.howlersafe.howler;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.howlersafe.howler.R;
import com.howlersafe.howler.database.Contact;
import com.howlersafe.howler.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Widget declarations
    Button startTrackingButton;
    public EditText startLocation;
    public EditText endLocation;
    private TextView timerHoursText;
    private TextView timerMinutesText;
    private TextView emergencyContact;
    public Button timerPopupButton;
    ImageButton addContactsButton;
    //timer input popup
    public TextView hourTenTextView, hourOneTextView, minTenTextView, minOneTextView;
    public Button digitButton1, digitButton2, digitButton3, digitButton4, digitButton5, digitButton6, digitButton7, digitButton8, digitButton9, digitButton0;
    public Button cancelButton;
    public ImageButton backspaceButton;
    public FloatingActionButton setTimeButton;
    private RelativeLayout mRelativeLayout;
    public View popupView;
    //Variable declarations
    public static String startLocationString;
    public static String endLocationString;
    int timerStart;
    private int timerHours;
    private int timerMinutes;
    public DatabaseHelper db;
    Intent i;
    public String userFullName;
    public String contactText;
    AlertDialog alert;
    //for adapters
    ArrayList<String> listItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayList<String> contactList = new ArrayList<>();
    ArrayList<String> contactIDList = new ArrayList<>();
    public static ArrayList<String> selectedContactsList = new ArrayList<>();
    public static ArrayList<String> selectedNamesList = new ArrayList<>();
    ArrayList<String> selectedItem = new ArrayList<>();
    //for location permission
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //for popup
    private PopupWindow mPopupWindow;
    public LayoutInflater inflater;
    ArrayList<String> timerInputList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Reset selected contacts
        selectedContactsList.clear();
        selectedNamesList.clear();
        selectedItem.clear();

        //check if we have location permission
        checkLocationPermission();

        //Timer input popup
        // Initialize a new instance of LayoutInflater service
        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        popupView = inflater.inflate(R.layout.timer_input_popup,null);


        //Widget References
        startTrackingButton = findViewById(R.id.startTrackingButton);
        startLocation = findViewById(R.id.startLocationEditText);
        endLocation = findViewById(R.id.endLocationEditText);
        timerHoursText = findViewById(R.id.hourEditText);
        timerMinutesText = findViewById(R.id.minuteEditText);
        addContactsButton = findViewById(R.id.addContactsButton);
        emergencyContact = findViewById(R.id.emergencyContactSelected);
        mRelativeLayout = findViewById(R.id.rl);
        timerPopupButton = findViewById(R.id.timerPopupButton);
        //in popup
        backspaceButton = popupView.findViewById(R.id.backspaceButton);
        cancelButton = popupView.findViewById(R.id.cancelButton);
        setTimeButton = popupView.findViewById(R.id.setTimeButton);
        digitButton0 = popupView.findViewById(R.id.digitButton0);
        digitButton1 = popupView.findViewById(R.id.digitButton1);
        digitButton2 = popupView.findViewById(R.id.digitButton2);
        digitButton3 = popupView.findViewById(R.id.digitButton3);
        digitButton4 = popupView.findViewById(R.id.digitButton4);
        digitButton5 = popupView.findViewById(R.id.digitButton5);
        digitButton6 = popupView.findViewById(R.id.digitButton6);
        digitButton7 = popupView.findViewById(R.id.digitButton7);
        digitButton8 = popupView.findViewById(R.id.digitButton8);
        digitButton9 = popupView.findViewById(R.id.digitButton9);
        hourTenTextView = popupView.findViewById(R.id.hourTenTextView);
        hourOneTextView = popupView.findViewById(R.id.hourOneTextView);
        minTenTextView = popupView.findViewById(R.id.minTenTextView);
        minOneTextView = popupView.findViewById(R.id.minOneTextView);

        //Listeners
        startTrackingButton.setOnClickListener(this);
        addContactsButton.setOnClickListener(this);
        emergencyContact.setOnClickListener(this);
        timerPopupButton.setOnClickListener(this);
        //in popup
        backspaceButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        setTimeButton.setOnClickListener(this);
        digitButton0.setOnClickListener(this);
        digitButton1.setOnClickListener(this);
        digitButton2.setOnClickListener(this);
        digitButton3.setOnClickListener(this);
        digitButton4.setOnClickListener(this);
        digitButton5.setOnClickListener(this);
        digitButton6.setOnClickListener(this);
        digitButton7.setOnClickListener(this);
        digitButton8.setOnClickListener(this);
        digitButton9.setOnClickListener(this);

        //Variables
        timerHours = 0;
        timerMinutes = 0;

        //Database
        db = new DatabaseHelper(this);

        //Get contacts
        List<Contact> contacts = db.getAllContacts();
        contactList = new ArrayList<>();
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, listItems);
        for (Contact cn : contacts) {
            adapter.add(cn.getName());
            contactList.add(cn.getName());
            contactIDList.add(String.valueOf(cn.getID()));
        }
        if (contacts.size() == 0) {
            adapter.add("No contacts added");
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                        .setTitle("We need your location!")
                        .setMessage("Howler uses your location to send to your contacts in case of emergency. We only get your location if and when the timer runs out.")
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
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {}
                } else {
                    checkLocationPermission();
                }
            }
        }
    }

    //OnClick Functions
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startTrackingButton:
                if (selectedItem.size() == 0){
                Toast.makeText(this, "Select an emergency contact", Toast.LENGTH_LONG).show();
                }
                else if (startLocation.getText().toString().equals("")) {
                    Toast.makeText(this, "Enter start location", Toast.LENGTH_LONG).show();
                }
                else if (endLocation.getText().toString().equals("")){
                    Toast.makeText(this, "Enter end location", Toast.LENGTH_LONG).show();
                }
                else if (timerMinutesText.getText().toString().equals("00") && timerHoursText.getText().toString().equals("00")) {
                    Toast.makeText(this, "Fill in estimated time", Toast.LENGTH_LONG).show();
                }

                else {
                    //Set location strings
                    startLocationString = startLocation.getText().toString();
                    endLocationString = endLocation.getText().toString();
                    //Compute timer time
                    timerHours = Integer.parseInt(timerHoursText.getText().toString());
                    timerMinutes = Integer.parseInt(timerMinutesText.getText().toString());
                    timerStart = timerHours * 3600000 + timerMinutes * 60000;
                    for (String position : selectedItem) {
                        String id = contactIDList.get(Integer.parseInt(position));
                        Contact thisContact = db.getContact(Integer.parseInt(id));
                        selectedContactsList.add(thisContact.getPhoneNumber());
                        selectedNamesList.add(thisContact.getName());
                    }
                    //Send timer time to next activity
                    i = new Intent(getApplicationContext(), TrackingTimer.class);
                    i.putExtra("TIMER_VAL", timerStart);
                    i.putExtra("USER_NAME", userFullName);
                    startActivity(i);
                }
                break;
            case R.id.addContactsButton:
                i = new Intent(getApplicationContext(), ContactList.class);
                startActivity(i);
                break;
            case R.id.emergencyContactSelected:
                selectedItem = new ArrayList<>();
                createPopup();
                break;
            case R.id.timerPopupButton:
                createTimerInputPopup();
                break;
            case R.id.cancelButton:
                mPopupWindow.dismiss();
                break;
            case R.id.backspaceButton:
                deleteTimerArrayItem();
                break;
            case R.id.digitButton0:
                addNumberToTimer("0");
                break;
            case R.id.digitButton1:
                addNumberToTimer("1");
                break;
            case R.id.digitButton2:
                addNumberToTimer("2");
                break;
            case R.id.digitButton3:
                addNumberToTimer("3");
                break;
            case R.id.digitButton4:
                addNumberToTimer("4");
                break;
            case R.id.digitButton5:
                addNumberToTimer("5");
                break;
            case R.id.digitButton6:
                addNumberToTimer("6");
                break;
            case R.id.digitButton7:
                addNumberToTimer("7");
                break;
            case R.id.digitButton8:
                addNumberToTimer("8");
                break;
            case R.id.digitButton9:
                addNumberToTimer("9");
                break;
            case R.id.setTimeButton:
                setTimerValue();
                mPopupWindow.dismiss();
                break;
        }
    }

    public void createPopup(){
        selectedItem.clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Emergency Contact")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                contactText = "";
                if (selectedItem.size() > 0) {
                    for (int i=0; i<selectedItem.size()-1; i++) {
                        contactText += contactList.get(Integer.parseInt(selectedItem.get(i))) + ", ";
                    }
                    contactText += contactList.get(Integer.parseInt(selectedItem.get(selectedItem.size()-1)));
                    emergencyContact.setText(contactText);
                }
                else {
                    emergencyContact.setText(R.string.select_emergency_contact);
                }
            }
        });


        alert = builder.create();
        alert.getListView().setItemsCanFocus(false);
        alert.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        alert.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Manage selected items here
                selectedItem.clear();
                for (int i=0; i<alert.getListView().getCount(); i++) {
                    if (alert.getListView().isItemChecked(i)) {
                        selectedItem.add(String.valueOf(i));
                    }
                }
                for (String item : selectedItem){
                    Log.d("SELECTED ITEMS", item);

                }
            }
        });
        alert.show();
    }

    public void createTimerInputPopup() {

        timerInputList.clear();
        timerInputList.add("0");
        timerInputList.add("0");
        timerInputList.add("0");
        timerInputList.add("0");

        updateTimerTextViews();

        mPopupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }
        // Finally, show the popup window at the center location of root relative layout
        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
    }

    public void addNumberToTimer(String num){
        if(Objects.equals(timerInputList.get(0), "0")) {
            timerInputList.remove(0);
            timerInputList.add(num);
            updateTimerTextViews();
        }
    }

    public void updateTimerTextViews() {
        hourTenTextView.setText(timerInputList.get(0));
        hourOneTextView.setText(timerInputList.get(1));
        minTenTextView.setText(timerInputList.get(2));
        minOneTextView.setText(timerInputList.get(3));
    }

    public void deleteTimerArrayItem() {
        for (int i=3; i>0; i--){
            timerInputList.set(i, timerInputList.get(i-1));
        }
        timerInputList.set(0, "0");
        updateTimerTextViews();
    }

    public void setTimerValue() {
        String hours = timerInputList.get(0)+timerInputList.get(1);
        String minutes = timerInputList.get(2)+timerInputList.get(3);
        timerHoursText.setText(hours);
        timerMinutesText.setText(minutes);
    }

    @Override
    public void onBackPressed() {
    }

}

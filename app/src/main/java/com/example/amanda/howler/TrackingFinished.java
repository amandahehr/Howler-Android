package com.example.amanda.howler;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.example.amanda.howler.aws.AWSHelper;

import java.util.HashMap;
import java.util.Map;

import static com.example.amanda.howler.aws.AWSHelper.getCognitoUser;

public class TrackingFinished extends AppCompatActivity implements View.OnClickListener {

    public Button returnButton;
    public int finishReason;
    public String userName= "[DEFAULT]";
    public TextView finishReasonText;

    LocationManager locationManager;
    String provider;

    private HandlerThread mBackgroundThread; // An additional thread for running tasks that shouldn't block the UI.
    private static Handler mBackgroundHandler; // A Handler for running tasks in the background.

    public static String SMSMessage;

    double myLatitude = 0;
    double myLongitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_finished);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION", "onCreate: NO PERMISSION");
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            Log.i("Location Info", "Location achieved!");
        } else {
            Log.i("Location Info", "No location :(");
        }


        //Get widget references
        returnButton = (Button) findViewById(R.id.returnFromTimerFinishButton);
        finishReasonText = (TextView) findViewById(R.id.finishedReasonTextView);

        //Listeners
        returnButton.setOnClickListener(this);

        //Get passed timer value
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            finishReason = extras.getInt("FINISH_REASON");
            userName = extras.getString("NAME");
            if (userName == null) {
                userName = "[NOT NULL]";
            }
        }

        if (finishReason == 0) {
            getUserFullName();
        }

        //Set screen text
        setFinishText(finishReason);

        //Set SMS message
//        SMSMessage = "This is a message from the Howler Security App. " + fullName + " has selected you as an emergency contact and does not seem to have made it to their destination in time. You may want to try to get ahold of them immediately.";
    }



    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        alertContact();
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("TrackingFinishedActivity");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (mBackgroundHandler != null) {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //OnClick functions
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.returnFromTimerFinishButton:
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                break;
        }
    }

    public void setFinishText(int finishReason) {
        if (finishReason == 0) {
            finishReasonText.setText("Timer has finished.");
        } else if (finishReason == 1) {
            finishReasonText.setText("You have stopped the timer.");
        } else {
            finishReasonText.setText("contacts have been contacted");
        }
    }

    public static void sendSMS() {
        AWSCredentials mAwsCredentials = new BasicAWSCredentials("########", "#######");
        AmazonSNSClient snsClient = new AmazonSNSClient(mAwsCredentials);
        String message = SMSMessage;
        String phoneNumber = "+17788887924";
        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<String, MessageAttributeValue>();
        sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);
    }

    public static void sendSMSMessage(AmazonSNSClient snsClient, String message, String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        Log.d("message ID", result.toString());
    }

    public void getLocation() {
        // Get LocationManager object
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Get the name of the best provider
//        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION", "getLocation: CHECK PERMISSION FAIL");
            return;
        }
        Location myLocation = locationManager.getLastKnownLocation(provider);
        if (myLocation != null) {
            //latitude of location
            myLatitude = myLocation.getLatitude();
            //longitude of location
            myLongitude = myLocation.getLongitude();
            Log.d("LOCATION", "Lat: " + myLatitude + " Long: " + myLongitude);
        }
        else {
            Log.d("LOCATION", "null");
        }
    }

    public void alertContact() {
        if (finishReason == 2) {
            mBackgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    SMSMessage = "This is a message from the Howler Security App. " + userName + " has selected you as an emergency contact and does not seem to have made it to their destination in time. You may want to try to get ahold of them immediately.";
                    getLocation();
                    SMSMessage = SMSMessage + " Their current location is: " + myLatitude + ", " + myLongitude;
//                    sendSMS();
                    Log.d("trackingFinished", "run: messageSent: " + SMSMessage);
                }
            });
        }
    }

    public void getUserFullName(){
        getCognitoUser().getDetailsInBackground(new GetDetailsHandler() {
            @Override
            public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                String name = cognitoUserDetails.getAttributes().getAttributes().get("custom:full_name");
                Log.d("getUserNameSuccess", name);
                Intent i = new Intent(getApplicationContext(), TrackingFinished.class);
                i.putExtra("FINISH_REASON", 2);
                i.putExtra("NAME", name);
                startActivity(i);
            }

            @Override
            public void onFailure(Exception exception) {
                Log.d("getUserNameFail", exception.toString());
            }
        });
    }


}

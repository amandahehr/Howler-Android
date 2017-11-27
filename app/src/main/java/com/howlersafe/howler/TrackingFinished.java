package com.howlersafe.howler;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.howlersafe.howler.R;
import com.howlersafe.howler.aws.AWSHelper;
import java.util.HashMap;
import java.util.Map;

import static com.howlersafe.howler.aws.AWSHelper.getCognitoUser;

public class TrackingFinished extends AppCompatActivity implements View.OnClickListener {

    //Variable defaults/declarations
    double myLatitude = 0;
    double myLongitude = 0;
    public int finishReason = 0;
    public String userName= "[DEFAULT]";
    public static String SMSMessage;
    //Widget variables
    public TextView finishReasonText;
    //For location providing
    LocationManager locationManager;
    String provider;
    //For background thread
    private HandlerThread mBackgroundThread; // An additional thread for running tasks that shouldn't block the UI.
    private static Handler mBackgroundHandler; // A Handler for running tasks in the background.
    //AWSHelper instance
    AWSHelper mAWSHelper;
    //Notification builder
    public NotificationCompat.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_finished);
        //get AWSHelper
        mAWSHelper = new AWSHelper();
        //Cancel running timer
        TrackingTimer.theTimer.cancel();
        //Cancel running notifications
        TrackingTimer.mNotificationManager.cancelAll();

        //Get location permission
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
        finishReasonText = findViewById(R.id.finishedReasonTextView);

        //Get passed timer value
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            finishReason = extras.getInt("FINISH_REASON");
            Log.d("finishReason", String.valueOf(finishReason));
            userName = extras.getString("NAME");
            if (userName == null) {
                userName = "[NOT NULL]";
            }
        }

        // if finishReason = 7 --> timer ran out
        if (finishReason == 7) {
            getUserFullName();
            Log.d("if finish reason 7", "getting user full name");
        }

        //Set screen text
        setFinishText(finishReason);

    }
    //For background thread
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
    }

    //Set text on screen determined by reason passed
    public void setFinishText(int finishReason) {
        //Timer ran out (7)
        if (finishReason == 7) {
            finishReasonText.setText(R.string.the_timer_has_finished);
        }
        // User stopped timer (0)
        else if (finishReason == 0) {
            finishReasonText.setText(R.string.you_have_stopped_the_timer);
        }
        // User name has been received (2) or reopened (from notification) (3)
        else if (finishReason == 2 || finishReason == 3) {
            Log.d("finish reason", String.valueOf(finishReason));
            String finishText = "The timer has run out, your contact(s): ";
            for (String name : MainActivity.selectedNamesList) {
                finishText += name + ", ";
            }
            finishText += "have been notified. Be sure to let them know if you are safe.";
            finishReasonText.setText(finishText);
        }
    }

    //Setup SMS sending
    public void sendSMS() {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
            AWSCredentials mAWSCredentials = new BasicAWSCredentials(mAWSHelper.KEY_1, mAWSHelper.KEY_2);
                AmazonSNSClient snsClient = new AmazonSNSClient(mAWSCredentials);
                String message = SMSMessage;
                Map<String, MessageAttributeValue> smsAttributes = new HashMap <String, MessageAttributeValue>();
                smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                        .withStringValue("Promotional") //Sets the type to promotional.
                        .withDataType("String"));
                for (String phone : MainActivity.selectedContactsList) {
                    sendSMSMessage(snsClient, message, phone, smsAttributes);
                    Log.d("trackingFinished", "run: messageSent to " + phone + " : " + SMSMessage);
                }
                finishReason = 3;
                sendTimerFinishNotification();
            }
        });
    }
    //Send SMS
    public static void sendSMSMessage(AmazonSNSClient snsClient, String message, String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        Log.d("message ID", result.toString());
    }

    //Get location
    public void getLocation() {
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

    //if timer ran out, alert contact: get location, send sms
    public void alertContact() {
        // Got user name (2)
        if (finishReason == 2) {
            mBackgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    SMSMessage = "This is a message from the Howler Security App. " + userName + " has selected you as an emergency contact and does not seem to have made it from " + MainActivity.startLocationString + " to " + MainActivity.endLocationString + " in time. You may want to try to get ahold of them immediately.";
                    getLocation();
                    SMSMessage = SMSMessage + " Their current location is: " + myLatitude + ", " + myLongitude;
                    sendSMS();
                }
            });
        }
    }

    //Get users name from cognito and restart activity with name passed
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

    //Notification for timer ran out
    public void sendTimerFinishNotification() {
        Intent i = new Intent(this, TrackingFinished.class);
        i.putExtra("FINISH_REASON", 3);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0,
                i, PendingIntent.FLAG_UPDATE_CURRENT);
        //Get an instance of NotificationManager
        mBuilder = new NotificationCompat.Builder(this , "my_channel_02")
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setContentTitle("Timer Finished")
                .setContentText("Your contacts have been notified.")
                .setContentIntent(pIntent);
        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//
        if (TrackingTimer.mNotificationManager != null) {
            TrackingTimer.mNotificationManager.notify(1, mBuilder.build());
        }
        else {
            Log.d("notification", "null notification manager");
        }
    }

    @Override
    public void onBackPressed() {
        Intent j = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(j);
    }
}
package com.howlersafe.howler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.NotificationChannel;

import com.howlersafe.howler.R;

public class TrackingTimer extends AppCompatActivity implements View.OnClickListener {
    //Widget declarations
    public TextView timerHours;
    public TextView timerMins;
    public TextView timerSecs;
    public Button stopTrackingButton;
    public Button addTimeButton;

    //Variable declarations
    //for timer
    public int counter;
    public int timerStart;
    public int hours;
    public int minutes;
    public int seconds;
    public static CountDownTimer theTimer;
    public double h;
    public double m;
    public double s;
    //for notifications
    public NotificationCompat.Builder mBuilder;
    public NotificationCompat.Builder mBuilder2;
    public static NotificationManager mNotificationManager;
    public String notificationTimer;
   // define the SharedPreferences object
    private SharedPreferences savedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_timer);
        //Get passed values
        Bundle extras = getIntent().getExtras();

        // Check if we're running on Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //if so, set notification channels
            createNotificationChannels();
        } else {
            //if not, create notifications without channels
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        //Get widget references
        timerHours = findViewById(R.id.hourEditText);
        timerMins = findViewById(R.id.minTextView);
        timerSecs = findViewById(R.id.secTextView);
        stopTrackingButton = findViewById(R.id.stopTrackingButton);
        addTimeButton = findViewById(R.id.addTimeButton);

        //Listeners
        stopTrackingButton.setOnClickListener(this);
        addTimeButton.setOnClickListener(this);

        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

        //Check the passed action
        if (extras.getString("ACTION") == null) {
            timerStart = extras.getInt("TIMER_VAL");
            //Set notification widget
            sendTimerNotification();
            //start timer
            theTimer = new trackingTimer(timerStart, 1000).start();
        }
        else {
            if (extras.getString("ACTION").equals("ADD")){
                sendTimerNotification();
                counter = savedValues.getInt("counter", 0);
                addTime();
            }
            else {
                sendTimerNotification();
                counter = savedValues.getInt("counter", 0);
                theTimer.cancel();
                theTimer = new trackingTimer(counter, 1000).start();
            }
        }
    }


    //OnClick functions
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addTimeButton:
                addTime();
                break;
            case R.id.stopTrackingButton:
                stopTracking();
                break;
        }
    }

    //Tracking timer class
    public class trackingTimer extends CountDownTimer{

        trackingTimer(long milisInFuture, long countDownInterval){
            super(milisInFuture,countDownInterval);
            counter = (int) milisInFuture;
        }

        @Override
        public void onTick(long millisUntilFinished){
            //set hours text
            h = counter*2.778e-7;
            hours = (int) h;
            timerHours.setText(String.valueOf(hours));
            //set minutes text
            m = counter*1.667e-5 - hours*60;
            minutes = (int) m;
            timerMins.setText(String.valueOf(minutes));
            //set seconds text
            s = counter*0.001 - minutes*60 - hours*3600;
            seconds = (int) s;
            timerSecs.setText(String.valueOf(seconds));

            //Update Notification Text
            //If has hours
            if (hours != 0){
                notificationTimer = String.valueOf(hours) + " hr : " + String.valueOf(minutes) + " min : " + String.valueOf(seconds) + " sec";
            }
            else {
                notificationTimer = String.valueOf(minutes) + " min : " + String.valueOf(seconds) + " sec";
            }
            //update notification
            mBuilder.setContentText(notificationTimer);
            if (mNotificationManager != null) {
                mNotificationManager.notify(0, mBuilder.build());
            }
            else {
                Log.d("notification", "null notificationManager");
            }

            // save the instance variables
            SharedPreferences.Editor editor = savedValues.edit();
            editor.putInt("counter", counter);
            editor.apply();

            //send last minute notification
            if (minutes == 0){
                sendLastMinuteNotification();
            }
            else {
                if (mNotificationManager != null) {
                    mNotificationManager.cancel(1);
                }
            }
            //count down
            counter-=1000;
        }

        //When countdown finishes
        @Override
        public void onFinish(){
            Intent i = new Intent(getApplicationContext(), TrackingFinished.class);
            i.putExtra("FINISH_REASON", 7); //7: timer ran out
            startActivity(i);
        }
    }

    public void addTime(){
        theTimer.cancel();
        theTimer = new trackingTimer(counter + 5*60000, 1000).start();
    }

    public void stopTracking(){
        Intent i = new Intent(getApplicationContext(), TrackingFinished.class);
        i.putExtra("FINISH_REASON", 0); //0: user stopped timer
        startActivity(i);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannels(){
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = "my_channel_01";
        // The user-visible name of the channel.
        CharSequence name = "Timer Widget";
        // The user-visible description of the channel.
        String description = "These notifications display the current timer and update every second.";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        // Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);
        mNotificationManager.createNotificationChannel(mChannel);

        String id2 = "my_channel_02";
        CharSequence name2 = "Howler alerts";
        String description2 = "These are alert notifications for Howler";
        int importance2 = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel2 = new NotificationChannel(id2, name2, importance2);
        // Configure the notification channel.
        mChannel2.setDescription(description2);
        mChannel2.enableLights(true);
        // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
        mChannel2.setLightColor(Color.RED);
        mChannel2.enableVibration(true);
        mChannel2.setVibrationPattern(new long[]{200, 300, 400, 300, 200, 300, 400, 300, 200});
        mNotificationManager.createNotificationChannel(mChannel2);

    }


    public void sendTimerNotification() {
        // Prepare intent which is triggered if the notification is selected
        Intent stopIntent = new Intent(this, TrackingFinished.class);
        stopIntent.putExtra("FINISH_REASON", 0);
        PendingIntent pStopIntent = PendingIntent.getActivity(this, 1, stopIntent, 0);

        Intent addIntent = new Intent(this, TrackingTimer.class);
        addIntent.putExtra("ACTION", "ADD");
        PendingIntent pAddIntent = PendingIntent.getActivity(this, 2, addIntent, 0);

        Intent thisIntent = new Intent(this, TrackingTimer.class);
        thisIntent.putExtra("ACTION", "NONE");
        PendingIntent pThisIntent = PendingIntent.getActivity(this, 3, thisIntent, 0);


        //Get an instance of NotificationManager
        mBuilder = new NotificationCompat.Builder(this , "my_channel_01")
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setContentTitle("Time Left")
                .setContentText("timer")
                .setOnlyAlertOnce(true)
                .setContentIntent(pThisIntent)
                .addAction(R.drawable.ic_add, "Add Time", pAddIntent)
                .addAction(R.drawable.ic_stat_name, "Stop", pStopIntent);
        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//
        if (mNotificationManager != null) {
            mNotificationManager.notify(0, mBuilder.build());
        }
        else {
            Log.d("notification", "null notification manager");
        }
    }

    public void sendLastMinuteNotification() {

        Intent thisIntent = new Intent(this, TrackingTimer.class);
        thisIntent.putExtra("ACTION", "NONE");
        PendingIntent pThisIntent = PendingIntent.getActivity(this, 3, thisIntent, 0);

        //Get an instance of NotificationManager
        mBuilder2 = new NotificationCompat.Builder(this , "my_channel_02")
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setContentTitle("Warning!")
                .setContentText("You have less than 1 minute left")
                .setContentIntent(pThisIntent);
        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//
        if (mNotificationManager != null) {
            mNotificationManager.notify(1, mBuilder2.build());
        }
        else {
            Log.d("notification", "null notification manager");
        }
    }
    @Override
    public void onBackPressed() {
    }
}

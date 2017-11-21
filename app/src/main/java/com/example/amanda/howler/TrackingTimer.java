package com.example.amanda.howler;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class TrackingTimer extends AppCompatActivity implements View.OnClickListener {
    public int counter;
    public TextView timerHours;
    public TextView timerMins;
    public TextView timerSecs;
    public int timerStart;
    public int hours;
    public int minutes;
    public int seconds;
    public Button stopTrackingButton;
    public Button addTimeButton;
    public CountDownTimer theTimer;
    public double h;
    public double m;
    public double s;
    public String userFullName;

   public NotificationCompat.Builder mBuilder;
   public NotificationManager mNotificationManager;
   public String notificationTimer;

   // define the SharedPreferences object
    private SharedPreferences savedValues;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_timer);

        Bundle extras = getIntent().getExtras();
        userFullName = extras.getString("USER_NAME");

        //Get widget references
        timerHours = (TextView) findViewById(R.id.hourTextView);
        timerMins = (TextView) findViewById(R.id.minTextView);
        timerSecs = (TextView) findViewById(R.id.secTextView);
        stopTrackingButton = (Button) findViewById(R.id.stopTrackingButton);
        addTimeButton = (Button) findViewById(R.id.addTimeButton);

        //Listeners
        stopTrackingButton.setOnClickListener(this);
        addTimeButton.setOnClickListener(this);

        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

        if (extras.getString("ACTION") == null) {
            timerStart = extras.getInt("TIMER_VAL");
            Log.d("timerStart", String.valueOf(timerStart));
            //Set text/variables
//            counter = timerStart;
            //Set notification widget
            sendNotification();
            //start timer
            theTimer = new trackingTimer(timerStart, 1000).start();
        }
        else {
            Log.d("ACTION ON CLICK", extras.getString("ACTION"));
            int restartCounter = savedValues.getInt("counter", 0);
            Log.d("Counter", String.valueOf(restartCounter));
            //Set notification widget
            sendNotification();
            //Start timer
            theTimer = new trackingTimer(restartCounter, 1000).start();
            if (extras.getString("ACTION").equals("ADD")){
                Log.d("Function", "Adding time");
                addTime();
            }
            else {
                Log.d("Function", "Stopping time");
                stopTracking();
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


    public class trackingTimer extends CountDownTimer{

//        private long counter;

        public trackingTimer(long milisInFuture, long countDownInterval){
            super(milisInFuture,countDownInterval);
            counter = (int) milisInFuture;
        }

//        public void setCounter(int newCounter) {
//            this.counter = newCounter;
//        }

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
                notificationTimer = String.valueOf(hours) + ":" + String.valueOf(minutes) + ":" + String.valueOf(seconds);
            }
            else {
                notificationTimer = String.valueOf(minutes) + ":" + String.valueOf(seconds);
            }
            mBuilder.setContentText(notificationTimer);
            if (mNotificationManager != null) {
                mNotificationManager.notify(0, mBuilder.build());
            }
            else {
                Log.d("notification", "null notificationManager");
            }
            Log.d("onTick", "minute: " + String.valueOf(minutes));
            // save the instance variables
            SharedPreferences.Editor editor = savedValues.edit();
            editor.putInt("counter", counter);
            editor.commit();

            //count down
            counter-=1000;
        }

        @Override
        public void onFinish(){
            Intent i = new Intent(getApplicationContext(), TrackingFinished.class);
            i.putExtra("FINISH_REASON", 0); //0: timer ran out
            i.putExtra("USER_NAME", userFullName);
            startActivity(i);
        }


    }

    public void addTime(){
//        theTimer.setCounter(counter + 5*60000);
        theTimer.cancel();
        theTimer = new trackingTimer(counter + 5*60000, 1000).start();
    }

    public void stopTracking(){
        theTimer.cancel();
        Intent i = new Intent(getApplicationContext(), TrackingFinished.class);
        i.putExtra("FINISH_REASON", 1); //1: user stopped timer
        i.putExtra("USER_NAME", userFullName);
        startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendNotification() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = "my_channel_01";
        // The user-visible name of the channel.
        CharSequence name = "channel name";
        // The user-visible description of the channel.
        String description = "channel description";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        // Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);
//        mChannel.enableVibration(false);
//        mChannel.setVibrationPattern(new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0});
        mNotificationManager.createNotificationChannel(mChannel);

        // Prepare intent which is triggered if the notification is selected
        Intent stopIntent = new Intent(this, TrackingTimer.class);
        stopIntent.putExtra("ACTION", "STOP");
        PendingIntent pStopIntent = PendingIntent.getActivity(this, 1, stopIntent, 0);

        Intent addIntent = new Intent(this, TrackingTimer.class);
        addIntent.putExtra("ACTION", "ADD");
        PendingIntent pAddIntent = PendingIntent.getActivity(this, 2, addIntent, 0);


        //Get an instance of NotificationManager
        mBuilder = new NotificationCompat.Builder(this , id)
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setContentTitle("Tracking Timer")
                .setContentText("timer")
                .addAction(R.drawable.ic_add, "Add Time", pAddIntent)
                .addAction(R.drawable.ic_stat_name, "Stop Timer", pStopIntent);
        // Gets an instance of the NotificationManager service
//        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//
        if (mNotificationManager != null) {
            mNotificationManager.notify(0, mBuilder.build());
        }
        else {
            Log.d("notification", "null notification manager");
        }
    }


}

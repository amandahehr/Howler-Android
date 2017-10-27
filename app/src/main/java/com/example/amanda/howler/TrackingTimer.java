package com.example.amanda.howler;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class TrackingTimer extends AppCompatActivity implements View.OnClickListener {
    public int counter;
    public TextView countdownVal;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_timer);

        //Get widget references
        countdownVal = (TextView) findViewById(R.id.countdownTextView);
        timerHours = (TextView) findViewById(R.id.hourTextView);
        timerMins = (TextView) findViewById(R.id.minTextView);
        timerSecs = (TextView) findViewById(R.id.secTextView);
        stopTrackingButton = (Button) findViewById(R.id.stopTrackingButton);
        addTimeButton = (Button) findViewById(R.id.addTimeButton);

        //Listeners
        stopTrackingButton.setOnClickListener(this);
        addTimeButton.setOnClickListener(this);

        //Get passed timer value
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            timerStart = extras.getInt("TIMER_VAL");
        }

        //Set text/variables
        counter = timerStart;
        countdownVal.setText(Integer.toString(timerStart));

        //start timer
        theTimer = new trackingTimer(timerStart, 1000).start();

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
        public trackingTimer(long milisInFuture, long countDownInterval){
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

            //count down
            counter-=1000;
        }
        @Override
        public void onFinish(){
            Intent i = new Intent(getApplicationContext(), TrackingFinished.class);
            i.putExtra("FINISH_REASON", 0); //0: timer ran out
            startActivity(i);
        }
    }

    public void addTime(){
        theTimer.cancel();
        theTimer = new trackingTimer(counter + 5*60000, 1000).start();
    }

    public void stopTracking(){
        theTimer.cancel();
        Intent i = new Intent(getApplicationContext(), TrackingFinished.class);
        i.putExtra("FINISH_REASON", 1); //1: user stopped timer
        startActivity(i);
    }
}

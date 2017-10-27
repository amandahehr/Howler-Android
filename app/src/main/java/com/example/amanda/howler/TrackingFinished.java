package com.example.amanda.howler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TrackingFinished extends AppCompatActivity implements View.OnClickListener {

    public Button returnButton;
    public int finishReason;
    public TextView finishReasonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_finished);

        //Get widget references
        returnButton = (Button) findViewById(R.id.returnFromTimerFinishButton);
        finishReasonText = (TextView) findViewById(R.id.finishedReasonTextView);

        //Listeners
        returnButton.setOnClickListener(this);

        //Get passed timer value
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            finishReason = extras.getInt("FINISH_REASON");
        }

        setFinishText(finishReason);
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

    public void setFinishText(int finishReason){
        if (finishReason == 0){
            finishReasonText.setText("Timer has finished and contacts have been contacted.");
        }
        else {
            finishReasonText.setText("You have stopped the timer.");
        }
    }
}

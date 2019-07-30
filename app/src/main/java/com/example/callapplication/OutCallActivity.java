package com.example.callapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.genband.mobile.api.services.call.CallInterface;
import com.genband.mobile.api.services.call.IncomingCallInterface;
import com.genband.mobile.api.utilities.exception.MobileException;
import com.genband.mobile.impl.utilities.constants.AdditionalInfoConstants;

import java.util.Timer;
import java.util.TimerTask;



/**
 * Created by user on 18.07.2019.
 */

public class OutCallActivity extends AppCompatActivity {

    IncomingCallInterface incomingCall;
    ImageView img_person;
    Handler handler = new Handler();
    Timer timer;
    public static Chronometer callTime;
    TextView calleName;
    public long lastPause;
    private TextView call_state;
    Button end_call;
    CallInterface call;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        call_state = (TextView) findViewById(R.id.call_state);
        callTime = (Chronometer) findViewById(R.id.call_time);
        end_call = (Button) findViewById(R.id.end_call);
        calleName = (TextView) findViewById(R.id.calle_name);

        call_state.setText(AudioCallActivity.getMyCallState());
        //calleeName.setText(AudioCallActivity.getCaller());
        calleName.setText(getIntent().getExtras().getString("calleename"));

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(call.getCallState().getType().toString()=="IN_CALL"){
                        callTime.start();
                        }
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,0,1000);


        end_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    call_state.setText(call.getCallState().toString());
                    call_state.setText("ENDED");
                    stopCall();
                    lastPause=SystemClock.elapsedRealtime();
                    callTime.stop();
                    callTime.setBase(SystemClock.elapsedRealtime());
                    lastPause=0;
                }
                catch (MobileException exception) {
                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent back = new Intent(OutCallActivity.this,AudioCallActivity.class);
                        startActivity(back);
                    }
                }, 1100);


            }
        });

    }

    public void stopCall() throws MobileException {
        if(call != null) {
            call.endCall();
        }
    }



}

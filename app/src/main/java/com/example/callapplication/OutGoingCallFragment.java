package com.example.callapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.genband.mobile.api.services.call.CallInterface;
import com.genband.mobile.api.utilities.exception.MobileException;
import com.genband.mobile.impl.services.call.CallState;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class OutGoingCallFragment extends Fragment implements CallStateListener {



    public CallStateListener getCallStateListener() {
        return callStateListener;
    }

    public void setCallStateListener(CallStateListener callStateListener) {
        this.callStateListener = callStateListener;
    }

    private CallStateListener callStateListener;
    TextView calleName;
    TextView call_state;
    static Button end_call;
    Chronometer callTime;
    CallInterface  call;
    InComingCallFragment ınComingCallFragment;

    public OutGoingCallFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_out_going_call, container, false);
        calleName = (TextView) view.findViewById(R.id.caller_name);
        call_state = (TextView) view.findViewById(R.id.call_state);
        end_call = view.findViewById(R.id.end_call);
        callTime = (Chronometer) view.findViewById(R.id.call_time);

        calleName.setText(getArguments().getString("name"));
        call_state.setText(AudioCallActivity.getMyCallState());

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        end_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    call_state.setText("Ended");
                    callTime.stop();
                    stopCall();

                } catch (MobileException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void callStateChange(CallInterface callInterface,CallState callState) {
        this.call = callInterface;
        Log.i("CALLSTATE", callState.getType().toString());
        if(callState.getType().equals(CallState.Type.IN_CALL)) {
            callTime.setBase(SystemClock.elapsedRealtime());
            Handler handler = new Handler();
            Timer timer;
            callTime.setVisibility(View.VISIBLE);
            call_state.setText("In Call");
            TimerTask timerTask = new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callTime.start();
                        }
                    });
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 0, 1000);
        }

        else if (callState.getType().equals(CallState.Type.DIALING)) {
            call_state.setText("Dialing");
            }
        else if(callState.getType().equals(CallState.Type.RINGING)){
            call_state.setText("Ringing"); }
        else if(callState.getType().equals(CallState.Type.ENDED)){
            try { Thread.sleep(2000); }catch (Exception e){ e.printStackTrace(); }
            callTime.stop();
            call_state.setText("Ended");

        }
    }

    public void stopCall() throws MobileException {
        if(call != null) {
            call.endCall();
        }
    }

}

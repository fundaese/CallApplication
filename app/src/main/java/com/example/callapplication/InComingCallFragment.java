package com.example.callapplication;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
public class InComingCallFragment extends Fragment implements CallStateListener {

    public CallStateListener getCallStateListener() {
        return callStateListener;
    }

    public void setCallStateListener(CallStateListener callStateListener) {
        this.callStateListener = callStateListener;
    }

    private CallStateListener callStateListener;
    TextView callerName;
    TextView callee_state;
    static Button end_calll;
    Chronometer calleeTime;
    CallInterface call;
    OutGoingCallFragment outGoingCallFragment;

    Handler handler = new Handler();
    Timer timer;


    public InComingCallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_in_coming_call, container, false);
        callerName = (TextView) view.findViewById(R.id.caller_name);
        callee_state = (TextView) view.findViewById(R.id.callee_state);
        end_calll = view.findViewById(R.id.end_calll);
        calleeTime = (Chronometer) view.findViewById(R.id.callee_time);

        callerName.setText(getArguments().getString("callername"));
        callee_state.setText(getArguments().getString("callstateee"));

        Log.i("calleestate",AudioCallActivity.getMyCallState());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        end_calll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    callee_state.setText("Ended");
                    calleeTime.stop();
                    stopCall();
                    try {
                        Thread.sleep(2000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } catch (MobileException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void callStateChange(CallInterface callInterface,CallState callState) {
        this.call = callInterface;
        if (callState.getType().equals(CallState.Type.DIALING)) {
            callee_state.setText("Dialing");
        }
        else if(callState.getType().equals(CallState.Type.RINGING)){
            callee_state.setText("Ringing");
        }
        else if(callState.getType().equals(CallState.Type.ANSWERING)){
            callee_state.setText("Answering");
        }
        else if(callState.getType().equals(CallState.Type.INITIAL)){
            callee_state.setText("Initial");
        }

        else if(callState.getType().equals(CallState.Type.IN_CALL)){
            callee_state.setText("In Call");
            calleeTime.setVisibility(View.VISIBLE);
            TimerTask timerTask = new TimerTask() {
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            calleeTime.start();
                        }
                    });
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 0, 1000);
        }

        else if(callState.getType().equals(CallState.Type.ENDED)){
            try { Thread.sleep(2000); }catch (Exception e){ e.printStackTrace(); }
            calleeTime.stop();
            callee_state.setText("Ended");
        }
    }

    public void stopCall() throws MobileException {
        if(call != null) {
            call.endCall();
        }
    }

}

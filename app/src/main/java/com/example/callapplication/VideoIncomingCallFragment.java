package com.example.callapplication;


import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.genband.mobile.ServiceProvider;
import com.genband.mobile.api.services.call.CallInterface;
import com.genband.mobile.api.services.call.IncomingCallInterface;
import com.genband.mobile.api.utilities.Configuration;
import com.genband.mobile.api.utilities.Constants;
import com.genband.mobile.api.utilities.SDKEventManager;
import com.genband.mobile.api.utilities.exception.MobileException;
import com.genband.mobile.core.webrtc.view.VideoView;
import com.genband.mobile.impl.services.call.CallState;

import java.util.Timer;
import java.util.TimerTask;

import static org.webrtc.ContextUtils.getApplicationContext;

public class VideoIncomingCallFragment extends Fragment implements CallStateListener {

    CallInterface callInterface;
    TextView callerName;
    TextView callee_state;
    Button stopVideoButton;
    Button startVideoButton;
    VideoView localVideoView;
    VideoView remoteVideoView;
    Button stopVideo_Button;
    Button volume;
    Button video;
    Button hold;
    Chronometer callTime;

    ProgressDialog progressDialog;
    View view;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_video_incoming_call, container, false);

        localVideoView = (VideoView)view.findViewById(R.id.locallVideoView);
        remoteVideoView = (VideoView)view.findViewById(R.id.remoteeVideoView);
        stopVideoButton = view.findViewById(R.id.stopVideoButton);
        startVideoButton = view.findViewById(R.id.startVideoButton);
        callerName = view.findViewById(R.id.caller_name);
        callee_state = view.findViewById(R.id.callee_state);
        stopVideo_Button = view.findViewById(R.id.stopVideo_Button);
        volume = view.findViewById(R.id.volume);
        video = view.findViewById(R.id.video);
        hold = view.findViewById(R.id.stop);
        callTime = (Chronometer) view.findViewById(R.id.timer);

        Configuration.getInstance().setDefaultCameraMode(Camera.CameraInfo.CAMERA_FACING_FRONT);
        CallInterface callInterface = (CallInterface) ServiceProvider.getInstance(getContext()).getCallService().getActiveCalls().firstObject();

        if (callInterface != null) {
            callInterface.setLocalVideoView(localVideoView);
            callInterface.setRemoteVideoView(remoteVideoView);
        }

        callerName.setText(getArguments().getString("callername"));
        callee_state.setText(getArguments().getString("callstateee"));


        return view;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {

        IncomingCallInterface ıncomingCallInterface = (IncomingCallInterface) ServiceProvider.getInstance(getContext()).getCallService().getActiveCalls().firstObject();
        this.callInterface = ıncomingCallInterface;

        stopVideo_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    stopCall();
                } catch (MobileException e) {
                    e.printStackTrace();
                }
            }
        });

        stopVideo_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    stopCall();
                } catch (MobileException e) {
                    e.printStackTrace();
                }
            }
        });

        startVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ıncomingCallInterface.acceptCall(true);
            }
        });

        volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait..");
                progressDialog.show();
                final int totalTime = 20;
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        int jumpTime = 0;

                        while(jumpTime < totalTime) {
                            try {
                                sleep(200);
                                jumpTime += 1;
                                progressDialog.setProgress(jumpTime);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();
                if(callInterface.isMute()){
                    Toast toast=Toast.makeText(getApplicationContext(),"Call is unmute now!",Toast.LENGTH_SHORT);
                    toast.setMargin(50,50);
                    toast.show();
                    callInterface.unMute();
                    volume.setBackgroundResource(R.drawable.btn_voice);
                }else{
                    Toast toast=Toast.makeText(getApplicationContext(),"Call is mute now!",Toast.LENGTH_SHORT);
                    toast.setMargin(50,50);
                    toast.show();
                    callInterface.mute();
                    volume.setBackgroundResource(R.drawable.btn_volumemute);
                }
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait..");
                progressDialog.show();
                final int totalTime = 20;
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        int jumpTime = 0;

                        while(jumpTime < totalTime) {
                            try {
                                sleep(200);
                                jumpTime += 1;
                                progressDialog.setProgress(jumpTime);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();

                if(callInterface.getMediaAttributes().getLocalVideo()){
                    callInterface.videoStop();
                    Toast toast=Toast.makeText(getApplicationContext(),"Video closed!",Toast.LENGTH_SHORT);
                    toast.setMargin(50,50);
                    toast.show();
                    video.setBackgroundResource(R.drawable.btn_videooff);

                }else{
                    callInterface.videoStart();
                    Toast toast=Toast.makeText(getApplicationContext(),"Video opened!",Toast.LENGTH_SHORT);
                    toast.setMargin(50,50);
                    toast.show();
                    video.setBackgroundResource(R.drawable.btn_video_call);
                }
            }
        });

        hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait..");
                progressDialog.show();
                final int totalTime = 20;
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        int jumpTime = 0;

                        while(jumpTime < totalTime) {
                            try {
                                sleep(200);
                                jumpTime += 1;
                                progressDialog.setProgress(jumpTime);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();
                if(callInterface.getCallState().getType().equals(CallState.Type.ON_DOUBLE_HOLD)||
                        callInterface.getCallState().getType().equals(CallState.Type.ON_HOLD)) {
                    callInterface.unHoldCall();
                    Toast toast = Toast.makeText(getApplicationContext(), "Call unhold!", Toast.LENGTH_SHORT);
                    toast.setMargin(50, 50);
                    toast.show();
                    hold.setBackgroundResource(R.drawable.btn_hold);
                }else{
                    callInterface.holdCall();
                    Toast toast = Toast.makeText(getApplicationContext(), "Call on hold!", Toast.LENGTH_SHORT);
                    toast.setMargin(50, 50);
                    toast.show();
                    hold.setBackgroundResource(R.drawable.btn_unhold);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        SDKEventManager.handleEvent(Constants.SDKEvents.EVENT_BACKGROUND);
    }

    @Override
    public void onResume() {
        super.onResume();
        SDKEventManager.handleEvent(Constants.SDKEvents.EVENT_FOREGROUND);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    public void stopCall() throws MobileException {
        if(callInterface != null) {
            callInterface.endCall();
        }
    }

    @Override
    public void callStateChange(CallInterface callInterface, CallState callState) {
        this.callInterface = callInterface;
        Log.i("CALLSTATE", callState.getType().toString());

        if(callState.getType().equals(CallState.Type.IN_CALL)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_CANCEL:

                            break;
                        case MotionEvent.ACTION_UP:
                            v.performClick();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    video.setVisibility(View.GONE);
                                    volume.setVisibility(View.GONE);
                                    hold.setVisibility(View.GONE);
                                    callTime.setVisibility(View.GONE);
                                    callee_state.setVisibility(View.GONE);
                                    callerName.setVisibility(View.GONE);
                                }
                            }, 5000);
                            break;
                        case MotionEvent.ACTION_DOWN:
                            hold.setVisibility(View.VISIBLE);
                            video.setVisibility(View.VISIBLE);
                            volume.setVisibility(View.VISIBLE);
                            callTime.setVisibility(View.VISIBLE);
                            callee_state.setVisibility(View.VISIBLE);
                            callerName.setVisibility(View.VISIBLE);
                        default:
                            break;
                    }
                    return true;
                }
            });
            stopVideoButton.setVisibility(View.GONE);
            stopVideo_Button.setVisibility(View.VISIBLE);
            startVideoButton.setVisibility(View.GONE);
            remoteVideoView.setVisibility(View.VISIBLE);
            localVideoView.setVisibility(View.VISIBLE);
            callee_state.setText("In Call");
            callee_state.setVisibility(View.GONE);
            callerName.setVisibility(View.GONE);

            AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(true);

            callTime.setBase(SystemClock.elapsedRealtime());
            Handler handler = new Handler();
            Timer timer;

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

            if (callInterface != null) {
                callInterface.setLocalVideoView(localVideoView);
                callInterface.setRemoteVideoView(remoteVideoView);
            }
        }

        else if (callState.getType().equals(CallState.Type.DIALING)) {
            callee_state.setText("Dialing");
        }

        else if(callState.getType().equals(CallState.Type.RINGING)) {
            callee_state.setText("Ringing");
            remoteVideoView.setVisibility(View.GONE);
        }

        else if(callState.getType().equals(CallState.Type.ENDED)){
            callee_state.setText("Ended");
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        else if(callState.getType().equals(CallState.Type.ON_HOLD)){
            callee_state.setText("On Hold");
        }

        else if(callState.getType().equals(CallState.Type.ON_DOUBLE_HOLD)){
            callee_state.setText("On Double Hold");
        }

        else if(callState.getType().equals(CallState.Type.REMOTELY_HELD)) {
            Log.i("Call", "Remote party holds the call");
            callee_state.setText("Remotely Held");
            if(callInterface.isMute()){
                Toast toast=Toast.makeText(getApplicationContext(),"Call is unmute now!",Toast.LENGTH_SHORT);
                toast.setMargin(50,50);
                toast.show();
                callInterface.unMute();
                volume.setBackgroundResource(R.drawable.btn_voice);
            }else{
                Toast toast=Toast.makeText(getApplicationContext(),"Call is mute now!",Toast.LENGTH_SHORT);
                toast.setMargin(50,50);
                toast.show();
                callInterface.mute();
                volume.setBackgroundResource(R.drawable.btn_volumemute);
            }
        }
    }

    @Override
    public void videoStateChange(CallInterface call) {
        this.callInterface = call;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void voiceStateChange(CallInterface call) {
        this.callInterface = call;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void holdStateChange(CallInterface call) {
        this.callInterface = call;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void failStates(CallInterface call) {
        this.callInterface = call;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast toast = Toast.makeText(getApplicationContext(), "TRY AGAIN!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void createVideoCall(CallInterface call) {

    }

    @Override
    public void transferCallListener(CallInterface call) {

    }

    @Override
    public void mediaAttributesChanged(CallInterface call) {

    }
}

package com.example.callapplication;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.genband.mobile.ServiceProvider;
import com.genband.mobile.api.services.call.CallApplicationListener;
import com.genband.mobile.api.services.call.CallInterface;
import com.genband.mobile.api.services.call.CallServiceInterface;
import com.genband.mobile.api.services.call.IncomingCallInterface;
import com.genband.mobile.api.services.call.OutgoingCallCreateInterface;
import com.genband.mobile.api.services.call.OutgoingCallInterface;
import com.genband.mobile.api.utilities.Configuration;
import com.genband.mobile.api.utilities.MobileError;
import com.genband.mobile.api.utilities.exception.MobileException;
import com.genband.mobile.core.webrtc.view.VideoView;
import com.genband.mobile.impl.services.call.Call;
import com.genband.mobile.impl.services.call.CallState;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.POWER_SERVICE;
import static org.webrtc.ContextUtils.getApplicationContext;

public class InComingCallFragment extends Fragment implements CallStateListener {

    TextView callerName;
    TextView callee_state;
    static Button end_calll;
    static Button accept_call;
    Chronometer calleeTime;
    CallInterface call;
    Button volume;
    Button hold;
    Button transfer;
    Button speaker;
    Button video;
    Button conferans;
    VideoView localVideoView;
    VideoView remoteVideoView;

    Handler handler = new Handler();
    Timer timer;
    ProgressDialog progressDialog;
    View view;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;


    Dialog dialog;
    Button dissmiss;
    private ArrayList<String> names = new ArrayList<>();
    public ArrayAdapter adapter;
    ListView listView;
    SearchView searchView;

    public InComingCallFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_in_coming_call, container, false);
        callerName =  view.findViewById(R.id.caller_name);
        callee_state =  view.findViewById(R.id.callee_state);
        end_calll = view.findViewById(R.id.end_call);
        calleeTime =  view.findViewById(R.id.callee_time);
        accept_call = view.findViewById(R.id.accept_call);
        volume = view.findViewById(R.id.volume);
        hold = view.findViewById(R.id.stop);
        transfer = view.findViewById(R.id.transfer);
        speaker = view.findViewById(R.id.speaker);
        video= view.findViewById(R.id.video);
        conferans = view.findViewById(R.id.conferans);
        localVideoView = view.findViewById(R.id.localVideoView);
        remoteVideoView = view.findViewById(R.id.remoteVideoView);

        callerName.setText(getArguments().getString("callername"));
        callee_state.setText(getArguments().getString("callstateee"));

        try {
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }

        powerManager = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, String.valueOf(getContext()));

        Log.i("calleestate",AudioCallActivity.getMyCallState());
        return view;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {

        IncomingCallInterface ıncomingCallInterface = (IncomingCallInterface) ServiceProvider.getInstance(getContext()).getCallService().getActiveCalls().firstObject();
        this.call = ıncomingCallInterface;


        accept_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ıncomingCallInterface.acceptCall(false);
            }
        });

        end_calll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    callee_state.setText("Ended");
                    calleeTime.stop();
                    stopCall();
                } catch (MobileException e) {
                    e.printStackTrace();
                }
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
                if(call.isMute()){
                    Toast toast=Toast.makeText(getApplicationContext(),"Call is unmute now!",Toast.LENGTH_SHORT);
                    toast.setMargin(50,50);
                    toast.show();
                    call.unMute();
                    volume.setBackgroundResource(R.drawable.btn_voice);
                }else{
                    Toast toast=Toast.makeText(getApplicationContext(),"Call is mute now!",Toast.LENGTH_SHORT);
                    toast.setMargin(50,50);
                    toast.show();
                    call.mute();
                    volume.setBackgroundResource(R.drawable.btn_volumemute);
                }
            }
        });

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                if(audioManager.isSpeakerphoneOn()){
                    audioManager.setSpeakerphoneOn(false);
                    speaker.setBackgroundResource(R.drawable.btn_spaker_on);
                }
                else {
                    audioManager.setSpeakerphoneOn(true);
                    speaker.setBackgroundResource(R.drawable.btn_spaker_off);
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
                if(call.getCallState().getType().equals(CallState.Type.ON_DOUBLE_HOLD)||
                        call.getCallState().getType().equals(CallState.Type.ON_HOLD)) {
                    call.unHoldCall();
                    Toast toast = Toast.makeText(getApplicationContext(), "Call unhold!", Toast.LENGTH_SHORT);
                    toast.setMargin(50, 50);
                    toast.show();
                    hold.setBackgroundResource(R.drawable.btn_hold);
                }else{
                    call.holdCall();
                    Toast toast = Toast.makeText(getApplicationContext(), "Call on hold!", Toast.LENGTH_SHORT);
                    toast.setMargin(50, 50);
                    toast.show();
                    hold.setBackgroundResource(R.drawable.btn_unhold);
                }
            }
        });

        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mview) {
                MyDialog(view);
                AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                audioManager.setSpeakerphoneOn(false);
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(call.getMediaAttributes().getLocalVideo()){
                    call.videoStop();
                    video.setBackgroundResource(R.drawable.btn_video_call);
                    localVideoView.setVisibility(View.GONE);
                    remoteVideoView.setVisibility(View.GONE);
                    AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setSpeakerphoneOn(false);
                }
                else{
                    call.setRemoteVideoView(remoteVideoView);
                    call.setLocalVideoView(localVideoView);
                    call.videoStart();
                    video.setBackgroundResource(R.drawable.btn_videooff);
                    Configuration.getInstance().setDefaultCameraMode(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setSpeakerphoneOn(true);
                }
            }
        });

        conferans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Only Premium Members",Toast.LENGTH_SHORT);
            }
        });
    }

    public void MyDialog(View view){
        dialog = new Dialog(getContext());
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.transfercall);

        listView = dialog.findViewById(R.id.listview);
        searchView = dialog.findViewById(R.id.search);
        dissmiss = dialog.findViewById(R.id.dismiss);

        names.add("adem1@spidr.com");
        names.add("adem2@spidr.com");
        names.add("adem3@spidr.com");
        names.add("adem4@spidr.com");
        names.add("adem5@spidr.com");
        names.add("adem6@spidr.com");
        names.add("adem7@spidr.com");
        names.add("adem8@spidr.com");
        names.add("emin1@spidr.com");
        names.add("3221030@cucm1.spidrmulti.netas.lab.nortel.com");
        names.add("3221031@cucm1.spidrmulti.netas.lab.nortel.com");
        names.add("3221032@cucm1.spidrmulti.netas.lab.nortel.com");
        names.add("3221045@cucm1.spidrmulti.netas.lab.nortel.com");
        names.add("android1@sdkpush.4d7m.att.com");
        names.add("android2@sdkpush.4d7m.att.com");

        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,names);
        listView.setAdapter(adapter);

        searchView.setQueryHint("Search..");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
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

                String transferAddress = names.get(i);
                call.transferCall(transferAddress);
            }
        });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
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
            calleeTime.setBase(SystemClock.elapsedRealtime());
            accept_call.setVisibility(View.GONE);

            if(!wakeLock.isHeld()) {
                wakeLock.acquire();
            }else{
                wakeLock.release();
            }


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
                                    volume.setVisibility(View.GONE);
                                    hold.setVisibility(View.GONE);
                                    transfer.setVisibility(View.GONE);
                                    speaker.setVisibility(View.GONE);
                                    video.setVisibility(View.GONE);
                                    conferans.setVisibility(View.GONE);
                                }
                            }, 5000);
                            break;
                        case MotionEvent.ACTION_DOWN:
                            hold.setVisibility(View.VISIBLE);
                            volume.setVisibility(View.VISIBLE);
                            transfer.setVisibility(View.VISIBLE);
                            speaker.setVisibility(View.VISIBLE);
                            video.setVisibility(View.VISIBLE);
                            conferans.setVisibility(View.VISIBLE);
                        default:
                            break;
                    }
                    return true;
                }
            });
        }

        else if(callState.getType().equals(CallState.Type.ENDED)){
            calleeTime.stop();
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
            if(call.isMute()){
                Toast toast=Toast.makeText(getApplicationContext(),"Call is unmute now!",Toast.LENGTH_SHORT);
                toast.setMargin(50,50);
                toast.show();
                call.unMute();
                volume.setBackgroundResource(R.drawable.btn_volumemute);
            }else{
                Toast toast=Toast.makeText(getApplicationContext(),"Call is mute now!",Toast.LENGTH_SHORT);
                toast.setMargin(50,50);
                toast.show();
                call.mute();
                volume.setBackgroundResource(R.drawable.btn_voice);
            }
        }
    }

    @Override
    public void videoStateChange(CallInterface call) {
        this.call = call;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void voiceStateChange(CallInterface call) {
        this.call = call;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void holdStateChange(CallInterface call) {
        this.call = call;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void failStates(CallInterface call) {
        this.call = call;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast toast = Toast.makeText(getApplicationContext(), "TRY AGAIN!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void createVideoCall(CallInterface call) {
        this.call = call;
    }

    @Override
    public void transferCallListener(CallInterface call) {
        this.call = call;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        dialog.dismiss();
        Toast toast = Toast.makeText(getApplicationContext(), "Transfer call is success!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void mediaAttributesChanged(CallInterface call) {
        this.call = call;
        localVideoView.setVisibility(call.getMediaAttributes().getLocalVideo() ? View.VISIBLE : View.GONE);
        remoteVideoView.setVisibility(call.getMediaAttributes().getRemoteVideo() ? View.VISIBLE : View.GONE);
    }

    public void stopCall() throws MobileException {
        if(call != null) {
            call.endCall();
        }
    }
}

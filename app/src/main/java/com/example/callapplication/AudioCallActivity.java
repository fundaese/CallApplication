package com.example.callapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.genband.mobile.NotificationStates;
import com.genband.mobile.OnCompletionListener;
import com.genband.mobile.RegistrationApplicationListener;
import com.genband.mobile.RegistrationService;
import com.genband.mobile.RegistrationStates;
import com.genband.mobile.ServiceProvider;
import com.genband.mobile.api.services.call.CallApplicationListener;
import com.genband.mobile.api.services.call.CallInterface;
import com.genband.mobile.api.services.call.CallServiceInterface;
import com.genband.mobile.api.services.call.IncomingCallInterface;
import com.genband.mobile.api.services.call.OutgoingCallCreateInterface;
import com.genband.mobile.api.services.call.OutgoingCallInterface;
import com.genband.mobile.api.utilities.Configuration;
import com.genband.mobile.api.utilities.MobileError;
import com.genband.mobile.api.utilities.OrientationMode;
import com.genband.mobile.api.utilities.ScreenOrientation;
import com.genband.mobile.api.utilities.exception.MobileException;
import com.genband.mobile.core.webrtc.view.VideoView;
import com.genband.mobile.impl.services.call.CallService;
import com.genband.mobile.impl.services.call.CallState;
import com.genband.mobile.impl.services.call.MediaAttributes;
import com.genband.mobile.impl.utilities.constants.AdditionalInfoConstants;

import java.util.ArrayList;
import java.util.Map;

import static org.webrtc.ContextUtils.getApplicationContext;

public class AudioCallActivity extends AppCompatActivity implements CallApplicationListener{

    ListView callList;
    CallInterface call;
    public ArrayAdapter adapter;
    public static String getMyCallState() {return myCallState; }
    private static String myCallState;
    CallServiceInterface callService;

    final ArrayList<String> names = new ArrayList<>();
    public static final String PREFS_NAME = "LoginPrefs";
    public boolean clicked1 = false;
    public boolean clicked2 = false;
    TextView tv4;

    String name;
    static MediaPlayer mediaPlayer;
    static Ringtone ringTone;
    ToneGenerator tg;

    /*public String[] names = {"adem1@spidr.com","adem1@spidr.com","adem1@spidr.com","adem1@spidr.com","adem1@spidr.com",
            "adem1@spidr.com","adem1@spidr.com","adem1@spidr.com","emin1@spidr.com","android1@sdkpush.4d7m.att.com","android2@sdkpush.4d7m.att.com"};*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiocall);

        callList = findViewById(R.id.callList);
        Toolbar toolbar = findViewById(R.id.toolbar);
        tv4 = findViewById(R.id.textView4);

        setSupportActionBar(toolbar);
        Configuration.getInstance().setOrientationMode(OrientationMode.CAMERA_ORIENTATION_USES_NONE);

        ServiceProvider serviceProvider = ServiceProvider.getInstance(getApplicationContext());
        callService = serviceProvider.getInstance(getApplicationContext()).getCallService();
        try {
            callService.setCallApplication(this);
        } catch (MobileException e) {
            e.printStackTrace();
        }

        tv4.setVisibility(View.VISIBLE);

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

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,names);
        callList.setAdapter(adapter);

        callList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                name = names.get(i);
                MyDialog();
            }
        });
    }

    public void MyDialog(){
        final Dialog MyDialog = new Dialog(AudioCallActivity.this);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.custom_dialog);
        MyDialog.setTitle("PersonCard");

        Button audiocall = MyDialog.findViewById(R.id.audiocall);
        Button videocall = MyDialog.findViewById(R.id.videocall);
        TextView calleeName = MyDialog.findViewById(R.id.calleeName);

        calleeName.setText(name);
        audiocall.setEnabled(true);
        videocall.setEnabled(true);

        audiocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog.dismiss();
                callExample(name);
                tg = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, 100);
                tg.startTone(ToneGenerator.TONE_CDMA_DIAL_TONE_LITE);
                clicked1=true;
                clicked2=false;
                tv4.setVisibility(View.GONE);
            }
        });

        videocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked2=true;
                clicked1=false;
                MyDialog.dismiss();
                callExample(name);
                play();
                mediaPlayer.setLooping(true);
                tv4.setVisibility(View.GONE);
            }
        });

        MyDialog.show();
    }

    public void play(){
        mediaPlayer = MediaPlayer.create(AudioCallActivity.this,R.raw.sound);
        mediaPlayer.start();
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void tgStop(){
        if (tg != null) {
            tg.stopTone();
            tg.release();
            tg = null;
        }
    }

    public static boolean stopRingtone() {
        if(ringTone != null) {
            ringTone.stop();
            ringTone = null;
            return true;
        }
        else
            return false;
    }

    private void changeOrientationToLandscape() {
        CallService.getInstance().rotateCameraOrientationToPosition(ScreenOrientation.LANDSCAPE);
    }

    public void callExample(String name) {
        String terminatorAddress = name ;

        callService.createOutgoingCall(terminatorAddress, this, new OutgoingCallCreateInterface() {
            @Override
            public void callCreated(OutgoingCallInterface callInterface) {
                call = callInterface;

                if(clicked1) {
                    callInterface.establishAudioCall();
                    Fragment outGoingCallFragment = new OutGoingCallFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.flContainer, outGoingCallFragment, callInterface.getId());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    Bundle bundle = new Bundle();
                    bundle.putString("name", terminatorAddress);
                    outGoingCallFragment.setArguments(bundle);

                    AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setMode(AudioManager.MODE_RINGTONE);
                    audioManager.setSpeakerphoneOn(false);
                }
                else if(clicked2){
                    VideoView localVideoView = (VideoView)findViewById(R.id.localVideoView);
                    VideoView remoteVideoView = (VideoView)findViewById(R.id.remoteVideoView);

                    CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(callInterface.getId());
                    if (callStateListener != null) {
                        callStateListener.createVideoCall(call);
                    }
                    callInterface.establishCall(true);
                    Fragment videoOutGoingCall = new VideoOutGoingCallFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.flContainer, videoOutGoingCall, callInterface.getId());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    Bundle bundle = new Bundle();
                    bundle.putString("name", terminatorAddress);
                    videoOutGoingCall.setArguments(bundle);

                    AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setMode(AudioManager.MODE_RINGTONE);
                    audioManager.setSpeakerphoneOn(true);
                }
            }
            @Override
            public void callCreationFailed(MobileError error) {
                Log.d("Funda","CallFAİL");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.item1:

                RegistrationApplicationListener registrationListener = new RegistrationApplicationListener() {
                    @Override
                    public void registrationStateChanged(RegistrationStates state) {
                    }

                    @Override
                    public void notificationStateChanged(NotificationStates notificationStates) {

                    }

                    @Override
                    public void onInternalError(MobileError mobileError) {
                    }
                };
                final RegistrationService registrationService = ServiceProvider.getInstance(getApplicationContext()).getRegistrationService();
                registrationService.unregisterFromServer(new OnCompletionListener() {
                    @Override
                    public void onSuccess() {
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove("logged");
                        editor.commit();
                        Intent intent = new Intent(AudioCallActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onFail(MobileError error) {
                        Log.d("Funda", "Fail");
                    }
                });
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void incomingCall(IncomingCallInterface ıncomingCallInterface) {
        this.call = ıncomingCallInterface;

        VideoView localVideoView = findViewById(R.id.localVideoView);
        VideoView remoteVideoView = findViewById(R.id.remoteVideoView);

        ıncomingCallInterface.setLocalVideoView(localVideoView);
        ıncomingCallInterface.setRemoteVideoView(remoteVideoView);

        if(ıncomingCallInterface.canReceiveVideo()) {
            VideoIncomingCallFragment fragment = new VideoIncomingCallFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.flContainer, fragment, ıncomingCallInterface.getId());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringTone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            ringTone.play();

            Bundle bund = new Bundle();
            bund.putString("callername",call.getCallerAddress());
            bund.putString("callstateee","Dialing");
            fragment.setArguments(bund);

            tv4.setVisibility(View.GONE);

        }
        else {
            InComingCallFragment frag = new InComingCallFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.flContainer, frag, ıncomingCallInterface.getId());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            audioManager.setSpeakerphoneOn(false);

            tv4.setVisibility(View.GONE);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringTone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            ringTone.play();

            Bundle bund = new Bundle();
            bund.putString("callername", call.getCallerAddress());
            bund.putString("callstateee", call.getCallState().getType().toString());
            frag.setArguments(bund);
        }

        /*AlertDialog.Builder Builder = new AlertDialog.Builder(AudioCallActivity.this);
        Builder.setCancelable(true);
        Builder.setTitle(call.getCallerAddress());
        Builder.setMessage(call.getCallState().getType().toString());

        Builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ıncomingCallInterface.rejectCall();
            }
        });
        Builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ıncomingCallInterface.acceptCall(false);

                Fragment ınComingCallFragment = new InComingCallFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.f2Container,ınComingCallFragment,ıncomingCallInterface.getId());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                Bundle bund = new Bundle();
                bund.putString("callername",call.getCallerAddress());
                bund.putString("callstateee",call.getCallState().getType().toString());
                ınComingCallFragment.setArguments(bund);
            }
        });
        Builder.show();*/
    }


    @Override
    public void callStatusChanged(CallInterface callInterface, CallState callState) {

        callInterface.getMediaAttributes().getRemoteVideo();
        callInterface.getMediaAttributes().getLocalVideo();

        myCallState = callState.getType().toString();
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(callInterface.getId());
        if (callStateListener != null) {
            callStateListener.callStateChange(callInterface, callState);
        }

        switch (callState.getType()) {
            case INITIAL:
                Log.i("Call", "Call came");
                break;
            case SESSION_PROGRESS:
                Log.i("Call", "Call is in early media state");
                break;
            case ENDED:
                tgStop();
                stopPlaying();
                stopRingtone();
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(callInterface.getId());
                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(fragment)
                            .commit();
                }
                this.onBackPressed();
                callList.setVisibility(View.VISIBLE);
                Log.i("Call", "Callee does not exist");
                break;
            case RINGING:
                callList.setVisibility(View.GONE);
                Log.i("Call", "Callee is ringing now");
                break;
            case IN_CALL:
                //r.stop();
                stopRingtone();
                stopPlaying();
                tgStop();
                callList.setVisibility(View.GONE);
                Log.i("Call", "Call establishment is successful");
                break;
            case DIALING:
                callList.setVisibility(View.GONE);
                break;
            case ANSWERING:
                callList.setVisibility(View.GONE);
                break;
            case UNKNOWN:
                break;
            case ON_HOLD:
                stopPlaying();
                callList.setVisibility(View.GONE);
                break;
            case ON_DOUBLE_HOLD:
                stopPlaying();
                callList.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void mediaAttributesChanged(CallInterface callInterface, MediaAttributes mediaAttributes) {
        float remoteVideoAspectRatio = mediaAttributes.getRemoteVideoAspectRatio();
        float localVideoAspectRatio = mediaAttributes.getLocalVideoAspectRatio();

        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(callInterface.getId());
        if (callStateListener != null) {
            callStateListener.mediaAttributesChanged(call);
        }

    }

    @Override
    public void callAdditionalInfoChanged(CallInterface callInterface, Map<String, String> events) {
        call = callInterface;
        long time = Long.parseLong(events.get("time"));
        String action = events.get("action");
        String type = events.get("type");
        String callId = callInterface.getCallId();


        long callCreateTime = 0;
        if(type.equals(AdditionalInfoConstants.CALL_CREATE) || type.equals(AdditionalInfoConstants.INCOMING_CALL)) {
            callCreateTime = time;
        }

        long delay = time - callCreateTime;
        Log.i("Call", "Time from creating call until " + type + " is " + delay + "for callId" + callId );
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String,String > entry : events.entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append(",\n");
        }
        sb.append("}");
        Log.i("Call", "Detailed info is " + sb.toString());
    }

    @Override
    public void errorReceived(CallInterface callInterface, MobileError mobileError) {
    }

    @Override
    public void errorReceived(MobileError mobileError) {
    }

    @Override
    public void establishCallSucceeded(OutgoingCallInterface outgoingCallInterface) {
        Log.i("Call", "establish call is OK");
    }

    @Override
    public void establishCallFailed(OutgoingCallInterface outgoingCallInterface, MobileError mobileError) {

        Log.e("Call", "establish call failed : " + mobileError.getErrorMessage());
    }

    @Override
    public void acceptCallSucceed(IncomingCallInterface ıncomingCallInterface) {
        Log.i("Call", "accept call is OK");
    }

    @Override
    public void acceptCallFailed(IncomingCallInterface ıncomingCallInterface, MobileError mobileError) {
        Log.e("Call", "accept call failed : " + mobileError.getErrorMessage());
    }

    @Override
    public void rejectCallSuccedded(IncomingCallInterface ıncomingCallInterface) {
        //called when reject call succeeds
        Log.i("Call", "reject call is OK");
    }

    @Override
    public void rejectCallFailed(IncomingCallInterface ıncomingCallInterface, MobileError mobileError) {
        Log.e("Call", "reject call failed : " + mobileError.getErrorMessage());
    }

    @Override
    public void ignoreSucceed(IncomingCallInterface ıncomingCallInterface) {
        Log.i("Call", "ignore call is OK");
    }

    @Override
    public void ignoreFailed(IncomingCallInterface ıncomingCallInterface, MobileError mobileError) {
        Log.e("Call", "ignore call failed : " + mobileError.getErrorMessage());
    }

    @Override
    public void videoStartSucceed(CallInterface call) {
    //called when video start succeeds
        Log.i("Call", "video start is OK");
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.videoStateChange(call);
        }
    }
    @Override
    public void videoStartFailed(CallInterface call, MobileError error) {
    //called when video start fails
        Log.e("Call", "video start failed : " + error.getErrorMessage());
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.failStates(call);
        }

    }
    @Override
    public void videoStopSucceed(CallInterface call) {
    //called when video stop succeeds
        Log.i("Call", "video stop is OK");
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.videoStateChange(call);
        }
    }
    @Override
    public void videoStopFailed(CallInterface call, MobileError error) {
    //called when video stop fails
        Log.e("Call", "video stop failed : " + error.getErrorMessage());
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.failStates(call);
        }
    }

    @Override
    public void muteCallSucceed(CallInterface callInterface) {
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.voiceStateChange(call);
        }
    }

    @Override
    public void muteCallFailed(CallInterface callInterface, MobileError mobileError) {
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.failStates(call);
        }
    }

    @Override
    public void unMuteCallSucceed(CallInterface callInterface) {
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.voiceStateChange(call);
        }
    }

    @Override
    public void unMuteCallFailed(CallInterface callInterface, MobileError mobileError) {
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.failStates(call);
        }
    }

    @Override
    public void holdCallSucceed(CallInterface call) {
        Log.i("Call", "hold call is OK");
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.holdStateChange(call);
        }
    }
    @Override
    public void holdCallFailed(CallInterface call, MobileError error) {
        Log.e("Call", "hold call failed : " + error.getErrorMessage());
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.failStates(call);
        }
    }

    @Override
    public void transferCallSucceed(CallInterface callInterface) {
        Log.i("Call", "Transfer is OK");
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.transferCallListener(call);
        }
    }

    @Override
    public void transferCallFailed(CallInterface callInterface, MobileError mobileError) {
        Log.e("Call", "Transfer failed : " + mobileError.getErrorMessage());
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.failStates(call);
        }
    }

    @Override
    public void unHoldCallSucceed(CallInterface callInterface) {
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.holdStateChange(call);
        }
    }

    @Override
    public void unHoldCallFailed(CallInterface callInterface, MobileError mobileError) {
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(call.getId());
        if (callStateListener != null) {
            callStateListener.failStates(call);
        }
    }

    @Override
    public void sendCustomParametersSuccess(CallInterface callInterface) {
    }

    @Override
    public void sendCustomParametersFail(CallInterface callInterface, MobileError mobileError) {
    }

    @Override
    public void joinSucceeded(CallInterface callInterface) {
    }

    @Override
    public void joinFailed(CallInterface callInterface, MobileError mobileError) {
    }

    @Override
    public void endCallSucceeded(CallInterface callInterface) {
    }

    @Override
    public void endCallFailed(CallInterface callInterface, MobileError mobileError) {
    }

    @Override
    public void ringingFeedbackSucceeded(IncomingCallInterface ıncomingCallInterface) {
    }

    @Override
    public void ringingFeedbackFailed(IncomingCallInterface ıncomingCallInterface, MobileError mobileError) {
    }

    @Override
    public void notifyCallProgressChange(CallInterface callInterface) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(getApplicationContext(),"Please Logout",Toast.LENGTH_SHORT).show();
    }
}


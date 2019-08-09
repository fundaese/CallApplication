package com.example.callapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

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
import com.genband.mobile.api.utilities.MobileError;
import com.genband.mobile.api.utilities.exception.MobileException;
import com.genband.mobile.impl.services.call.CallState;
import com.genband.mobile.impl.services.call.MediaAttributes;
import com.genband.mobile.impl.utilities.constants.AdditionalInfoConstants;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;

/**
 * Created by user on 12.07.2019.
 */

public class AudioCallActivity extends AppCompatActivity implements CallApplicationListener{
    
    ImageView unregBtn;
    ListView callList;
    CallInterface call;
    Toolbar toolbar;
    public ArrayAdapter adapter;
    public static String getMyCallState() {return myCallState; }
    public static void setMyCallState(String myCallState) { AudioCallActivity.myCallState = myCallState; }
    public static String getCaller() { return caller; }
    private static String caller;
    private static String myCallState;
    CallServiceInterface callService;
    int stateNumber;
    OutGoingCallFragment outGoingCallFragment = new OutGoingCallFragment();
    InComingCallFragment ınComingCallFragment = new InComingCallFragment();

    private CallStateListener callStateListener;

    public void setCallStateListener(CallStateListener callStateListener) {
        this.callStateListener = callStateListener;
    }

    Handler handler = new Handler();
    Timer timer;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiocall);

        callList = (ListView) findViewById(R.id.callList);
        unregBtn = (ImageView) findViewById(R.id.unregBtn);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        ServiceProvider serviceProvider = ServiceProvider.getInstance(getApplicationContext());
        callService = serviceProvider.getInstance(getApplicationContext()).getCallService();
        try {
            callService.setCallApplication(this);
        } catch (MobileException e) {
            e.printStackTrace();
        }

        final ArrayList<String> names = new ArrayList<>();
        names.add("adem1@spidr.com");
        names.add("adem2@spidr.com");
        names.add("adem3@spidr.com");
        names.add("adem4@spidr.com");
        names.add("adem5@spidr.com");
        names.add("adem6@spidr.com");
        names.add("adem7@spidr.com");
        names.add("adem8@spidr.com");
        names.add("emin1@spidr.com");

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,names);
        callList.setAdapter(adapter);


        callList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                callExample(names.get(i));
            }
        });

        unregBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                RegistrationApplicationListener registrationListener = new RegistrationApplicationListener() {
                    @Override
                    public void registrationStateChanged(RegistrationStates state) {
                    }

                    @Override
                    public void notificationStateChanged(NotificationStates state) {
                    }

                    @Override
                    public void onInternalError(MobileError mobileError) {
                    }
                };
                final RegistrationService registrationService = ServiceProvider.getInstance(getApplicationContext()).getRegistrationService();
                registrationService.unregisterFromServer(new OnCompletionListener() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(AudioCallActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onFail(MobileError error) {
                        Log.d("Funda", "Fail");
                    }
                });
            }
        });
    }

    public void callExample(String name) {
        String terminatorAddress = name ;
        callService.createOutgoingCall(terminatorAddress, this, new OutgoingCallCreateInterface() {
            @Override
            public void callCreated(OutgoingCallInterface callInterface) {
                callInterface.establishAudioCall();
                CallInterface call;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.flContainer,outGoingCallFragment,callInterface.getId());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                Bundle bundle = new Bundle();
                bundle.putString("name", terminatorAddress);

                outGoingCallFragment.setArguments(bundle);
            }
            @Override
            public void callCreationFailed(MobileError error) {
                Log.d("Funda","CallFAİL");
            }
        });
    }

    @Override
    public void incomingCall(IncomingCallInterface ıncomingCallInterface) {
        this.call=ıncomingCallInterface;
        Log.d("Fundi" , call.getCallState().getType().toString()); //INITIAL
        AlertDialog.Builder Builder = new AlertDialog.Builder(AudioCallActivity.this);
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
        Builder.show();
    }

    @Override
    public void callStatusChanged(CallInterface callInterface, CallState callState) {

        myCallState = callState.getType().toString();
        CallStateListener callStateListener = (CallStateListener) getSupportFragmentManager().findFragmentByTag(callInterface.getId());
        if (callStateListener != null) {
            callStateListener.callStateChange(callInterface, callState);
        }

        switch (callState.getType()) {
            case INITIAL:
                Log.i("Call", "Call came");
                stateNumber = 0;
                break;
            case SESSION_PROGRESS:
                Log.i("Call", "Call is in early media state");
                break;
            case ENDED:
                stateNumber = 1;
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(callInterface.getId());
                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(fragment)
                            .commit();
                }
              this.onBackPressed();
                Log.i("Call", "Callee does not exist");
                break;
            case RINGING:
                stateNumber = 3;
                Log.i("Call", "Callee is ringing now");
                Log.d("MYINT", "value: " + stateNumber);
                break;
            case IN_CALL:
                stateNumber = 2;
                Log.i("Call", "Call establishment is successful");
                break;
            case DIALING:
                stateNumber = 4;
                break;
            case ANSWERING:
                stateNumber = 5;
                break;
            case UNKNOWN:
                stateNumber = 6;
                break;
            default:
                break;
        }

    }

    @Override
    public void mediaAttributesChanged(CallInterface callInterface, MediaAttributes mediaAttributes) {
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
    public void videoStopSucceed(CallInterface callInterface) {
    }

    @Override
    public void videoStopFailed(CallInterface callInterface, MobileError mobileError) {
    }

    @Override
    public void videoStartSucceed(CallInterface callInterface) {
    }

    @Override
    public void videoStartFailed(CallInterface callInterface, MobileError mobileError) {
    }

    @Override
    public void muteCallSucceed(CallInterface callInterface) {
    }

    @Override
    public void muteCallFailed(CallInterface callInterface, MobileError mobileError) {
    }

    @Override
    public void unMuteCallSucceed(CallInterface callInterface) {
    }

    @Override
    public void unMuteCallFailed(CallInterface callInterface, MobileError mobileError) {
    }

    @Override
    public void holdCallSucceed(CallInterface callInterface) {
    }

    @Override
    public void holdCallFailed(CallInterface callInterface, MobileError mobileError) {
    }

    @Override
    public void transferCallSucceed(CallInterface callInterface) {
    }

    @Override
    public void transferCallFailed(CallInterface callInterface, MobileError mobileError) {
    }

    @Override
    public void unHoldCallSucceed(CallInterface callInterface) {
    }

    @Override
    public void unHoldCallFailed(CallInterface callInterface, MobileError mobileError) {
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

    }
}


package com.example.callapplication;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.genband.mobile.api.services.call.CallApplicationListener;
import com.genband.mobile.api.services.call.CallInterface;
import com.genband.mobile.api.services.call.IncomingCallInterface;
import com.genband.mobile.api.services.call.OutgoingCallInterface;
import com.genband.mobile.api.utilities.MobileError;
import com.genband.mobile.impl.services.call.CallState;
import com.genband.mobile.impl.services.call.MediaAttributes;

import java.util.Map;

public class PopupFragment extends Fragment implements CallStateListener, CallApplicationListener {

    TextView callerName;
    TextView call_state;
    Button reject_call;
    Button accept_call;
    CallInterface call;
    public PopupFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_popup, container, false);
        callerName = (TextView) view.findViewById(R.id.caller_name);
        call_state = (TextView) view.findViewById(R.id.call_state);
        reject_call = view.findViewById(R.id.end_call);
        accept_call = view.findViewById(R.id.accept_call);

        callerName.setText(getArguments().getString("callername"));
        call_state.setText(getArguments().getString("callstateee"));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void callStateChange(CallInterface callInterface, CallState callState) {
        this.call = callInterface;
        Log.i("CALLSTATE", callState.getType().toString());
        if(callState.getType().equals(CallState.Type.IN_CALL)) {
            call_state.setText("In Call");
        }
        else if (callState.getType().equals(CallState.Type.DIALING)) {
            call_state.setText("Dialing");
        }
        else if(callState.getType().equals(CallState.Type.RINGING)){
            call_state.setText("Ringing"); }
        else if(callState.getType().equals(CallState.Type.ENDED)){
            Fragment fragment = getFragmentManager().findFragmentByTag(callInterface.getId());
            if (fragment != null) {
                getFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
            }

            call_state.setText("Ended");
        }
    }

    @Override
    public void incomingCall(IncomingCallInterface ıncomingCallInterface) {

        accept_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ıncomingCallInterface.acceptCall(false);

                Fragment ınComingCallFragment = new InComingCallFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.f3Container,ınComingCallFragment,ıncomingCallInterface.getId());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


                Bundle bund = new Bundle();
                bund.putString("callername",call.getCallerAddress());
                bund.putString("callstateee",call.getCallState().getType().toString());
                ınComingCallFragment.setArguments(bund);

                AudioCallActivity.mediaPlayer.stop();

            }
        });

        reject_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ıncomingCallInterface.rejectCall();
            }
        });
    }

    @Override
    public void callStatusChanged(CallInterface callInterface, CallState callState) {

    }

    @Override
    public void mediaAttributesChanged(CallInterface callInterface, MediaAttributes mediaAttributes) {

    }

    @Override
    public void callAdditionalInfoChanged(CallInterface callInterface, Map<String, String> map) {

    }

    @Override
    public void errorReceived(CallInterface callInterface, MobileError mobileError) {

    }

    @Override
    public void errorReceived(MobileError mobileError) {

    }

    @Override
    public void establishCallSucceeded(OutgoingCallInterface outgoingCallInterface) {

    }

    @Override
    public void establishCallFailed(OutgoingCallInterface outgoingCallInterface, MobileError mobileError) {

    }

    @Override
    public void acceptCallSucceed(IncomingCallInterface ıncomingCallInterface) {

    }

    @Override
    public void acceptCallFailed(IncomingCallInterface ıncomingCallInterface, MobileError mobileError) {

    }

    @Override
    public void rejectCallSuccedded(IncomingCallInterface ıncomingCallInterface) {

    }

    @Override
    public void rejectCallFailed(IncomingCallInterface ıncomingCallInterface, MobileError mobileError) {

    }

    @Override
    public void ignoreSucceed(IncomingCallInterface ıncomingCallInterface) {

    }

    @Override
    public void ignoreFailed(IncomingCallInterface ıncomingCallInterface, MobileError mobileError) {

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

}


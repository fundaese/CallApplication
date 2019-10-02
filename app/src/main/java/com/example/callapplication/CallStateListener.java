package com.example.callapplication;

import com.genband.mobile.api.services.call.CallInterface;
import com.genband.mobile.impl.services.call.CallState;

public interface CallStateListener {
    void callStateChange(CallInterface callInterface, CallState callState);
    void videoStateChange(CallInterface call);
    void voiceStateChange(CallInterface call);
    void holdStateChange(CallInterface call);
    void failStates(CallInterface call);
    void createVideoCall(CallInterface call);
    void transferCallListener(CallInterface call);
    void mediaAttributesChanged(CallInterface call);
}

package com.example.callapplication;

import com.genband.mobile.api.services.call.CallInterface;
import com.genband.mobile.impl.services.call.CallState;

public interface CallStateListener {
    void callStateChange(CallInterface callInterface, CallState callState);
}

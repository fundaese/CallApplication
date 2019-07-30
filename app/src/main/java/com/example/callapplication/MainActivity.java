package com.example.callapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.genband.mobile.NotificationStates;
import com.genband.mobile.OnCompletionListener;
import com.genband.mobile.RegistrationApplicationListener;
import com.genband.mobile.RegistrationService;
import com.genband.mobile.RegistrationStates;
import com.genband.mobile.ServiceProvider;
import com.genband.mobile.api.services.call.CallInterface;
import com.genband.mobile.api.utilities.Configuration;
import com.genband.mobile.api.utilities.Constants;

import com.genband.mobile.api.utilities.ICEServers;
import com.genband.mobile.api.utilities.MobileError;

import static com.example.callapplication.R.*;
import static com.genband.mobile.api.utilities.Constants.LogLevel.TRACE;

public class MainActivity extends AppCompatActivity {

    EditText userName;
    EditText userPassword;
    Button regBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = (EditText) findViewById(R.id.userName);
        userPassword = (EditText) findViewById(R.id.userPassword);
        regBtn = (Button) findViewById(R.id.regBtn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    configExample();
                    RegistrationApplicationListener registrationListener = new RegistrationApplicationListener() {
                        @Override
                        public void registrationStateChanged(RegistrationStates state) {
                            // Handle registration state changes
                        }

                        @Override
                        public void notificationStateChanged(NotificationStates state) {
                            // Handle notification state changes
                        }

                        @Override
                        public void onInternalError(MobileError mobileError) {

                        }
                    };

                    ServiceProvider serviceProvider = ServiceProvider.getInstance(getApplicationContext());
                    final RegistrationService registrationService = ServiceProvider.getInstance(getApplicationContext()).getRegistrationService();
                    registrationService.setRegistrationApplicationListener(registrationListener);

                    Constants.SubscribeServices[] subscribeServices = {Constants.SubscribeServices.Call};
                    registrationService.registerToServer(subscribeServices, 3600, new OnCompletionListener() {
                        @Override
                        public void onSuccess() {
                            Intent intent = new Intent(MainActivity.this, AudioCallActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFail(MobileError mobileError) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                            mBuilder.setCancelable(true);
                            mBuilder.setTitle("OPS!!!");
                            mBuilder.setMessage("Incorrect username or password");
                            Log.d("Funda", "FAIL ");

                            mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            mBuilder.show();
                        }
                    });
                }
        });
    }

    public void configExample() {
        Configuration configuration = Configuration.getInstance();
        Constants.LogLevel logLevel = configuration.getLogLevel();
        logLevel = TRACE;
        configuration.setLogLevel(logLevel);
        configuration.setUsername(userName.getText().toString().trim());
        configuration.setPassword(userPassword.getText().toString().trim());
        configuration.setRestServerIp("47.168.161.85");
        configuration.setRestServerPort(8580);
        configuration.setRequestHttpProtocol(true);
        configuration.setSecuredWSProtocol(false);
        configuration.setDTLS(false);
        configuration.setWebSocketServerIp("47.168.161.85");
        configuration.setWebSocketServerPort(8581);
        configuration.setICECollectionTimeout(5);

        ICEServers iceServers = new ICEServers();
        iceServers.addICEServer("turn:multi1turn.netas.com.tr:443?transport=tcp");
        configuration.setICEServers(iceServers);
    }
}

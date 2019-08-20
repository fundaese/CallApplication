package com.example.callapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.genband.mobile.NotificationStates;
import com.genband.mobile.OnCompletionListener;
import com.genband.mobile.RegistrationApplicationListener;
import com.genband.mobile.RegistrationService;
import com.genband.mobile.RegistrationStates;
import com.genband.mobile.ServiceProvider;
import com.genband.mobile.api.utilities.Configuration;
import com.genband.mobile.api.utilities.Constants;
import com.genband.mobile.api.utilities.ICEServers;
import com.genband.mobile.api.utilities.MobileError;

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
        userName.setText("3221032@cucm1.spidrmulti.netas.lab.nortel.com");
        //userName.setText("3221031@cucm1.spidrmulti.netas.lab.nortel.com");
        userPassword = (EditText) findViewById(R.id.userPassword);
        userPassword.setText("135792468");
        regBtn = (Button) findViewById(R.id.regBtn);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("username", userName.getText().toString());
        editor.putString("userpassword", userPassword.getText().toString());


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
                    final RegistrationService registrationService = serviceProvider.getInstance(getApplicationContext()).getRegistrationService();
                    registrationService.setRegistrationApplicationListener(registrationListener);

                    Constants.SubscribeServices[] subscribeServices = {Constants.SubscribeServices.Call};
                    registrationService.registerToServer(subscribeServices, 3600, new OnCompletionListener() {
                        @Override
                        public void onSuccess() {
                            Intent intent = new Intent(MainActivity.this, AudioCallActivity.class);
                            intent.putExtra("username",userName.getText());
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

    @Override
    public void onBackPressed() {

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
        configuration.setAuthorizationName("c_3221032");
        //configuration.setAuthorizationName("c_3221031");

        ICEServers iceServers = new ICEServers();
        iceServers.addICEServer("turn:multi1turn.netas.com.tr:443?transport=tcp");
        configuration.setICEServers(iceServers);
    }
}

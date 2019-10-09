package com.example.callapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

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

    ProgressDialog progressDialog;

    public static final String PREFS_NAME = "LoginPrefs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = findViewById(R.id.userName);
        userName.setText("adem8@spidr.com");

        //userName.setText("3221030@cucm1.spidrmulti.netas.lab.nortel.com");
        //userName.setText("3221032@cucm1.spidrmulti.netas.lab.nortel.com");
        //userName.setText("3221045@cucm1.spidrmulti.netas.lab.nortel.com");
        //userName.setText("android1@sdkpush.4d7m.att.com");
        //userName.setText("3221031@cucm1.spidrmulti.netas.lab.nortel.com");
        //userName.setText("android1@sdkpush.4d7m.att.com");

        userPassword = findViewById(R.id.userPassword);
        userPassword.setText("1234");
        //userPassword.setText("135792468");
        //userPassword.setText("3456");
        //userPassword.setText("GrnDjUP750");//android1
        //userPassword.setText("syuuKmH908"); //android2


        regBtn =  findViewById(R.id.regBtn);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getString("logged", "").toString().equals("logged")) {
            configExample();
            registerToServer();
        }

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configExample();
                registerToServer();

                progressDialog = new ProgressDialog(MainActivity.this);
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
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();
            }
        });

        userPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    registerToServer();
                }
                return false;
            }
        });
    }

    private void registerToServer() {
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
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("logged", "logged");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), AudioCallActivity.class);
                startActivity(intent);

                int expirationTime = registrationService.getExpirationTime();
                Log.i("ExpirationTime", String.valueOf(expirationTime));

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFail(MobileError mobileError) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setCancelable(true);
                mBuilder.setTitle("OPS!!!");
                mBuilder.setMessage("Incorrect username or password");
                Log.d("Funda", "FAIL ");

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

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

    public void configExample() {
        Configuration configuration = Configuration.getInstance();
        Constants.LogLevel logLevel = configuration.getLogLevel();
        logLevel = TRACE;
        configuration.setLogLevel(logLevel);
        configuration.setUsername(userName.getText().toString().trim());
        configuration.setPassword(userPassword.getText().toString().trim());

        configuration.setRestServerIp("47.168.161.85");
        configuration.setRestServerIp("47.168.161.85");
        configuration.setRestServerPort(8580);
        configuration.setRequestHttpProtocol(true);
        configuration.setSecuredWSProtocol(false);
        configuration.setDTLS(false);
        configuration.setWebSocketServerIp("47.168.161.85");
        configuration.setWebSocketServerPort(8581);
        configuration.setAuditEnabled(true);
        configuration.setAuditFrequence(30);
        //configuration.setICECollectionTimeout(5);
        //configuration.setAuthorizationName("c_3221032");
        //configuration.setAuthorizationName("SDK45");
        //configuration.setAuthorizationName("c_3221030");
        //configuration.setAuthorizationName("c_3221031");
        /*

        configuration.setRestServerIp("spidr-nds.genband.com");
        configuration.setRestServerPort(443);
        configuration.setRequestHttpProtocol(false);
        configuration.setSecuredWSProtocol(true);
        configuration.setDTLS(true);
        configuration.setWebSocketServerIp("spidr-nds.genband.com");
        configuration.setWebSocketServerPort(443);
        configuration.setSecuredWSProtocol(true);
        */
        ICEServers iceServers = new ICEServers();
        iceServers.addICEServer("turn:multi1turn.netas.com.tr:443?transport=tcp");
        configuration.setICEServers(iceServers);
    }

    @Override
    public void onBackPressed() {}


}

package in.motorindiaonline.motorindia.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import in.motorindiaonline.motorindia.R;
import in.motorindiaonline.motorindia.Utilities.AlertDialogManager;
import in.motorindiaonline.motorindia.Utilities.CommonUtilities;
import in.motorindiaonline.motorindia.Utilities.ConnectionDetector;
import in.motorindiaonline.motorindia.ServerInteraction.ServerUtilities;
import in.motorindiaonline.motorindia.Utilities.WakeLocker;

public class GCMRegister extends ActionBarActivity {

    // AsyncTask used to register the user
    AsyncTask<Void, Void, Void> mRegisterTask;

    AlertDialogManager alert = new AlertDialogManager();

    // UI elements
    EditText userName;
    EditText userEmail;

    // Register button
    Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcmregister);
        Log.i(CommonUtilities.TAG, "OnCreate of GCMRegister");
    }

    @Override
    protected void onStart(){
        super.onStart();
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        // Check if Internet present
        if (!connectionDetector.isConnectedInternet()) {
            // Internet Connection is not present
            // Here as I need the dialog to be a inner class to send the intent and call finish
            new AlertDialog.Builder(this)
                    .setTitle("Internet Connection Error")
                    .setMessage("Please connect to working Internet connection")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(CommonUtilities.TAG,"ok has been clicked");
                            Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(CommonUtilities.TAG,"Cancel has been clicked");
                            finish();
                        }
                    })
                    .setIcon(R.drawable.alert)
                    .setCancelable(false)
                    .show();
        }
    }

    public void SubmitRegistrationDetails(View view) {
        userName       = (EditText) findViewById(R.id.EditTextFullName);
        userEmail      = (EditText) findViewById(R.id.EditTextEmailID);
        buttonRegister = (Button) findViewById(R.id.ButtonGCMRegister);

        final String name  = userName.getText().toString();
        final String email = userEmail.getText().toString();

        // Check if user filled the form
        if (name.trim().length() > 0 && email.trim().length() > 0) {
            Log.i(CommonUtilities.TAG,"User has submitted some data");

            // Store the name and email into AppData
            SharedPreferences.Editor editor = getSharedPreferences("GENERAL_DATA", MODE_PRIVATE).edit();
            editor.putString("USER_NAME", name);
            editor.putString("USER_EMAIL", email);
            editor.apply();

            // DEBUGGING
            Log.i(CommonUtilities.TAG,"registering with name: "+name+" and email: "+email);

            // START
            // Make sure the device has the proper dependencies.
            GCMRegistrar.checkDevice(this);

            // Make sure the manifest was properly set
            GCMRegistrar.checkManifest(this);

            Log.i(CommonUtilities.TAG,"Before broadcast receiver is set ");
            // Set a broadcast receiver for incoming GCM notifications
            registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
            Log.i(CommonUtilities.TAG,"After broadcast receiver is set ");

            // Get GCM registration id
            final String regId = GCMRegistrar.getRegistrationId(this);

            Log.i(CommonUtilities.TAG,"This is the regID: "+regId+"  <-- before this");
            // Check if RegistrationId is not present
            if (regId.equals("")) {
                Log.i(CommonUtilities.TAG,"Registration is not present, going to register with google");

                // Registration is not present, register now with GCM
                GCMRegistrar.register(this, CommonUtilities.SENDER_ID);

                // Keep in mind that in.motorindiaonline.motorindia.gcm.GCMIntentService.onRegistered() is called after
                // they register the mobile with Google

                Log.i(CommonUtilities.TAG,"Registration will happen async with MainUI moving on to the ArticleList");

                Intent myIntent = new Intent(GCMRegister.this, ArticleList.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                GCMRegister.this.startActivity(myIntent);
            }
            else {
                // Device is already registered on GCM
                if (GCMRegistrar.isRegisteredOnServer(this)) {
                    // Skips registration.
                    Log.i("DEBUG","this device is already registered with an ID on the google server "+regId);
                    editor.putBoolean("GCM_REGISTRATION_STATUS", GCMRegistrar.isRegisteredOnServer(this));
                    Log.i(CommonUtilities.TAG,"Is registration now required? - "+GCMRegistrar.isRegisteredOnServer(this));
                    Intent myIntent = new Intent(GCMRegister.this, ArticleList.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    GCMRegister.this.startActivity(myIntent);
                }
                else {
                    // As no such registration has happened on google server
                    // Try to register again, but not in the UI thread.
                    // It's also necessary to cancel the thread onDestroy(),
                    // hence the use of AsyncTask instead of a raw thread.
                    Log.i(CommonUtilities.TAG,"Going to register the device with google server");
                    final Context context = this;
                    mRegisterTask = new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            // Register on our server
                            // On server creates a new user
                            ServerUtilities.register(context, name, email, regId);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            mRegisterTask = null;
                        }
                    };
                    mRegisterTask.execute(null, null, null);
                    editor.putBoolean("GCM_REGISTRATION_STATUS", GCMRegistrar.isRegisteredOnServer(this));
                    Log.i(CommonUtilities.TAG,"Is registration now required? - "+GCMRegistrar.isRegisteredOnServer(this));
                    // We don't have to worry, we can move on to the actual app,
                    // In the background the registration will be going on
                    Intent myIntent = new Intent(GCMRegister.this, ArticleList.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    GCMRegister.this.startActivity(myIntent);
                }
            }
        } else {
            Log.i(CommonUtilities.TAG,"User has tried registering with empty fields");
            // user hasn't filled that data ask him to fill the form
            alert.showAlertDialog(GCMRegister.this, "Registration Error!", "Please enter your details", false);
        }

    }

    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the message from the incoming GCM
            String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
            // Message has been fetched
            Log.i(CommonUtilities.TAG,newMessage);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */

            // Showing received message
            Toast.makeText(getApplicationContext(), "ALERT! " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            WakeLocker.release();
        }
    };

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            Log.i("DEBUG","We just canceled the asyncTask which was registering user");
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(getApplicationContext());
        } catch (Exception e) {
            Log.i("DEBUG","error!!");
            Log.e("UnRegisterReceiverError", e.getMessage());
        }
        super.onDestroy();
    }
}

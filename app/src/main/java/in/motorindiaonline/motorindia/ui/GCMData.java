package in.motorindiaonline.motorindia.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import in.motorindiaonline.motorindia.R;
import in.motorindiaonline.motorindia.Utilities.AlertDialogManager;
import in.motorindiaonline.motorindia.Utilities.ConnectionDetector;
import in.motorindiaonline.motorindia.Utilities.MotorIndiaPreferences;
import in.motorindiaonline.motorindia.gcm.RegistrationIntentService;

public class GCMData extends ActionBarActivity {

    private static final String TAG = "GCM Data collection";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcmregister);

        Log.i(TAG,"checking connection");
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        // Check if Internet present
        if (!connectionDetector.isConnectedInternet()) {
            // Internet Connection is not present, I make a special alert to get the user to access internet
            // Here as I need the dialog to be a inner class to send the intent and call finish
            new AlertDialog.Builder(this)
                    .setTitle("Internet Connection Error")
                    .setMessage("Please connect to working Internet connection")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(TAG, "ok has been clicked");
                            Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(TAG, "Cancel has been clicked");
                            finish();
                        }
                    })
                    .setIcon(R.drawable.alert)
                    .setCancelable(false)
                    .show();
        }
    }

    public void SubmitRegistrationDetails(View view) {
        EditText userName = (EditText) findViewById(R.id.EditTextFullName);
        EditText userEmail = (EditText) findViewById(R.id.EditTextEmailID);
        CheckBox notificationCheckBok = (CheckBox) findViewById(R.id.checkbox);

        final String name = userName.getText().toString();
        final String eMail = userEmail.getText().toString();
        final Boolean sendNotification = notificationCheckBok.isChecked();

        // Check if user filled the form
        if (name.trim().length() > 0 && eMail.trim().length() > 0) {
            Log.i(TAG, "User has actually submitted some data");

            // Store the name and email into AppData
            SharedPreferences.Editor editor = getSharedPreferences(MotorIndiaPreferences.GENERAL_DATA, MODE_PRIVATE).edit();
            editor.putString(MotorIndiaPreferences.USER_NAME, name);
            editor.putString(MotorIndiaPreferences.USER_EMAIL, eMail);
            editor.putBoolean(MotorIndiaPreferences.SEND_NOTIFICATIONS, sendNotification);
            editor.apply();

            Log.i(TAG, " Saved DATA to App memory");
            Log.i(TAG, "registering with name: " + name + " and email: " + eMail + " and Send Notifications: " + sendNotification.toString());

            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (checkPlayServices()) {
                Log.i(TAG, "MOBILE has PlayServices Installed");
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                intent.putExtra("NAME",name);
                intent.putExtra("EMAIL", eMail);
                startService(intent);
                Log.i(TAG, "Started intent to RegistrationIntentService to fetch registration token");
            }
            else{
                AlertDialogManager alert = new AlertDialogManager();
                alert.showAlertDialog(this,"ERROR","Install PlayServices",Boolean.FALSE);
            }
        }
        Intent myIntent = new Intent(GCMData.this, ArticleList.class);
        // so that the user cant go back to splash screen and registration screen
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        GCMData.this.startActivity(myIntent);
    }

    // Pretty self explanatory :p
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
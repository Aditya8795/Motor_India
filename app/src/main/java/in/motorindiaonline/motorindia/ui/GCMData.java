package in.motorindiaonline.motorindia.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.UUID;

import in.motorindiaonline.motorindia.R;
import in.motorindiaonline.motorindia.Utilities.AlertDialogManager;
import in.motorindiaonline.motorindia.Utilities.MotorIndiaPreferences;
import in.motorindiaonline.motorindia.gcm.RegistrationIntentService;

public class GCMData extends ActionBarActivity {

    private static final String TAG = "GCM Data collection";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcmregister);

        // Just avoid registration screen
        SubmitRegistrationDetails(null);
    }

    public static String getDeviceID(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice, tmSerial, androidId;

        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

    public void SubmitRegistrationDetails(View view) {
        EditText userName = (EditText) findViewById(R.id.EditTextFullName);
        EditText userEmail = (EditText) findViewById(R.id.EditTextEmailID);
        CheckBox notificationCheckBok = (CheckBox) findViewById(R.id.checkBoxNotification);

        //final String name = userName.getText().toString();
        //final String eMail = userEmail.getText().toString();
        //final Boolean sendNotification = notificationCheckBok.isChecked();

        String AndroidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG,"Android ID : "+AndroidID);
        String DeviceID = getDeviceID(this);
        Log.d(TAG,"Device ID : "+DeviceID);
        final Boolean sendNotification = Boolean.TRUE; // By default

        // Check if user filled the form
        if (AndroidID.trim().length() > 0 && DeviceID.trim().length() > 0) {
            Log.i(TAG, "User has actually submitted some data");

            // Store the name and email into AppData
            SharedPreferences.Editor editor = getSharedPreferences(MotorIndiaPreferences.GENERAL_DATA, MODE_PRIVATE).edit();
            editor.putString(MotorIndiaPreferences.USER_NAME, AndroidID);
            editor.putString(MotorIndiaPreferences.USER_EMAIL, DeviceID);
            editor.putBoolean(MotorIndiaPreferences.SEND_NOTIFICATIONS, sendNotification);
            editor.apply();

            Log.i(TAG, " Saved DATA to App memory");
            Log.i(TAG, "registering with name: " + AndroidID + " and email: " + DeviceID + " and Send Notifications: " + sendNotification.toString());

            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (checkPlayServices()) {
                Log.i(TAG, "MOBILE has PlayServices Installed");
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                intent.putExtra("NAME",AndroidID);
                intent.putExtra("EMAIL", DeviceID);
                startService(intent);
                Log.i(TAG, "Started intent to RegistrationIntentService to fetch registration token");
            }
            else{
                AlertDialogManager alert = new AlertDialogManager();
                alert.showAlertDialog(this,"ERROR","Install PlayServices",Boolean.FALSE);
            }
        }

        Intent myIntent = new Intent(GCMData.this, MainMenu.class);
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
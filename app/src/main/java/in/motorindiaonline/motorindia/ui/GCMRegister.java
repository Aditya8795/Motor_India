package in.motorindiaonline.motorindia.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import in.motorindiaonline.motorindia.R;
import in.motorindiaonline.motorindia.Utilities.AlertDialogManager;
import in.motorindiaonline.motorindia.Utilities.CommonUtilities;
import in.motorindiaonline.motorindia.Utilities.ConnectionDetector;

public class GCMRegister extends ActionBarActivity {

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
    }

    @Override
    protected void onStart(){
        super.onStart();
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        // Check if Internet present
        if (!connectionDetector.isConnectingToInternet()) {
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

        String name  = userName.getText().toString();
        String email = userEmail.getText().toString();

        // Check if user filled the form
        if (name.trim().length() > 0 && email.trim().length() > 0) {

            //TODO more things here!!
            // DEBUGGING
            Log.i("DEBUG", "register activity is done");
            Log.i("DEBUG","registering with name: "+name+" and email: "+email);
        } else {
            // user hasn't filled that data
            // ask him to fill the form
            alert.showAlertDialog(GCMRegister.this, "Registration Error!", "Please enter your details", false);
        }
    }
}

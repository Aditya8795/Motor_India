package in.motorindiaonline.motorindia.gcm;

/*
    As we can see this service being called, it is called from line - 83 MainActivity
    now what is this IntentService, basically we offload the whole registration task
    from an application's main thread to a worker thread.
    Once started, a service can run in the background indefinitely, even if the component
    that started it is destroyed -- that's one of the reasons why we use this.

*/

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import in.motorindiaonline.motorindia.R;
import in.motorindiaonline.motorindia.ServerInteraction.ServerUtilities;
import in.motorindiaonline.motorindia.Utilities.CommonData;
import in.motorindiaonline.motorindia.Utilities.MotorIndiaPreferences;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        // used to name the worker thread
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            /*
             A synchronized block in Java is synchronized on some object, here its TAG
             All synchronized blocks synchronized on the same object can only have one thread
             executing inside them at the same time. All other threads attempting to enter the
             synchronized block are blocked until the thread inside the synchronized block
             exits the block.
            */
            synchronized (TAG) {
                /*
                    A sender ID is a project number acquired from the API console, as described in Getting Started.
                    I have NO CLUE (maybe just that its some sort of ah no idea) TODO what is GoogleCloudMessaging.INSTANCE_ID_SCOPE

                */
                // fetch the data passed through the intent as extras
                String name = intent.getStringExtra("NAME");
                String email = intent.getStringExtra("EMAIL");

                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // [START get_token]
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.i(TAG, "GCM Registration Token: " + token);

                // Now that we have this token that identifies the device we pass it on to OUR server
                // so that the server can tell google to whom all the notification should go to
                // TODO: Implement this method to send any registration to your app's servers.
                sendRegistrationToServer(token,name,email);

                // Subscribe to topic channels
                subscribeTopics(token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                sharedPreferences.edit().putBoolean(MotorIndiaPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                // [END register_for_gcm]
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(MotorIndiaPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(MotorIndiaPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token,String name, String email) {
        // Add custom implementation, as needed.
        //TODO code goes here :p
        // REMEMBER ABOUT boolean sentToken = sharedPreferences.getBoolean(MotorIndiaPreferences.SENT_TOKEN_TO_SERVER, false);
        Log.i(CommonData.TAG,"Going to register the device with google server");
        final Context context = this;
        ServerUtilities.register(context,name, email, token);
    }

    // In our case its just the global topic, when we make a app using
    // GCM topics is THE WAY to go when you want to send the same message to multiple people
    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}


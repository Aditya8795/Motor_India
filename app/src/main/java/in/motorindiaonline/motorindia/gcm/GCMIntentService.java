package in.motorindiaonline.motorindia.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import in.motorindiaonline.motorindia.R;
import in.motorindiaonline.motorindia.Utilities.CommonUtilities;
import in.motorindiaonline.motorindia.ServerInteraction.ServerUtilities;
import in.motorindiaonline.motorindia.ui.SplashScreen;

public class GCMIntentService extends GCMBaseIntentService {

    public GCMIntentService() {
        super(CommonUtilities.SENDER_ID);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        String message = intent.getExtras().getString("price");
        Log.i(TAG, "Received message is "+message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    protected void onError(Context context, String s) {
        Log.i(TAG,"GCM ERROR");
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        CommonUtilities.displayMessage(context, "Your device is registered with GCM");

        SharedPreferences prefs = getSharedPreferences("GENERAL_DATA", MODE_PRIVATE);
        final String name = prefs.getString("USER_NAME", "Unknown!");
        final String email = prefs.getString("USER_EMAIL", "Unknown!");

        Log.i(TAG,"Sending data to OUR server name: "+name+" email: "+email+" regId: "+registrationId);
        //HERE once the mobile is registered with google using the Project Number
        //we send the user data to our server so that we can send notifications to him/her
        ServerUtilities.register(context, name, email, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        CommonUtilities.displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.icon_tentative;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        // TODO this is deprecated, find an alternative
        Notification notification = new Notification(icon, message, when);

        String title = context.getString(R.string.app_name);

        // Specifying where clicking on the notification sends you
        Intent notificationIntent = new Intent(context, SplashScreen.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        //IF YOU WANT custom notification sound
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);
    }
}

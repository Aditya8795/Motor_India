package in.motorindiaonline.motorindia.Utilities;

import android.content.Context;
import android.content.Intent;

public class CommonUtilities {
    //TODO get a proper server with the PHP files up

    // give your server registration url here
    public static final String SERVER_URL = "http://742ece35.ngrok.com/gcm_server_php/register.php";

    // Google project id
    public static final String SENDER_ID = "256673637169";

    // Amount of time (in milliseconds) that the splash screen should be shown
    public static final int SPLASH_SCREEN_DELAY = 1500;

    public static final String TAG = "Spider MotorIndia";

    public static final String DISPLAY_MESSAGE_ACTION = "in.motorindiaonline.motorindia.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */

    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}

package in.motorindiaonline.motorindia.Utilities;

public class CommonData {

    // give your server registration url here gcm_server_php/register.php
    public static final String SERVER_URL = "http://4e25eec.ngrok.com/";
    // TODO WHY?
    public static final String SENDER_ID = "544864356150";
    // Amount of time (in milliseconds) that the splash screen should be shown
    public static final int SPLASH_SCREEN_DELAY = 1500;


    // when we send the user to show him the article he clicked, this is used as the 'key' for sending the 'id' of the article to be displayed
    // and other keys
    public final static String EXTRA_MESSAGE = "com.spider.motorindia.ID";
    public final static String EXTRA_URL = "com.spider.motorindia.URL";
    public final static String EXTRA_TITLE = "com.spider.motorindia.TITLE";
    public final static String GENERAL_DATA = "com.spider.motorindia.GENERAL_DATA";
    public final static String GCM_REGISTRATION_STATUS = "com.spider.motorindia.GCM_REGISTRATION_STATUS";
    public final static String TOKEN_WENT_TO_SERVER = "com.spider.motorindia.TOKEN_WENT_TO_SERVER";
    public final static String SEND_NOTIFICATIONS = "com.spider.motorindia.SEND_NOTIFICATIONS";
    public final static String USER_NAME = "com.spider.motorindia.USER_NAME";
    public final static String USER_EMAIL = "com.spider.motorindia.USER_EMAIL";
}

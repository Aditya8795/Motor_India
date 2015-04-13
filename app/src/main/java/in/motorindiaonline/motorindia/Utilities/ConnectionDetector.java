package in.motorindiaonline.motorindia.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {
    private Context _context;

    public ConnectionDetector(Context context){
        this._context = context;
    }

    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectedInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        //Now we might be connected to a wifi or even the local tower
                        // which in turn might NOT be connected to the Global Internet
                        // We can ping google to check if we are in a network which has access
                        // to most of the webPages.
                        // THUS we shall add httpParameters for ALL network operations otherwise
                        // we will have the app crash after waiting for a long time
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

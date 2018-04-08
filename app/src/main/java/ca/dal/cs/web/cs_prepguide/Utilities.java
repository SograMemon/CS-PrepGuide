package ca.dal.cs.web.cs_prepguide;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by vamshikrishnamoogala on 2018-03-30.
 */

/**
 * Class used for common functions that are used throughout the app
 */
public class Utilities {

    /**
     * Method to check the network connection status.
     * returns true when network is available
     */
    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
    }

}

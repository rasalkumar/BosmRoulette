package info.androidhive.bosmroulette.activity;

import android.content.Context;
import android.content.Intent;

/**
 * Created by the master mind Mr.Shivam Gupta on 11/09/2015.
 */
public final class CommonUtilities {

    // give your server registration url here
    static final String SERVER_URL = "http://doctordhoondo.org/bosm/bosm/";

    // Google project id
    static final String SENDER_ID = "462743600548";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "AndroidHive GCM";

    static final String DISPLAY_MESSAGE_ACTION =
            "info.androidhive.bosmroullete.DISPLAY_MESSAGE";

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
package info.androidhive.bosmroulette.activity;

import android.content.Context;

/**
 * Created by teja on 9/13/2015.
 */
public class GCMBroadcastReceiver extends
        com.google.android.gcm.GCMBroadcastReceiver {

    @Override
    protected String getGCMIntentServiceClassName(Context context) {

        return "info.androidhive.bosmroulette.activity.GCMIntentService";
    }
}
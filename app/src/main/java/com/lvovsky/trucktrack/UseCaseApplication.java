package com.lvovsky.trucktrack;

import android.app.Application;

import com.hypertrack.lib.HyperTrack;

/**
 * Created by Aman Jain on 15/05/17.
 */

public class UseCaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
       // FirebaseApp.initializeApp(this);
        // Initialize HyperTrack SDK with the Publishable Key
        // Refer to documentation at https://docs.hypertrack.com/gettingstarted/authentication.html#publishable-key
        // @NOTE: Add **YOUR_PUBLISHABLE_KEY** here for SDK to be authenticated with HyperTrack Server
        HyperTrack.initialize(this, "pk_6d208cbb7314d5d22eb6b131425a291b79236954");

        // Enable HyperTrack SDK Debug Logging, if required
        // Refer to the details
        // HyperTrack.enableDebugLogging(Log.VERBOSE);
    }
}

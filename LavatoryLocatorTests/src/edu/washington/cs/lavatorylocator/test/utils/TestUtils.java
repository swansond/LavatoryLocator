package edu.washington.cs.lavatorylocator.test.utils;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.jayway.android.robotium.solo.Solo;

/**
 * Utilities for testing.
 * 
 * @author Chris Rovillos
 * 
 */
public final class TestUtils {
    /**
     * Prevent instantiation of this class.
     */
    private TestUtils() {
    }

    /**
     * Dismisses the Google Play Services error dialog if present.
     * 
     * @param solo
     *            the {@link Solo} object used for testing
     */
    public static void dismissGooglePlayServicesErrorDialog(Solo solo) {
        final Context applicationContext = solo.getCurrentActivity()
                .getApplicationContext();
        if (!googlePlayServicesAreAvailable(applicationContext)) {
            solo.goBack();
        }
    }

    /**
     * Returns true if Google Play Services are available. Returns false
     * otherwise.
     * 
     * @param context
     *            the application context
     * @return true if Google Play Services are available; false otherwise
     */
    private static boolean googlePlayServicesAreAvailable(Context context) {
        final int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(context);

        return (resultCode == ConnectionResult.SUCCESS);
    }
}

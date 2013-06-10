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
     * The amount of milliseconds to wait before dismissing the Google Play
     * services error dialog.
     */
    private static final int ERROR_DIALOG_DISMISS_WAIT_TIME = 2500;
    
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
            solo.sleep(ERROR_DIALOG_DISMISS_WAIT_TIME);
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

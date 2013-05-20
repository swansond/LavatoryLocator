package edu.washington.cs.lavatorylocator.test;

import org.junit.Test;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.gms.common.ConnectionResult;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.activity.MainActivity;
import edu.washington.cs.lavatorylocator.location.LocationServiceErrorMessages;

/**
 * Tests the error messages.
 * @author David Swanson
 *
 */
public class ErrorMessageTest extends 
        ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity a;
    private Context context;
    
    /**
     * Constructs a new ErrorMessageTest.
     */
    public ErrorMessageTest() {
        super(null);
        a = getActivity();
        context = a;
    }

    /**
     * Tests the developer error.
     */
    @Test
    public void testDeveloper() {
        assertTrue(testMessage(ConnectionResult.DEVELOPER_ERROR, 
                context.getString(R.string.connection_error_misconfigured)));
    }
    
    /**
     * Tests the internal connection error.
     */
    @Test
    public void testInternal() {
        assertTrue(testMessage(ConnectionResult.INTERNAL_ERROR, 
                context.getString(R.string.connection_error_internal)));
    }
    
    /**
     * Tests for the invalid account error.
     */
    @Test
    public void testAccount() {
        assertTrue(testMessage(ConnectionResult.INVALID_ACCOUNT, 
                context.getString(R.string.connection_error_invalid_account)));
    }
    
    /**
     * Tests the license check error.
     */
    @Test
    public void testLicense() {
        assertTrue(testMessage(
                ConnectionResult.LICENSE_CHECK_FAILED, 
                context.getString(
                        R.string.connection_error_license_check_failed)));
    }
    
    /**
     * Tests the network connection error.
     */
    @Test
    public void testNetwork() {
        assertTrue(testMessage(ConnectionResult.NETWORK_ERROR, 
                context.getString(R.string.connection_error_network)));
    }
    
    /**
     * Tests the connection error resolution request error.
     */
    @Test
    public void testResolution() {
        assertTrue(testMessage(ConnectionResult.RESOLUTION_REQUIRED, 
                context.getString(R.string.connection_error_needs_resolution)));
    }
    
    /**
     * Tests the service disabled error.
     */
    @Test
    public void testDisabled() {
        assertTrue(testMessage(ConnectionResult.SERVICE_DISABLED, 
                context.getString(R.string.connection_error_disabled)));
    }
    
    /**
     * Tests the invalid connection error.
     */
    @Test
    public void testInvalid() {
        assertTrue(testMessage(ConnectionResult.SERVICE_INVALID, 
                context.getString(R.string.connection_error_invalid)));
    }
    
    /**
     * Tests the missing service error.
     */
    @Test
    public void testMissing() {
        assertTrue(testMessage(ConnectionResult.SERVICE_MISSING, 
                context.getString(R.string.connection_error_missing)));
    }
    
    /**
     * Tests the update required error.
     */
    @Test
    public void testUpdate() {
        assertTrue(testMessage(
                ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, 
                context.getString(R.string.connection_error_outdated)));
    }
    
    /**
     * Tests the not signed in error.
     */
    @Test
    public void testSignin() {
        assertTrue(testMessage(ConnectionResult.SIGN_IN_REQUIRED, 
                context.getString(R.string.connection_error_sign_in_required)));
    }
    
    /**
     * Helper method for testing an error.
     * @param errorCode The error code to test
     * @param result The expected String.
     * @return true if the message matches, false otherwise.
     */
    public boolean testMessage(int errorCode, String result) {
        return result.equals(LocationServiceErrorMessages.getErrorString(
                context, errorCode));
    }

}

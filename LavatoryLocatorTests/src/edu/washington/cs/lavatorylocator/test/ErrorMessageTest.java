package edu.washington.cs.lavatorylocator.test;

import org.junit.Before;
import org.junit.Test;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.gms.common.ConnectionResult;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.activity.MainActivity;
import edu.washington.cs.lavatorylocator.location.LocationServiceErrorMessages;

public class ErrorMessageTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity a;
    Context context;
    
    public ErrorMessageTest() {
        super(null);
        a = getActivity();
        context = a;
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testDeveloper() {
        assertTrue(testMessage(ConnectionResult.DEVELOPER_ERROR, 
                context.getString(R.string.connection_error_misconfigured)));
    }
    
    @Test
    public void testInternal() {
        assertTrue(testMessage(ConnectionResult.INTERNAL_ERROR, 
                context.getString(R.string.connection_error_internal)));
    }
    
    @Test
    public void testAccount() {
        assertTrue(testMessage(ConnectionResult.INVALID_ACCOUNT, 
                context.getString(R.string.connection_error_invalid_account)));
    }
    
    @Test
    public void testLicense() {
        assertTrue(testMessage(ConnectionResult.LICENSE_CHECK_FAILED, 
                context.getString(R.string.connection_error_license_check_failed)));
    }
    
    @Test
    public void testNetwork() {
        assertTrue(testMessage(ConnectionResult.NETWORK_ERROR, 
                context.getString(R.string.connection_error_network)));
    }
    
    @Test
    public void testResolution() {
        assertTrue(testMessage(ConnectionResult.RESOLUTION_REQUIRED, 
                context.getString(R.string.connection_error_needs_resolution)));
    }
    
    @Test
    public void testDisabled() {
        assertTrue(testMessage(ConnectionResult.SERVICE_DISABLED, 
                context.getString(R.string.connection_error_disabled)));
    }
    
    @Test
    public void testInvalid() {
        assertTrue(testMessage(ConnectionResult.SERVICE_INVALID, 
                context.getString(R.string.connection_error_invalid)));
    }
    
    @Test
    public void testMissing() {
        assertTrue(testMessage(ConnectionResult.SERVICE_MISSING, 
                context.getString(R.string.connection_error_missing)));
    }
    
    @Test
    public void testUpdate() {
        assertTrue(testMessage(ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, 
                context.getString(R.string.connection_error_outdated)));
    }
    
    @Test
    public void testSignin() {
        assertTrue(testMessage(ConnectionResult.SIGN_IN_REQUIRED, 
                context.getString(R.string.connection_error_sign_in_required)));
    }
    
    public boolean testMessage(int errorCode, String result) {
        return result.equals(LocationServiceErrorMessages.getErrorString(context, errorCode));
    }

}

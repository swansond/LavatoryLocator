package edu.washington.cs.lavatorylocator.activity;

import org.springframework.http.ResponseEntity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.
        ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.
        OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.
        springandroid.SpringAndroidSpiceRequest;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.activity.libraryabstract.
    JacksonSpringSpiceSherlockFragmentActivity;
import edu.washington.cs.lavatorylocator.googleplus.PlusClientFragment;
import edu.washington.cs.lavatorylocator.googleplus.PlusClientFragment.
        OnSignedInListener;
import edu.washington.cs.lavatorylocator.location.LocationUtils;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.LavatoryMapMarkerOptionsFactory;
import edu.washington.cs.lavatorylocator.network.AddLavatoryRequest;
import edu.washington.cs.lavatorylocator.network.EditLavatoryDetailRequest;

/**
 * {@link android.app.Activity} for adding a new lavatory into the
 * LavatoryLocator service.
 *
 * @author Chris Rovillos
 * @author Wil Sunseri
 *
 */
public class EditLavatoryDetailActivity extends
        JacksonSpringSpiceSherlockFragmentActivity implements
        OnSignedInListener, ConnectionCallbacks, OnConnectionFailedListener,
        LocationListener, OnMapClickListener {
    // -------------------------------------------------------------------
    // CONSTANTS
    // -------------------------------------------------------------------
    private static final String TAG = "EditLavatoryDetailActivity";
    
    private static final int REQUEST_CODE_PLUS_CLIENT_FRAGMENT = 0;
    
    /**
     * Key for caching the result from a edit lavatory detail request.
     */
    private static final String EDIT_LAVATORY_DETAIL_CACHE_KEY =
            "editLavatoryDetailJson";

    /**
     * Cache duration, in milliseconds.
     */
    private static final long JSON_CACHE_DURATION =
            DurationInMillis.ALWAYS_EXPIRED;
    
    // -------------------------------------------------------------------
    // INSTANCE VARIABLES
    // -------------------------------------------------------------------
    // MAPPING AND LOCATION
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;
    
    private Marker mLavatoryMarker;
    
    private PlusClientFragment mPlusClientFragment;
    
    private String uid;

    // -------------------------------------------------------------------
    // ACTIVITY LIFECYCLE
    // -------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lavatory);
        
        setUpLocationRequest();
        
        mPlusClientFragment = PlusClientFragment.getPlusClientFragment(this,
                null);

        final LavatoryData lavatoryData = getIntent().getParcelableExtra(
                LavatoryDetailActivity.LAVATORY_DATA);
        if (lavatoryData != null) {
            setTitle(R.string.title_edit_lavatory_detail);

            final String building = lavatoryData.getBuilding();
            final String floor = lavatoryData.getFloor();
            final String room = lavatoryData.getRoom();
            final char type = lavatoryData.getType();

            ((EditText) findViewById(R.id.activity_add_lavatory_building_name))
                    .setText(building);

            ((EditText) findViewById(R.id.activity_add_lavatory_floor))
                    .setText(floor);
            ((EditText) findViewById(R.id.activity_add_lavatory_room))
                    .setText(room);

            if (type == 'M') {
                ((RadioButton) findViewById(R.id.activity_add_lavatory_male))
                        .setChecked(true);
            } else {
                ((RadioButton) findViewById(R.id.activity_add_lavatory_female))
                        .setChecked(true);
            }
        } else {
            setTitle(R.string.title_activity_add_lavatory);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        
        mPlusClientFragment.signIn(REQUEST_CODE_PLUS_CLIENT_FRAGMENT);
        
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu called");

        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.edit_lavatory, menu);
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (mPlusClientFragment.handleOnActivityResult(requestCode, resultCode,
                data)) {
            switch (resultCode) {
            case RESULT_CANCELED:
                // User canceled sign in.
                Toast.makeText(this, R.string.google_sign_in_required,
                        Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                break;
            }
        }
    }

    // ----------------------------------------------------------------------
    // VIEW EVENT HANDLERS
    // ----------------------------------------------------------------------
    /**
     * Starts submitting the new lavatory to the LavatoryLocator service.
     *
     * @param item
     *            the {@link MenuItem} that was selected
     */
    @SuppressWarnings("rawtypes")
    public void submit(MenuItem item) {
        Log.d(TAG, "submit called");

        if (uid != null) {
            getSherlock().setProgressBarIndeterminateVisibility(true);
    
            final String building = ((EditText) findViewById(
                    R.id.activity_add_lavatory_building_name))
                    .getText().toString();
            final String floor = ((EditText) findViewById(
                    R.id.activity_add_lavatory_floor))
                    .getText().toString();
            final String room = ((EditText) findViewById(
                    R.id.activity_add_lavatory_room))
                    .getText().toString();
            final int typeRadioButtonSelected = ((RadioGroup) findViewById(
                    R.id.activity_add_lavatory_type))
                    .getCheckedRadioButtonId();
    
            char type;
            if (typeRadioButtonSelected == R.id.activity_add_lavatory_male) {
                type = 'M';
            } else {
                type = 'F';
            }
    
            if (!TextUtils.isEmpty(building) && !TextUtils.isEmpty(floor)
                    && !TextUtils.isEmpty(room) && (mLavatoryMarker != null)) {
                final LatLng lavatoryMarkerPosition = mLavatoryMarker.
                        getPosition();
                
                final double latitude = lavatoryMarkerPosition.latitude;
                final double longitude = lavatoryMarkerPosition.longitude;
                
                final Intent intent = getIntent();
    
                final LavatoryData lavatoryToEdit = intent
                        .getParcelableExtra(LavatoryDetailActivity.
                                LAVATORY_DATA);
    
                SpringAndroidSpiceRequest<ResponseEntity> request;
    
                if (lavatoryToEdit != null) {
                    final int lid = lavatoryToEdit.getLid();
    
                    request = new EditLavatoryDetailRequest(uid, lid, building,
                            floor, room, type, latitude, longitude);
                } else { // add a new lavatory
                    request = new AddLavatoryRequest(uid, building, floor, room,
                            type, latitude, longitude);
                }
    
                Log.d(TAG, "submit: executing Add or Edit LavatoryRequest");
                getSpiceManager().execute(request,
                        EDIT_LAVATORY_DETAIL_CACHE_KEY,
                        JSON_CACHE_DURATION, 
                        new AddOrEditLavatoryDetailRequestListener());
            } else { // some fields not filled
                Toast.makeText(this, R.string.
                        activity_edit_lavatory_detail_fields_not_filled,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * Discards changes.
     *
     * @param item
     *            the {@link MenuItem} that was selected
     */
    public void discard(MenuItem item) {
        finish();
    }
    
    /**
     * Called when the {@link com.google.android.gms.plus.PlusClient} has been
     * connected successfully.
     *
     * @param plusClient
     *                The connected {@link PlusClient} for making API requests.
     */
    @Override
    public void onSignedIn(PlusClient plusClient) {
        final Person user = plusClient.getCurrentPerson();
        uid = user.getId();
    }
    
    /**
     * Signs the user out.
     * 
     * @param item
     *            the {@link MenuItem} that was selected
     */
    public void signOut(MenuItem item) {
        mPlusClientFragment.signOut();

        // go back to MainActivity
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // -------------------------------------------------------------
    // MAPPING AND LOCATION METHODS
    // -------------------------------------------------------------
    /**
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onDisconnected() {
        Log.e(TAG, "Location client disconnected");
    }

    /**
     * Called by Location Services if the attempt to Location Services fails.
     *
     * @param connectionResult
     *            the connection result
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed called");

        // Google Play services can resolve some errors it detects. If the error
        // has a resolution, try sending an Intent to start a Google Play
        // services activity that can resolve the error.
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                // thrown if Google Play services canceled the original
                // PendingIntent
                Log.e(TAG, e.getMessage());
            }
        } else {
            // if no resolution is available, display a dialog to the user with
            // the error
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    /**
     * Called when the user's location changes.
     *
     * @param location
     *            the updated location
     */
    @Override
    public void onLocationChanged(Location location) {
        // Nothing to do.
    }

    /**
     * Called by Location Services when the request to connect the client
     * finishes successfully.
     *
     * Callback called when connected to GCore. Implementation of
     * {@link ConnectionCallbacks}.
     *
     * @param connectionHint
     *            a {@link Bundle} with information about the connection
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                mLocationRequest, this); // LocationListener
        
        final LavatoryData lavatoryData = getIntent().getParcelableExtra(
                LavatoryDetailActivity.LAVATORY_DATA);
        if (lavatoryData != null) {
            final MarkerOptions lavatoryMarkerOptions =
                    LavatoryMapMarkerOptionsFactory
                    .createLavatoryMapMarkerOptions(lavatoryData);
            lavatoryMarkerOptions.draggable(true);
            mLavatoryMarker = mMap.addMarker(lavatoryMarkerOptions);
            
            // center map on marker's location
            final LatLng markerPosition = lavatoryMarkerOptions.getPosition();
            final CameraUpdate cameraUpdateToMarkerLocation =
                    CameraUpdateFactory.newLatLng(markerPosition);
            mMap.animateCamera(cameraUpdateToMarkerLocation);
        }
    }
    
    @Override
    public void onMapClick(LatLng point) {
        // hide the soft keyboard if showing
        final InputMethodManager inputManager = (InputMethodManager)            
                this.getSystemService(Context.INPUT_METHOD_SERVICE); 
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().
                getWindowToken(),      
                InputMethodManager.HIDE_NOT_ALWAYS);
        
        if (mLavatoryMarker == null) { // don't add a marker if one exists
            final MarkerOptions lavatoryMarkerOptions = new MarkerOptions().
                    position(point);
            lavatoryMarkerOptions.draggable(true);
            mLavatoryMarker = mMap.addMarker(lavatoryMarkerOptions);
        }
    }

    /**
     * Sets up the global {@code LocationClient} with the appropriate
     * parameters.
     */
    private void setUpLocationClientIfNeeded() {
        Log.d(TAG, "setUpLocationClientIfNeeded called");

        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
                    this, // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }

    /**
     * Sets up the global {@code LocationRequest} with the appropriate
     * parameters.
     */
    private void setUpLocationRequest() {
        // create a new global location parameters object
        mLocationRequest = LocationRequest.create();

        // set the update interval for the location request
        mLocationRequest
        .setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(
                LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play
     * services APK is correctly installed) and the map has not already been
     * instantiated.. This will ensure that we only ever call
     * {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt
     * for the user to install/update the Google Play services APK on their
     * device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and
     * correctly installing/updating/enabling the Google Play services. Since
     * the FragmentActivity may not have been completely destroyed during this
     * process (it is likely that it would only be stopped or paused),
     * {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        Log.d(TAG, "setUpMapIfNeeded called");

        // do a null check to confirm that we have not already instantiated the
        // map
        if (mMap == null) {
            Log.d(TAG, "setUpMapIfNeeded: attempting to obtain the map "
                    + "from the fragment");
            // try to obtain the map from the SupportMapFragment
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();

            // check if we were successful in obtaining the map
            if (mMap != null) {
                Log.d(TAG, "setUpMapIfNeeded: Successfully obtained the map");
                mMap.setMyLocationEnabled(true);

                mMap.setOnMapClickListener(this);
            } else {
                Log.d(TAG, "setUpMapIfNeeded: Unable to obtain the map");
            }
        } else {
            Log.d(TAG, "setUpMapIfNeeded: Map setup was not needed");
        }
    }

    /**
     * Show a dialog returned by Google Play services for the provided
     * connection error code.
     *
     * @param errorCode
     *            an error code returned from {@code onConnectionFailed}
     */
    private void showErrorDialog(int errorCode) {
        Log.d(TAG, "showErrorDialog called");

        // Get the error dialog from Google Play services
        final Dialog errorDialog = GooglePlayServicesUtil.
                getErrorDialog(errorCode, this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            final ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(),
                    LocationUtils.APPTAG);
        }
    }
    
    // ------------------------------------------------------------------
    // PRIVATE INNER CLASSES
    // ------------------------------------------------------------------
    /**
     * {@code RequestListener} for result of submitting the lavatory to the
     * LavatoryLocator service.
     *
     * @author Chris Rovillos
     *
     */
    @SuppressWarnings("rawtypes")
    private class AddOrEditLavatoryDetailRequestListener implements
            RequestListener<ResponseEntity> {
        private static final String TAG = "AddOrEditLavatory"
                + "DetailRequestListener";
        
        @Override
        public void onRequestSuccess(ResponseEntity responseEntity) {
            final String logSuccessMessage = "Review submission succeeded";
            Log.d(TAG, logSuccessMessage);

            EditLavatoryDetailActivity.this.getSherlock().
                    setProgressBarIndeterminateVisibility(false);

            Toast.makeText(EditLavatoryDetailActivity.this,
                    R.string.activity_edit_lavatory_detail_submission_success,
                    Toast.LENGTH_SHORT).show();

            finish();
        }
        
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            final String logErrorMessage = "Edit lavatory detail submission "
                    + "failed: " + spiceException.getMessage();
            Log.e(TAG, logErrorMessage);

            Toast.makeText(EditLavatoryDetailActivity.this,
                    R.string.activity_edit_lavatory_detail_submission_error,
                    Toast.LENGTH_LONG).show();
            EditLavatoryDetailActivity.this.getSherlock().
                    setProgressBarIndeterminateVisibility(false);
        }
    }

    /**
     * {@link DialogFragment} to display the error dialog generated in
     * {@link showErrorDialog}.
     */
    private static class ErrorDialogFragment extends SherlockDialogFragment {
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null.
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display.
         *
         * @param dialog
         *            the dialog to display
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    
}

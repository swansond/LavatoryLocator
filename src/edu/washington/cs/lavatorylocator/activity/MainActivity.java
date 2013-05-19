package edu.washington.cs.lavatorylocator.activity;

import static junit.framework.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.adapter.LavatorySearchResultsAdapter;
import edu.washington.cs.lavatorylocator.location.LocationUtils;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.LavatoryMapMarkerOptionsFactory;
import edu.washington.cs.lavatorylocator.model.LavatorySearchResults;
import edu.washington.cs.lavatorylocator.network.LavatorySearchRequest;

/**
 * {@code Activity} first displayed when LavatoryLocator is opened. Shows a map
 * and list of nearby lavatories.
 * 
 * @author Keith Miller
 * @author Chris Rovillos
 * 
 */
public class MainActivity extends JacksonSpringSpiceSherlockFragmentActivity
        implements ConnectionCallbacks, OnConnectionFailedListener,
        LocationListener, OnInfoWindowClickListener {

    // --------------------------------------------------------------------------------------------
    // CONSTANTS
    // --------------------------------------------------------------------------------------------
    public static final String LAVATORY_DATA = "lavatoryData";

    /**
     * The search radius for the Got2Go feature, in meters.
     */
    private static final int GOT2GO_SEARCH_RADIUS = 100;

    private static final String JSON_CACHE_KEY = "lavatorySearchResultsJson";
    private static final long JSON_CACHE_DURATION = DurationInMillis.ONE_MINUTE;

    private static final int SEARCH_ACTIVITY_RESULT = 0;

    // --------------------------------------------------------------------------------------------
    // INSTANCE VARIABLES
    // --------------------------------------------------------------------------------------------
    private PopupWindow popup;

    // Mapping and location
    private GoogleMap mMap;
    private LocationRequest mLocationRequest; // request to connect to Location
                                              // Services
    private LocationClient mLocationClient; // current instantiation of the
                                            // location client

    private LavatorySearchResultsAdapter lavatorySearchResultsAdapter;

    private HashMap<Marker, LavatoryData> markerLavatoryDataMap;

    // --------------------------------------------------------------------------------------------
    // ACTIVITY LIFECYCLE
    // --------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // prepare for the indeterminate progress indicator in the action bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);

        // the content view must be set before the following methods, as they
        // access the items within the view
        setContentView(R.layout.activity_main);

        setUpLocationRequest();

        markerLavatoryDataMap = new HashMap<Marker, LavatoryData>();
        setUpMapIfNeeded();

        setUpLavatorySearchResultsListView();
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent i = getIntent();
        Bundle searchResultsBundle = i
                .getParcelableExtra(SearchActivity.SEARCH_RESULTS);

        if (searchResultsBundle == null) { // the user hasn't requested a search
            loadAllLavatories();
        } else {
            // TODO: do the searching network request from within this activity;
            // right now there's duplicate request code
            List<Parcelable> searchResultsListParcelable = Arrays
                    .asList(searchResultsBundle.getParcelableArray("results"));

            if (!searchResultsListParcelable.isEmpty()) {
                // cast Parcelable to LavatoryData
                List<LavatoryData> searchResults = new ArrayList<LavatoryData>();
                for (Parcelable searchResultParcelable : searchResultsListParcelable) {
                    searchResults.add((LavatoryData) searchResultParcelable);
                }
                updateLavatorySearchResults(searchResults);
            } else {
                Toast.makeText(this, "No results found.", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }

    // --------------------------------------------------------------------------------------------
    // EVENT HANDLERS
    // --------------------------------------------------------------------------------------------
    /**
     * Activates the "Got2Go" feature, showing the user the nearest highly-rated
     * lavatory.
     * 
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void activateGot2go(MenuItem item) {
        Location currentLocation = mLocationClient.getLastLocation();
        if (currentLocation != null) {
            double currentLatitude = currentLocation.getLatitude();
            double currentLongitude = currentLocation.getLongitude();
            searchForLavatories(currentLatitude, currentLongitude,
                    GOT2GO_SEARCH_RADIUS);
        } else {
            String message = "Location information is currently not available. Got2Go requires location information to show you the nearest lavatory.";
            Toast.makeText(this, message, Toast.LENGTH_LONG);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getSupportMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        // Calling super after populating the menu is necessary here to ensure
        // that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Goes to the <code>AboutActivity</code>.
     * 
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void goToAboutActivity(MenuItem item) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Goes to the <code>AddLavatoryActivity</code> to allow the user to request
     * to add a lavatory to the LavatoryLocator service.
     * 
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void goToAddLavatoryActivity(MenuItem item) {
        SharedPreferences userDetails = getApplicationContext()
                .getSharedPreferences("User", MODE_PRIVATE);
        Boolean loggedIn = userDetails.getBoolean("isLoggedIn", false);

        if (!loggedIn) {
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.login_popup,
                    (ViewGroup) findViewById(R.id.login_popup_layout));

            popup = new PopupWindow(layout, 350, 250, true);
            popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        } else {
            Intent intent = new Intent(this, AddLavatoryActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    /**
     * Logs the user in so that they can add missing lavatories
     * 
     * @param target
     */
    public void loginUser(View target) {
        SharedPreferences userDetails = getApplicationContext()
                .getSharedPreferences("User", MODE_PRIVATE);
        userDetails.edit().putBoolean("isLoggedIn", true).commit();
        dismissLoginPrompt(target);
    }

    /**
     * Closes the popup window
     * 
     * @param target
     */
    public void dismissLoginPrompt(View target) {
        popup.dismiss();
    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
        // TODO: show button for user to reconnecy
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects. If the error
         * has a resolution, try sending an Intent to start a Google Play
         * services activity that can resolve error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with
            // the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    /**
     * Called when the user's location changes.
     * 
     * @param location
     *            The updated location.
     */
    @Override
    public void onLocationChanged(Location location) {
        // Nothing to do.
    }

    /**
     * Called by Location Services when the request to connect the client
     * finishes successfully. At this point, you can request the current
     * location or start periodic updates
     * 
     * Callback called when connected to GCore. Implementation of
     * {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(mLocationRequest, this); // LocationListener

        centerMapOnCurrentLocation();
    }

    /**
     * Shows the search action view.
     * 
     * @param view
     *            the <code>MenuItem</code> that was selected
     */
    public void goToSearchActivity(MenuItem item) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, SEARCH_ACTIVITY_RESULT);
    }

    @Override
    public void onInfoWindowClick(Marker m) {
        LavatoryData ld = markerLavatoryDataMap.get(m);
        assertNotNull(ld);

        showLavatoryDetail(ld);
    }

    // --------------------------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    // --------------------------------------------------------------------------------------------
    private void loadAllLavatories() {
        setProgressBarIndeterminateVisibility(true);

        getSpiceManager().execute(new LavatorySearchRequest(), JSON_CACHE_KEY,
                JSON_CACHE_DURATION, new LavatorySearchListener());
    }

    private void searchForLavatories(double latitude, double longitude,
            double radius) {
        setProgressBarIndeterminateVisibility(true);

        getSpiceManager().execute(
                new LavatorySearchRequest(latitude, longitude, radius),
                JSON_CACHE_KEY, JSON_CACHE_DURATION,
                new LavatorySearchListener());
    }

    private void setUpLavatorySearchResultsListView() {
        lavatorySearchResultsAdapter = new LavatorySearchResultsAdapter(this,
                R.layout.search_result_item,
                R.id.search_result_item_lavatory_name);

        ListView searchResultsListView = (ListView) findViewById(R.id.activity_main_search_results);

        searchResultsListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                        final LavatoryData selectedLavatory = (LavatoryData) parent
                                .getItemAtPosition(position);
                        Intent intent = new Intent(parent.getContext(),
                                LavatoryDetailActivity.class);
                        intent.putExtra(LAVATORY_DATA, selectedLavatory);
                        startActivity(intent);
                    }
                });

        searchResultsListView.setAdapter(lavatorySearchResultsAdapter);
    }

    private void setUpLocationRequest() {
        // create a new global location parameters object
        mLocationRequest = LocationRequest.create();

        // set the update interval for the location request
        mLocationRequest
                .setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest
                .setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
    }

    private void updateLavatorySearchResults(List<LavatoryData> searchResults) {
        assertNotNull(searchResults);

        addLavatoriesToMap(searchResults);
        addLavatoriesToList(searchResults);
    }

    private void addLavatoriesToMap(List<LavatoryData> lavatories) {
        if (mMap != null) { // the map can be null (for example, when running in
                            // the emulator on or a phone without Google Play
                            // services)
            for (LavatoryData lavatoryData : lavatories) {
                MarkerOptions lavatoryMarkerOptions = LavatoryMapMarkerOptionsFactory
                        .createLavatoryMapMarkerOptions(lavatoryData);
                Marker marker = mMap.addMarker(lavatoryMarkerOptions);
                markerLavatoryDataMap.put(marker, lavatoryData);
            }
        }
    }

    private void addLavatoriesToList(List<LavatoryData> lavatories) {
        lavatorySearchResultsAdapter.addAll(lavatories);
        lavatorySearchResultsAdapter.notifyDataSetChanged();
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
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment test = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map));
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);

                mMap.setOnInfoWindowClickListener(this);
            }
        }
    }

    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(getApplicationContext(), this, // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }

    /**
     * Centers and animates the map on the user's current location.
     */
    private void centerMapOnCurrentLocation() {
        Location currentLocation = mLocationClient.getLastLocation();
        if (currentLocation != null) {
            double currentLatitude = currentLocation.getLatitude();
            double currentLongitude = currentLocation.getLongitude();

            LatLng currentLatLng = new LatLng(currentLatitude, currentLongitude);

            CameraUpdate cameraUpdateToCurrentLocation = CameraUpdateFactory
                    .newLatLng(currentLatLng);

            mMap.animateCamera(cameraUpdateToCurrentLocation);
        } else {
            String message = "Your current location could not be found. Try again later.";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Show a dialog returned by Google Play services for the connection error
     * code
     * 
     * @param errorCode
     *            An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
                this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(),
                    LocationUtils.APPTAG);
        }
    }

    private void showLavatoryDetail(LavatoryData lavatoryData) {
        Intent intent = new Intent(this, LavatoryDetailActivity.class);
        intent.putExtra(LAVATORY_DATA, lavatoryData);
        startActivity(intent);
    }

    // --------------------------------------------------------------------------------------------
    // PRIVATE INNER CLASSES
    // --------------------------------------------------------------------------------------------
    private class LavatorySearchListener implements
            RequestListener<LavatorySearchResults> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            String errorMessage = "Lavatory search request failed: "
                    + spiceException.getMessage();

            Log.e(getLocalClassName(), errorMessage);
            Toast.makeText(getApplicationContext(), errorMessage,
                    Toast.LENGTH_LONG).show();
            MainActivity.this.setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void onRequestSuccess(LavatorySearchResults lavatorySearchResults) {
            MainActivity.this.updateLavatorySearchResults(lavatorySearchResults
                    .getLavatories());

            MainActivity.this.setProgressBarIndeterminateVisibility(false);
        }
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    private static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         * 
         * @param dialog
         *            An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

}

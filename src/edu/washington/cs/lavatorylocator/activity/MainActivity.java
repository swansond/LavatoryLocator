package edu.washington.cs.lavatorylocator.activity;

import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.common
    .GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common
    .GooglePlayServicesClient.OnConnectionFailedListener;
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
import edu.washington.cs.lavatorylocator.activity.libraryabstract.JacksonSpringSpiceSherlockFragmentActivity;
import edu.washington.cs.lavatorylocator.adapter.LavatorySearchResultsAdapter;
import edu.washington.cs.lavatorylocator.location.LocationUtils;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.LavatoryMapMarkerOptionsFactory;
import edu.washington.cs.lavatorylocator.model.LavatorySearchResults;
import edu.washington.cs.lavatorylocator.network.Got2goRequest;
import edu.washington.cs.lavatorylocator.network.LavatorySearchRequest;

/**
 * {@link android.app.Activity} first displayed when LavatoryLocator is opened.
 * Shows a map and list of nearby lavatories.
 *
 * @author Chris Rovillos
 * @author Keith Miller
 *
 */
public class MainActivity extends JacksonSpringSpiceSherlockFragmentActivity
    implements ConnectionCallbacks, OnConnectionFailedListener,
    LocationListener, OnInfoWindowClickListener {

    // --------------------------------------------------------------
    // CONSTANTS
    // --------------------------------------------------------------
    /**
     * Intent identifier for sending {@link LavatoryData} objects to
     * {@link LavatoryDetailActivity}.
     */
    public static final String LAVATORY_DATA = "lavatoryData";

    /**
     * Key for caching lavatory search results.
     */
    private static final String SEARCH_RESULTS_JSON_CACHE_KEY =
            "lavatorySearchResultsJson";

    /**
     * Key for caching the result from a Got2Go request.
     */
    private static final String GOT2GO_JSON_CACHE_KEY = "got2goResultJson";

    /**
     * Cache duration, in milliseconds.
     */
    private static final long JSON_CACHE_DURATION =
            DurationInMillis.ALWAYS_EXPIRED;

    /**
     * Request identifier for the {@link Intent} sent to {@link SearchActivity}.
     */
    private static final int SEARCH_PARAMETERS_REQUEST = 0;

    private static final String TAG = "MainActivity";

    // -----------------------------------------------------------
    // INSTANCE VARIABLES
    // -----------------------------------------------------------

    // MAPPING AND LOCATION
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;

    private LavatorySearchResultsAdapter lavatorySearchResultsAdapter;

    /**
     * Map of {@code Marker}s to {@link LavatoryData} objects. Needed to send
     * the {@link LavatoryData} objects to the {@link LavatoryDetailActivity}
     * when the user taps on a map marker info window.
     */
    private HashMap<Marker, LavatoryData> markerLavatoryDataMap;

    // ------------------------------------------------------------
    // ACTIVITY LIFECYCLE
    // ------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");

        super.onCreate(savedInstanceState);

        // prepare for the indeterminate progress indicator in the action bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getSherlock().setProgressBarIndeterminateVisibility(false);

        // the content view must be set before the following methods, as they
        // access the items within the view
        setContentView(R.layout.activity_main);

        setUpLocationRequest();

        markerLavatoryDataMap = new HashMap<Marker, LavatoryData>();

        setUpLavatorySearchResultsListView();

        loadAllLavatories();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart called");

        super.onStart();

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume called");

        super.onResume();

        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop called");
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause called");

        super.onPause();

        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu called");

        // Inflate the menu; this adds items to the action bar if it is present.
        final MenuInflater menuInflater = getSupportMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        // Calling super after populating the menu is necessary here to ensure
        // that the action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult called");

        switch (requestCode) { // in case there are more activity requests in
        // the future
        case SEARCH_PARAMETERS_REQUEST:
            if (resultCode == RESULT_OK) {
                final String building = data
                        .getStringExtra(SearchActivity.BUILDING_PARAMETER);
                final String floor = data
                        .getStringExtra(SearchActivity.FLOOR_PARAMETER);
                final String room = data
                        .getStringExtra(SearchActivity.ROOM_PARAMETER);
                final double minRating = data.getDoubleExtra(
                        SearchActivity.MIN_RATING_PARAMETER, 0.0);

                final String radius = data
                        .getStringExtra(SearchActivity.RADIUS_PARAMETER);
                final String latitude = data
                        .getStringExtra(SearchActivity.LATITUDE_PARAMETER);
                final String longitude = data
                        .getStringExtra(SearchActivity.LONGITUDE_PARAMETER);
                final String type = data
                        .getStringExtra(SearchActivity.TYPE_PARAMETER);

                searchForLavatories(building, floor, room, minRating, type,
                        latitude, longitude, radius);
            }

            break;
        default:
            break;
        }
    }

    // ---------------------------------------------------------------
    // VIEW EVENT HANDLERS
    // ---------------------------------------------------------------
    /**
     * Activates the Got2Go feature, showing the user the nearest highly-rated
     * lavatory.
     *
     * @param item
     *            the {@link MenuItem} that was selected
     */
    public void activateGot2go(MenuItem item) {
        Log.d(TAG, "activateGot2go called");

        if (mLocationClient.isConnected()
                && (mLocationClient.getLastLocation() != null)) {
            final Location currentLocation = mLocationClient.getLastLocation();
            final double currentLatitude = currentLocation.getLatitude();
            final double currentLongitude = currentLocation.getLongitude();

            Log.d(TAG, "activateGot2go: got2go request initiated with lat: "
                    + currentLatitude + "; long: " + currentLongitude);
            getSpiceManager().execute(
                    new Got2goRequest(currentLatitude, currentLongitude),
                    GOT2GO_JSON_CACHE_KEY, JSON_CACHE_DURATION,
                    new Got2goRequestListener());
        } else {
            Log.d(TAG, "activateGot2go: got2go request failed:"
                    + "location services unavailable");
            // TODO: move to the string resource XML file
            final String message = "Location information is currently "
                    + "not available. Got2Go requires location "
                    + "information to show you the nearest lavatory.";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Goes to the {@link AboutActivity}.
     *
     * @param item
     *            the {@link MenuItem} that was selected
     */
    public void goToAboutActivity(MenuItem item) {
        Log.d(TAG, "goToAboutActivity called with item: " + item.toString());
        final Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Goes to the {@link AddLavatoryActivity} to allow the user to request
     * to add a lavatory to the LavatoryLocator service.
     *
     * @param item
     *            the {@link MenuItem} that was selected
     */
    public void goToAddLavatoryActivity(MenuItem item) {
        Log.d(TAG, "goToAddLavatoryActivity called");

        final Intent intent = new Intent(
                this, EditLavatoryDetailActivity.class);
        startActivity(intent);
    }

    /**
     * Shows the search action view.
     *
     * @param item the {@link MenuItem} that was selected
     */
    public void goToSearchActivity(MenuItem item) {
        Log.d(TAG, "goToSearchActivity called");

        final Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, SEARCH_PARAMETERS_REQUEST);
    }

    @Override
    public void onInfoWindowClick(Marker m) {
        Log.d(TAG, "onInfoWindowClick called");

        final LavatoryData ld = markerLavatoryDataMap.get(m);
        assert (ld != null);

        showLavatoryDetail(ld);
    }

    // -------------------------------------------------------------
    // MAPPING AND LOCATION METHODS
    // -------------------------------------------------------------
    /**
     * Centers and animates the map on the user's current location.
     */
    private void centerMapOnCurrentLocation() {
        Log.d(TAG, "centerMapOnCurrentLocation called");

        final Location currentLocation = mLocationClient.getLastLocation();
        if (currentLocation != null) {
            final double currentLatitude = currentLocation.getLatitude();
            final double currentLongitude = currentLocation.getLongitude();

            final LatLng currentLatLng =
                    new LatLng(currentLatitude, currentLongitude);

            Log.d(TAG, "Centering map on location: "
                    + currentLatLng.toString());

            final CameraUpdate cameraUpdateToCurrentLocation =
                    CameraUpdateFactory.newLatLng(currentLatLng);

            mMap.animateCamera(cameraUpdateToCurrentLocation);
        } else {
            Log.d(TAG, "centerMapOnCurrentLocation: Unable to center the map, "
                    + "no location services");

            // TODO: move string to resources XML file
            final String message = "Your current location could not "
                    + "be found. Try again later.";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onDisconnected() {
        Log.d(TAG, "onDisconnected called");

        // TODO: move to string resource XML file
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
        // TODO: show button for user to reconnect
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
                Log.e(getLocalClassName(), e.getMessage());
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

        centerMapOnCurrentLocation();
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

                mMap.setOnInfoWindowClickListener(this);
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

    // ----------------------------------------------------
    // PRIVATE HELPER METHODS
    // ----------------------------------------------------
    /**
     * Displays the given list of lavatories in the map and list.
     *
     * @param lavatories
     *            the lavatories to display
     */
    private void displayLavatories(List<LavatoryData> lavatories) {
        Log.d(TAG, "displayLavatories called");

        assert (lavatories != null);

        if (lavatories.isEmpty()) {
            Log.d(TAG, "displayLavatories: No lavatories obtained from "
                    + "server response");
            final String message = "No lavatories found!";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            // TODO: move string to resources xml file
        }

        displayLavatoriesOnMap(lavatories);
        displayLavatoriesOnList(lavatories);
    }

    /**
     * Displays the given set of lavatories on the list.
     *
     * @param lavatories
     *            the lavatories to display on the list
     */
    private void displayLavatoriesOnList(List<LavatoryData> lavatories) {
        Log.d(TAG, "displayLavatoriesOnList called");

        lavatorySearchResultsAdapter.clear();
        lavatorySearchResultsAdapter.addAll(lavatories);
        lavatorySearchResultsAdapter.notifyDataSetChanged();
    }

    /**
     * Displays the given set of lavatories on the map.
     *
     * @param lavatories
     *            the lavatories to display on the map
     */
    private void displayLavatoriesOnMap(List<LavatoryData> lavatories) {
        Log.d(TAG, "displayLavatoriesOnMap called");

        if (mMap != null) { // the map can be null (for example, when running in
            // the emulator on or a phone without Google Play
            // services)
            mMap.clear();
            for (LavatoryData lavatoryData : lavatories) {
                final MarkerOptions lavatoryMarkerOptions =
                        LavatoryMapMarkerOptionsFactory
                        .createLavatoryMapMarkerOptions(lavatoryData);
                final Marker marker = mMap.addMarker(lavatoryMarkerOptions);
                markerLavatoryDataMap.put(marker, lavatoryData);
            }
        }
    }

    /**
     * Loads all the lavatories in the LavatoryLocator service and displays
     * them.
     */
    private void loadAllLavatories() {
        Log.d(TAG, "loadAllLavatories called");

        getSherlock().setProgressBarIndeterminateVisibility(true);

        Log.d(TAG, "loadAllLavatories: LavatorySearchRequest executing");
        getSpiceManager().execute(new LavatorySearchRequest(),
                SEARCH_RESULTS_JSON_CACHE_KEY, JSON_CACHE_DURATION,
                new LavatorySearchListener());
    }

    /**
     * Sends a search request to the LavatoryLocator service with the given
     * parameters.
     *
     * @param building
     *            the building the lavatory is in
     * @param floor
     *            the floor the lavatory is on
     * @param room
     *            the lavatory's room number
     * @param minRating
     *            the minimum rating of lavatories to be returned
     * @param type
     *            the type of lavatory
     * @param latitude
     *            the latitude of the search request point
     * @param longitude
     *            the longitude of the search request point
     * @param radius
     *            the radius of lavatories to return, as measured from the
     *            search request point
     */
    private void searchForLavatories(String building, String floor,
            String room, double minRating, String type, String latitude,
            String longitude, String radius) {
        Log.d(TAG, "searchForLavatories called");

        getSherlock().setProgressBarIndeterminateVisibility(true);

        final LavatorySearchRequest lavatorySearchRequest =
                new LavatorySearchRequest(
                        building, floor, room, minRating,
                        type, latitude, longitude, radius);

        Log.d(TAG, "searchForLavatories: LavatorySearchRequest executing...");
        getSpiceManager().execute(lavatorySearchRequest,
                SEARCH_RESULTS_JSON_CACHE_KEY, JSON_CACHE_DURATION,
                new LavatorySearchListener());
    }

    /**
     * Sets up the {@link ListView} and accompanying {@link Adapter} for
     * displaying lavatory search results.
     */
    private void setUpLavatorySearchResultsListView() {
        Log.d(TAG, "setUpLavatorySearchResultsListView called");

        lavatorySearchResultsAdapter = new LavatorySearchResultsAdapter(this,
                R.layout.search_result_item,
                R.id.search_result_item_lavatory_name);

        final ListView searchResultsListView =
                (ListView) findViewById(R.id.activity_main_search_results);

        searchResultsListView
        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                final LavatoryData selectedLavatory =
                        (LavatoryData) parent.
                        getItemAtPosition(position);
                showLavatoryDetail(selectedLavatory);
            }
        });

        searchResultsListView.setAdapter(lavatorySearchResultsAdapter);
    }

    /**
     * Goes to the {@link LavatoryDetailActivity} for the given
     * {@link LavatoryData}.
     *
     * @param lavatoryData
     *            the data to show in the {@link LavatoryDetailActivity}
     */
    private void showLavatoryDetail(LavatoryData lavatoryData) {
        Log.d(TAG, "showLavatoryDetail called");

        final Intent intent = new Intent(this, LavatoryDetailActivity.class);
        intent.putExtra(LAVATORY_DATA, lavatoryData);
        startActivity(intent);
    }

    // ----------------------------------------------------------------
    // PRIVATE INNER CLASSES
    // ----------------------------------------------------------------
    /**
     * {@code RequestListener} for search results from the LavatoryLocator
     * service.
     *
     * @author Chris Rovillos
     *
     */
    private class LavatorySearchListener implements
        RequestListener<LavatorySearchResults> {
        private static final String TAG = "LavatorySearchListener";

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.d(TAG, "onRequestFailure called");

            // TODO: move to string resources XML file
            final String errorMessage = "Lavatory search request failed: "
                    + spiceException.getMessage();

            Log.e(getLocalClassName(), errorMessage);
            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG)
                    .show();
            MainActivity.this.getSherlock()
                    .setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void onRequestSuccess(
                LavatorySearchResults lavatorySearchResults) {
            Log.d(TAG, "onRequestSuccess called");

            MainActivity.this.displayLavatories(lavatorySearchResults
                    .getLavatories());

            MainActivity.this.getSherlock()
                    .setProgressBarIndeterminateVisibility(false);
        }
    }

    /**
     * {@code RequestListener} for the Got2Go lavatory returned from the
     * LavatoryLocator service.
     *
     * @author Chris Rovillos
     *
     */
    private class Got2goRequestListener implements
        RequestListener<LavatoryData> {
        private static final String TAG = "Got2goRequestListener";

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.d(TAG, "onRequestFailure called");

            // TODO: move to string resources XML file
            final String errorMessage = "Got2Go request failed: "
                    + spiceException.getMessage();

            Log.e(getLocalClassName(), errorMessage);
            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG)
                .show();
            MainActivity.this.getSherlock()
                    .setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void onRequestSuccess(LavatoryData got2goLavatory) {
            Log.d(TAG, "onRequestSuccess called");

            MainActivity.this.getSherlock()
                    .setProgressBarIndeterminateVisibility(false);

            showLavatoryDetail(got2goLavatory);
        }
    }

    /**
     * {@link DialogFragment} to display the error dialog generated in
     * {@link showErrorDialog}.
     */
    private static class ErrorDialogFragment extends DialogFragment {
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

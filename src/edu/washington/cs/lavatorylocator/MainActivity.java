package edu.washington.cs.lavatorylocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;

import location.LocationUtils;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
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

/**
 * <code>Activity</code> first displayed when LavatoryLocator is opened. Shows a
 * list of nearby lavatories.
 *
 * @author Keith Miller, Chris Rovillos
 *
 */
public class MainActivity extends SherlockFragmentActivity
        implements ConnectionCallbacks, OnConnectionFailedListener,
        LocationListener, LoaderCallbacks<RESTLoader.RESTResponse>,
        OnInfoWindowClickListener {
    public static final String LAVATORY = "LAVATORY";
    private static final int MANAGER_ID = 0;
    private static final String LAVA_SEARCH
            = "http://lavlocdb.herokuapp.com/lavasearch.php";

    private ListView listView;
    private PopupWindow popup;
    private PopupWindow connectionPopup;
    private ProgressDialog loadingScreen;
    private boolean got2GoFlag;

    // Fields to store the previous search parameters in so we can repeat it
    private String lastBldg;
    private String lastFlr;
    private String lastRmNum;
    private String lastLocLong;
    private String lastLocLat;
    private String lastMaxDist;
    private String lastMinRating;
    private String lastLavaType;

    private GoogleMap mMap;

    // A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;

    // A WeakHashMap so that then a Marker gets garbage-collected,
    // so will its entry in the map
    private WeakHashMap<Marker, LavatoryData> markerLavatoryDataMap;

    /**
     * Activates the "Got2Go" feature, showing the user the nearest
     * highly-rated lavatory.
     *
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void activateGot2go(MenuItem item) {
        // TODO: implement; remove stub message
//        Context context = getApplicationContext();
//        CharSequence notImplementedMessage = "Got2Go is not implemented yet!";
//        int duration = Toast.LENGTH_SHORT;
//
//        Toast toast = Toast.makeText(context, notImplementedMessage, duration);
//        toast.show();
        got2GoFlag = true;
        lavatorySearch("CSE", "1", "", "-122.305599", "47.653305", "50", "4", "");
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
     * Goes to the <code>AddLavatoryActivity</code> to allow the user to request to add a
     * lavatory to the LavatoryLocator service.
     *
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void goToAddLavatoryActivity(MenuItem item) {
        SharedPreferences userDetails = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        Boolean loggedIn = userDetails.getBoolean("isLoggedIn", false);

        if(!loggedIn){
            LayoutInflater inflater = (LayoutInflater)
                    this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    public void loginUser(View target){
        SharedPreferences userDetails = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        userDetails.edit().putBoolean("isLoggedIn", true).commit();
        dismissLoginPrompt(target);
    }

    /**
     * Closes the popup window
     *
     * @param target
     */
    public void dismissLoginPrompt(View target){
        popup.dismiss();
    }

    /**
     * Goes to the <code>SettingsActivity</code>.
     *
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void goToSettingsActivity(MenuItem item) {
        // TODO: implement; remove stub message
        Context context = getApplicationContext();
        CharSequence notImplementedMessage = "Settings are not implemented yet!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, notImplementedMessage, duration);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setUpMapIfNeeded();

        // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();

        // Set the update interval
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        listView = (ListView) findViewById(R.id.activity_main_search_results);

        Intent i = getIntent();
        Bundle extra = i.getParcelableExtra(SearchActivity.SEARCH_RESULTS);

        if (extra == null) {
            lavatorySearch("CSE", "1", "", "-122.305599", "47.653305", "50", "", "");
        } else {
            List<Parcelable> results = Arrays.asList(extra.getParcelableArray("results"));
            List<LavatoryData> lavatories = new ArrayList<LavatoryData>();
            if (results.size() != 0) {
                for (Parcelable p : results) {
                    lavatories.add((LavatoryData) p);
                }
                populateSearchResults(lavatories);
            } else {
                Toast.makeText(this, "No results found.",
                        Toast.LENGTH_LONG).show();
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

    /**
     * Called when the Activity is no longer visible at all.
     * Stop updates and disconnect.
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Called when the Activity is going into the background.
     * Parts of the UI may be visible, but the Activity is inactive.
     */
    @Override
    public void onPause() {
        super.onPause();

        if (mLocationClient != null) {
            mLocationClient.disconnect();
          }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getSupportMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

     // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not have been
     * completely destroyed during this process (it is likely that it would only be stopped or
     * paused), {@link #onCreate(Bundle)} may not be called again so we should call this method in
     * {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);

                mMap.setOnInfoWindowClickListener(this);
            }
        }
    }

    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
          mLocationClient = new LocationClient(
              getApplicationContext(),
              this,  // ConnectionCallbacks
              this); // OnConnectionFailedListener
        }
      }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
        //TODO
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
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

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    /**
     * Called when the user's location changes.
     *
     * @param location The updated location.
     */
    @Override
    public void onLocationChanged(Location location) {
          // Nothing to do.
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     *
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
      mLocationClient.requestLocationUpdates(
          mLocationRequest,
          this);  // LocationListener

      centerMapOnCurrentLocation();
    }

    /**
     * Centers and animates the map on the user's current location.
     */
    private void centerMapOnCurrentLocation() {
       Location currentLocation = mLocationClient.getLastLocation();
       // TODO Figure out why currentLocation can be null sometimes.
       double currentLatitude = currentLocation.getLatitude();
       double currentLongitude = currentLocation.getLongitude();

       LatLng currentLatLng = new LatLng(currentLatitude, currentLongitude);

       CameraUpdate cameraUpdateToCurrentLocation = CameraUpdateFactory.newLatLng(currentLatLng);

       mMap.animateCamera(cameraUpdateToCurrentLocation);
    }

    /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
        }
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

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
         * @param dialog An error dialog
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


    /**
     * Shows the search action view.
     *
     * @param view
     *            the <code>MenuItem</code> that was selected
     */
    public void goToSearchActivity(MenuItem item) {
        // TODO: implement; remove stub message
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * Custom <code>Adapter</code> for displaying an array of
     * <code>LavatoryData</code>s. Creates a custom <code>View</code> for each
     * lavatory. row.
     *
     * @author Keith Miller
     *
     */
    private class SearchResultsAdapter extends ArrayAdapter<LavatoryData> {

        private final List<LavatoryData> searchResults;

        /**
         * Constructs a new <code>SearchResultsAdapter</code> with given
         * <code>List</code> of <code>LavatoryData</code>s.
         *
         * @param context
         *            the current context
         * @param resultRowResource
         *            the resource ID for a layout file containing the LavatoryData
         *            row layout to use when instantiating views
         * @param LavatoryDataNameTextViewResourceId
         *            the id of the <code>TextView</code> for displaying the
         *            LavatoryData's name in each row
         * @param reviews
         *            a <code>List</code> of <code>Review</code>s to display
         */
        public SearchResultsAdapter(Context context, int resultRowResource,
                int LavatoryDataNameTextViewResourceId, List<LavatoryData> searchResults) {
            super(context, resultRowResource, LavatoryDataNameTextViewResourceId,
                    searchResults);
            this.searchResults = searchResults;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.search_result_item, parent, false);
            }

            ((RatingBar) convertView.findViewById(R.id.search_result_item_average_review))
            		.setRating((float) searchResults.get(position)
                    		.avgRating);

            ((TextView) convertView.findViewById(R.id.search_result_item_lavatory_name))
            		.setText(searchResults.get(position).building + " "
                    		+ searchResults.get(position).lavatoryID + " "
                    		+ searchResults.get(position).lavatoryGender);

            ((TextView) convertView.findViewById(R.id.search_result_item_review_count))
            		.setText("" + searchResults.get(position).numReviews);

            ((TextView) convertView.findViewById(R.id.search_result_item_floor)).setText("Floor "
                    + searchResults.get(position).floor);

            return convertView;
        }
    }

    /**
     * Returns a new Loader to this activity's LoaderManager.
     * NOTE: We never need to call this directly as it is done automatically.
     *
     * @author Wilkes Sunseri
     *
     * @param id the id of the LoaderManager
     * @param args the Bundle of arguments to be passed to the Loader
     *
     * @return A Loader to search for lavatories
     */
    @Override
    public Loader<RESTLoader.RESTResponse> onCreateLoader(int id, Bundle args) {
        Uri searchAddress =
                Uri.parse(LAVA_SEARCH);
        return new RESTLoader(getApplicationContext(), searchAddress,
                RESTLoader.requestType.GET, args);
    }

    /**
     * Parses the response from the server if there is one and passes it off.
     * If the app could not connect to the server properly, the user will be
     * prompted to try again.
     *
     * This is called automatically when the load finishes.
     *
     * @author Wilkes Sunseri
     *
     * @param loader the Loader doing the loading
     * @param response the server response to be processed
     */
    @Override
    public void onLoadFinished(Loader<RESTLoader.RESTResponse> loader,
            RESTLoader.RESTResponse response) {
        loadingScreen.dismiss();
        if (response.getCode() == HttpStatus.SC_OK) {
            try {
                if (got2GoFlag) {
                    got2GoFlag = false;
                    JSONObject got2GoResults = Parse.readJSON(response);
                    LavatoryData topResult = Parse.lavatoryList(got2GoResults).get(0);

                    Intent intent = new Intent(this, LavatoryDetailActivity.class);
                    intent.putExtra(LAVATORY, topResult);
                    startActivity(intent);
                } else {
                    JSONObject finalResult = Parse.readJSON(response);
                    List<LavatoryData> lavatories = Parse.lavatoryList(finalResult);

                    // add the resulting lavatories to the map
                    for (LavatoryData ld : lavatories) {
                        placeLavatoryMarker(ld);
                    }

                    // add the resulting lavatories to the list
                    SearchResultsAdapter adapter = new SearchResultsAdapter(this,
                            R.layout.search_result_item,
                            R.id.search_result_item_lavatory_name, lavatories);

                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                View view, int position, long id) {
                            final LavatoryData selectedLavatory = (LavatoryData) parent
                                    .getItemAtPosition(position);
                            showLavatoryDetail(selectedLavatory);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(this.getClass().getName(), "Error in loading data: " + e.getLocalizedMessage());
                Toast.makeText(this, "The data is ruined. I'm sorry.",
                        Toast.LENGTH_SHORT).show();
            } finally {
                getLoaderManager().destroyLoader(loader.getId());
            }
        } else {
            getLoaderManager().destroyLoader(loader.getId());
            LayoutInflater inflater = (LayoutInflater)
                    this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.no_connection_popup,
                    (ViewGroup) findViewById(R.id.no_connection_layout));

            connectionPopup = new PopupWindow(layout, 350, 250, true);
            connectionPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            Log.e(this.getClass().getName(), "Error in loading data:\nResponse code: " + response.getCode());
            Toast.makeText(this, "Connection failure. Try again later.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void placeLavatoryMarker(LavatoryData ld) {
        Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(ld.latitude, ld.longitude)).title("Lavatory " + ld.lavatoryID));

        markerLavatoryDataMap.put(m, ld);
    }

    /**
     * Nullifies the reset Loader's data so it can be garbage collected.
     * NOTE: This is called automatically when the Loader is reset.
     *
     * @author Wilkes Sunseri
     *
     * @param loader the Loader being reset
     */
    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {
        // TODO: nullify the loader's cached data for garbage collecting
    }

    /**
     * Queries the server for lavatories that match the passed parameters
     *
     * @author Wilkes Sunseri
     *
     * @param bldgName building to search for
     * @param floor floor to search on
     * @param roomNumber room number to search for
     * @param locationLong longitude to search for
     * @param locationLat latitude to search for
     * @param maxDist max distance from the search coordinates to look
     * @param minRating minimum rating found lavatories must have
     * @param lavaType gender to search for
     */
    //queries the server for lavatories that meet the passed parameters
    private void lavatorySearch(String bldgName, String floor,
            String roomNumber, String locationLong, String locationLat,
            String maxDist, String minRating, String lavaType) {

        //save our search params for later in case we need to try again
        lastBldg = bldgName;
        lastFlr = floor;
        lastRmNum = roomNumber;
        lastLocLong = locationLong;
        lastLocLat = locationLat;
        lastMaxDist = maxDist;
        lastMinRating = minRating;
        lastLavaType = lavaType;

        Bundle args = new Bundle(8);

        //set up the request
        if (!bldgName.equals("")) {
            args.putString("bldgName", bldgName);
        }
        if (!floor.equals("")) {
            args.putString("floor", floor);
        }
        if (!roomNumber.equals("")) {
            args.putString("roomNumber", roomNumber);
        }
        if (!locationLong.equals("")) {
            args.putString("locationLong", locationLong);
        }
        if (!locationLat.equals("")) {
            args.putString("locationLat", locationLat);
        }
        if (!maxDist.equals("")) {
            args.putString("maxDist", maxDist);
        }
        if (!minRating.equals("")) {
            args.putString("minRating", minRating);
        }
        if (!lavaType.equals("")) {
            args.putString("lavaType", lavaType);
        }

        loadingScreen = ProgressDialog.show(this, "Loading...",
                "Getting data just for you!", true);

        // and finally pass it to the loader to be sent to the server
        getSupportLoaderManager().initLoader(MANAGER_ID, args, this);
    }

    /**
     * Retries the previous request and dismisses the popup box.
     *
     * @author Wilkes Sunseri
     *
     * @param target the popup box View to be dismissed
     */
    public void retryConnection(View target) {
        lavatorySearch(lastBldg, lastFlr, lastRmNum, lastLocLong, lastLocLat,
                lastMaxDist, lastMinRating, lastLavaType);
        dismissConnection(target);
    }

    /**
     * Dismisses the popup box.
     *
     * @author Wilkes Sunseri
     *
     * @param target the popup box View to be dismissed
     */
    public void dismissConnection(View target) {
        connectionPopup.dismiss();
    }

    /**
     * Populates the list view and map with the search results.
     *
     * @author
     *
     * @param lavatories the List of LavatoryData results
     */
    private void populateSearchResults(List<LavatoryData> lavatories) {

        // add the resulting lavatories to the map
        for (LavatoryData ld : lavatories) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(ld.latitude, ld.longitude)).title("Lavatory " + ld.lavatoryID));
        }

        SearchResultsAdapter adapter = new SearchResultsAdapter(this,
                R.layout.search_result_item,
                R.id.search_result_item_lavatory_name, lavatories);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                final LavatoryData selectedLavatory = (LavatoryData) parent
                        .getItemAtPosition(position);
                Intent intent = new Intent(parent.getContext(),
                        LavatoryDetailActivity.class);
                intent.putExtra(LAVATORY, selectedLavatory);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker m) {
        LavatoryData ld = markerLavatoryDataMap.get(m);
        assert (ld != null);

        showLavatoryDetail(ld);
    }

    private void showLavatoryDetail(LavatoryData ld) {
        Intent intent = new Intent(this,
                LavatoryDetailActivity.class);
        intent.putExtra(LAVATORY, ld);
        startActivity(intent);
    }
}

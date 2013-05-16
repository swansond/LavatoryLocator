package edu.washington.cs.lavatorylocator;

import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.washington.cs.lavatorylocator.RESTLoader.RESTResponse;
import edu.washington.cs.lavatorylocator.actionBarCompat.ActionBarActivity;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <code>Activity</code> first displayed when LavatoryLocator is opened. Shows a
 * list of nearby lavatories.
 * 
 * @author Keith Miller, Chris Rovillos
 * 
 */
public class MainActivity extends ActionBarActivity {

    private ListView listView;
    private PopupWindow popup;
    private GoogleMap mMap;

    /**
     * Activates the "Got2Go" feature, showing the user the nearest highly-rated
     * lavatory.
     * 
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void activateGot2go(MenuItem item) {
        // TODO: implement; remove stub message
        Context context = getApplicationContext();
        CharSequence notImplementedMessage = "Got2Go is not implemented yet!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, notImplementedMessage, duration);
        toast.show();
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

        listView = (ListView) findViewById(R.id.activity_main_search_results);
        //lavatorySearch("CSE", "1", "", "-122.305599", "47.653305", "50", "", "");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
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
                setUpMap();
            }
        }
    }
    
    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
    
//    /**
//     * Shows the search action view.
//     * 
//     * @param view
//     *            the <code>MenuItem</code> that was selected
//     */
//    public void showSearchView(MenuItem item) {
//        // TODO: implement; remove stub message
//        Context context = getApplicationContext();
//        CharSequence notImplementedMessage = "Search is not implemented yet!";
//        int duration = Toast.LENGTH_SHORT;
//
//        Toast toast = Toast.makeText(context, notImplementedMessage, duration);
//        toast.show();
//    }
//
//    /**
//     * Custom <code>Adapter</code> for displaying an array of
//     * <code>LavatoryData</code>s. Creates a custom <code>View</code> for each
//     * lavatory. row.
//     * 
//     * @author Keith Miller
//     * 
//     */
//    private class SearchResultsAdapter extends ArrayAdapter<LavatoryData> {
//
//        private List<LavatoryData> searchResults;
//        
//        /**
//         * Constructs a new <code>SearchResultsAdapter</code> with given
//         * <code>List</code> of <code>LavatoryData</code>s.
//         * 
//         * @param context
//         *            the current context
//         * @param resultRowResource
//         *            the resource ID for a layout file containing the LavatoryData
//         *            row layout to use when instantiating views
//         * @param LavatoryDataNameTextViewResourceId
//         *            the id of the <code>TextView</code> for displaying the
//         *            LavatoryData's name in each row
//         * @param reviews
//         *            a <code>List</code> of <code>Review</code>s to display
//         */
//        public SearchResultsAdapter(Context context, int resultRowResource,
//                int LavatoryDataNameTextViewResourceId, List<LavatoryData> searchResults) {
//            super(context, resultRowResource, LavatoryDataNameTextViewResourceId,
//                    searchResults);
//            this.searchResults = searchResults;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = getLayoutInflater().inflate(
//                        R.layout.search_result_item, parent, false);
//            }
//
//            ((RatingBar) convertView.findViewById(R.id.search_result_item_average_review))
//                    .setRating((float) searchResults.get(position)
//                            .avgRating);
//
//            ((TextView) convertView.findViewById(R.id.search_result_item_lavatory_name))
//                    .setText(searchResults.get(position).building + " "
//                            + searchResults.get(position).lavatoryID + " "
//                            + searchResults.get(position).lavatoryGender);
//
//            ((TextView) convertView.findViewById(R.id.search_result_item_review_count))
//                    .setText("" + searchResults.get(position).numReviews);
//
//            ((TextView) convertView.findViewById(R.id.search_result_item_floor)).setText("Floor "
//                    + searchResults.get(position).floor);
//
//            return convertView;
//        }
//    }
//    
//    /**
//     * Returns a new LavSearchLoader to this activity's LoaderManager.
//     * NOTE: We never need to call this directly as it is done automatically.
//     * 
//     * @author Wilkes Sunseri
//     * 
//     * @param id the id of the LoaderManager
//     * @param args the Bundle of arguments to be passed to the LavSearchLoader
//     * 
//     * @return A LavSearchLoader
//     */
//    @Override
//    public Loader<RESTLoader.RESTResponse> onCreateLoader(int id, Bundle args) {
//        Uri searchAddress = 
//                Uri.parse("http://lavlocdb.herokuapp.com/lavasearch.php");
//        //return new RESTLoader(getApplicationContext(), searchAddress, 
//        //        RESTLoader.requestType.GET, args);
//        return null;
//    }
//
//    /**
//     * Anything that needs to be done to process a successful load's data is to
//     * be done here.
//     * This is called automatically when the load finishes.
//     * 
//     * @author Wilkes Sunseri
//     * 
//     * @param loader the Loader doing the loading
//     * @param lavatories List of LavatoryData objects to be processed
//     */
//    @Override
//    public void onLoadFinished(Loader<RESTLoader.RESTResponse> loader,
//            RESTLoader.RESTResponse response) {
//        
//        if (response.getCode() == 200 && !response.getData().equals("")) {
//            try {
//                JSONObject finalResult = Parse.readJSON(response);
//                List<LavatoryData> lavatories = Parse.lavatoryList(finalResult);
//                
//                // add the resulting lavatories to the map
//                for (LavatoryData ld : lavatories) {
//                    mMap.addMarker(new MarkerOptions().position(new LatLng(ld.latitude, ld.longitude)).title("Lavatory " + ld.lavatoryID));
//                }
//                
//                // add the resulting lavatories to the list
//                SearchResultsAdapter adapter = new SearchResultsAdapter(this,
//                        R.layout.search_result_item, 
//                        R.id.search_result_item_lavatory_name, lavatories);
//
//                listView.setAdapter(adapter);
//
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view,
//                            int position, long id) {
//                        final LavatoryData selectedLavatory = (LavatoryData) parent
//                                .getItemAtPosition(position);
//                        Intent intent = new Intent(parent.getContext(),
//                                LavatoryDetailActivity.class);
//                        intent.putExtra(LAVATORY, selectedLavatory);
//                        startActivity(intent);
//                    }
//                });
//            } catch (Exception e) {
//                Toast.makeText(this, "The data is ruined. I'm sorry.", 
//                        Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "Connection failure. Try again later.", 
//                    Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * Anything that needs to be done to nullify a reset Loader's data is to be
//     * done here.
//     * NOTE: This is called automatically when the Loader is reset.
//     * 
//     * @author Wilkes Sunseri
//     * 
//     * @param loader the Loader being reset
//     */
//    @Override
//    public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {
//        // TODO: nullify the loader's data for garbage collecting
//    }
//    
//    /**
//     * Queries the server for lavatories that match the passed parameters
//     * 
//     * @author Wilkes Sunseri
//     * 
//     * @param bldgName building to search for
//     * @param floor floor to search on
//     * @param roomNumber room number to search for
//     * @param locationLong longitude to search for
//     * @param locationLat latitude to search for
//     * @param maxDist max distance from the search coordinates to look
//     * @param minRating minimum rating found lavatories must have
//     * @param lavaType gender to search for
//     */
//    //queries the server for lavatories that meet the passed parameters
//    private void lavatorySearch(String bldgName, String floor, 
//            String roomNumber, String locationLong, String locationLat, 
//            String maxDist, String minRating, String lavaType) {
//        Bundle args = new Bundle(10);
//        
//        //set up the request
//        if (!bldgName.equals("")) {
//            args.putString("bldgName", bldgName);
//        }
//        if (!floor.equals("")) {
//            args.putString("floor", floor);
//        }
//        if (!roomNumber.equals("")) {
//            args.putString("roomNumber", roomNumber);
//        }
//        if (!locationLong.equals("")) {
//            args.putString("locationLong", locationLong);
//        }
//        if (!locationLat.equals("")) {
//            args.putString("locationLat", locationLat);
//        }
//        if (!maxDist.equals("")) {
//            args.putString("maxDist", maxDist);
//        }
//        if (!minRating.equals("")) {
//            args.putString("minRating", minRating);
//        }
//        if (!lavaType.equals("")) {
//            args.putString("lavaType", lavaType);
//        }
//
//        //and finally pass it to the loader to be sent to the server
//        //getLoaderManager().initLoader(0, args, this);
//    }
}

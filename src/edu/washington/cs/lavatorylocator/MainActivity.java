package edu.washington.cs.lavatorylocator;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.app.LoaderManager;
import android.content.Loader;
import android.view.Menu;
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
public class MainActivity extends Activity
        implements LoaderCallbacks<RESTLoader.RESTResponse>{

    public static final String LAVATORY = "LAVATORY";
    private ListView listView;
    private PopupWindow popup;

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

        listView = (ListView) findViewById(R.id.activity_main_search_results);
        lavatorySearch("CSE", "1", "", "-122.305599", "47.653305", "50", "", "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /**
     * Shows the search action view.
     * 
     * @param view
     *            the <code>MenuItem</code> that was selected
     */
    public void showSearchView(MenuItem item) {
        // TODO: implement; remove stub message
        Context context = getApplicationContext();
        CharSequence notImplementedMessage = "Search is not implemented yet!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, notImplementedMessage, duration);
        toast.show();
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

        private List<LavatoryData> searchResults;
        
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
     * Returns a new LavSearchLoader to this activity's LoaderManager.
     * NOTE: We never need to call this directly as it is done automatically.
     * 
     * @author Wilkes Sunseri
     * 
     * @param id the id of the LoaderManager
     * @param args the Bundle of arguments to be passed to the LavSearchLoader
     * 
     * @return A LavSearchLoader
     */
    @Override
    public Loader<RESTLoader.RESTResponse> onCreateLoader(int id, Bundle args) {
        Uri searchAddress = 
                Uri.parse("http://lavlocdb.herokuapp.com/lavasearch.php");
        return new RESTLoader(getApplicationContext(), searchAddress, 
                RESTLoader.requestType.GET, args);
    }

    /**
     * Anything that needs to be done to process a successful load's data is to
     * be done here.
     * This is called automatically when the load finishes.
     * 
     * @author Wilkes Sunseri
     * 
     * @param loader the Loader doing the loading
     * @param lavatories List of LavatoryData objects to be processed
     */
    @Override
    public void onLoadFinished(Loader<RESTLoader.RESTResponse> loader,
            RESTLoader.RESTResponse response) {
        
        if (response.getCode() == 200 && !response.getData().equals("")) {
            try {
                JSONObject finalResult = Parse.readJSON(response);
                List<LavatoryData> lavatories = Parse.lavatoryList(finalResult);

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
            } catch (Exception e) {
                Toast.makeText(this, "The data is ruined. I'm sorry.", 
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Connection failure. Try again later.", 
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Anything that needs to be done to nullify a reset Loader's data is to be
     * done here.
     * NOTE: This is called automatically when the Loader is reset.
     * 
     * @author Wilkes Sunseri
     * 
     * @param loader the Loader being reset
     */
    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {
        // TODO: nullify the loader's data for garbage collecting
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
        Bundle args = new Bundle(10);
        
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

        //and finally pass it to the loader to be sent to the server
        getLoaderManager().initLoader(0, args, this);
    }
}

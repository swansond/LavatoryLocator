package edu.washington.cs.lavatorylocator;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.app.LoaderManager;
import android.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
        implements LoaderManager.LoaderCallbacks<List<LavatoryData>>{

    private ListView listView;

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

        //Example method call for testing
        //TODO: move call to the right part of the activity and delete this
        //lavatorySearch("1", "2", "3", "4", "5", "6", "7", "8", "9");

        listView = (ListView) findViewById(R.id.activity_main_search_results);

        // create dummy list of search results
        // TODO: replace with actual search results once networking stuff gets
        // finalized
        LavatoryData[] results = new LavatoryData[] {
                new LavatoryData(1, 'M', "Mary Gates Hall", "3", "test", 1.0,
                        2.0, 5, 2.5),
                new LavatoryData(2, 'F', "Mary Gates Hall", "3", "test", 1.0, 
                        2.0, 20, 3),
                new LavatoryData(
                        3,
                        'M',
                        "Paul G. Allen Center for Computer Science and Engineering",
                        "B1", "test", 1.0, 2.0, 1, 5),
                new LavatoryData(
                        4,
                        'F',
                        "Paul G. Allen Center for Computer Science and Engineering",
                        "B1", "test", 1.0, 2.0, 1, 4.5),
                new LavatoryData(
                        5,
                        'M',
                        "Paul G. Allen Center for Computer Science and Engineering",
                        "6", "test", 1.0, 2.0, 16, 4) };
        List<LavatoryData> resultsAsList = Arrays.asList(results);

        SearchResultsAdapter adapter = new SearchResultsAdapter(this,
                R.layout.search_result_item, R.id.search_result_item_lavatory_name, resultsAsList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                final LavatoryData LavatoryData = (LavatoryData) parent
                        .getItemAtPosition(position);
                Intent intent = new Intent(parent.getContext(),
                        LavatoryDetailActivity.class);
                // TODO: Once LavatoryData is Parcelable, pass it in to
                // LavatoryDetail
                startActivity(intent);
            }
        });
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
    public Loader<List<LavatoryData>> onCreateLoader(int id, Bundle args) {
        return new LavSearchLoader(getApplicationContext(), args);
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
    public void onLoadFinished(Loader<List<LavatoryData>> loader,
            List<LavatoryData> lavatories) {
        // TODO: process data
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
    public void onLoaderReset(Loader<List<LavatoryData>> loader) {
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
        Bundle args = new Bundle(9);

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
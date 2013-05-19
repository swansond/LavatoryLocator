package edu.washington.cs.lavatorylocator.activity;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.R.id;
import edu.washington.cs.lavatorylocator.R.layout;
import edu.washington.cs.lavatorylocator.R.menu;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.util.Parse;
import edu.washington.cs.lavatorylocator.util.RESTLoader;
import edu.washington.cs.lavatorylocator.util.RESTLoader.RESTResponse;
import edu.washington.cs.lavatorylocator.util.RESTLoader.requestType;

/**
 * <code>Activity</code> displayed when searching for lavatories. Shows a form
 * to fill out with search parameters.
 *
 * @author Wilkes Sunseri, (featuring code from Keith Miller, Chris Rovillos)
 *
 */
public class SearchActivity extends SherlockFragmentActivity
        implements LoaderCallbacks<RESTResponse>{

    private static final String LAVA_SEARCH
            = "http://lavlocdb.herokuapp.com/lavasearch.php";
    //this number just needs to not collide with other managers
    private static final int MANAGER_ID = 5;
    public static final String SEARCH_RESULTS = "SEARCH RESULTS";

    private PopupWindow connectionPopup;
    private ProgressDialog loadingScreen;

    //stored data in case we need to repeat a query
    private String lastBldg;
    private String lastLavaType;
    private String lastLocLat;
    private String lastLocLong;
    private String lastFlr;
    private String lastRmNum;
    private String lastMaxDist;
    private String lastMinRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    /**
     * Searches the database for lavatories.
     *
     * @author Wilkes Sunseri and featuring code by NAME_HERE
     *
     * @param item the <code>MenuItem</code> that was selected
     */
    public void searchLava(MenuItem item) {
        //I don't think these can be line brokenly cleanly
        String buildingName = ((EditText) findViewById(R.id.activity_search_building_name)).getText().toString();
        String roomNum = ((EditText) findViewById(R.id.activity_search_room_number)).getText().toString();
        String floor = ((EditText) findViewById(R.id.activity_search_floor)).getText().toString();
        RatingBar ratingbar = ((RatingBar) findViewById(R.id.activity_search_rating));
        int lavaType = ((RadioGroup) findViewById(R.id.activity_search_type)).getCheckedRadioButtonId();

        String lavaTypeString = "";
        switch (lavaType) {
            case R.id.activity_search_male:
                lavaTypeString = "M";
                break;
            case R.id.activity_search_female:
                lavaTypeString = "F";
                break;
        }

        String longitude = ((EditText) findViewById(R.id.activity_search_longitude)).getText().toString();
        String latitude = ((EditText) findViewById(R.id.activity_search_latitude)).getText().toString();
        String maxDist = ((EditText) findViewById(R.id.activity_search_max_distance)).getText().toString();
        float minRating = ratingbar.getRating();

        lavatorySearch(buildingName, floor, roomNum, longitude, latitude,
                maxDist, Float.toString(minRating), lavaTypeString);
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
    public void onLoadFinished(Loader<RESTResponse> loader,
            RESTResponse response) {
        loadingScreen.dismiss();
        if (response.getCode() == HttpStatus.SC_OK) {
            Intent intent = new Intent(this, MainActivity.class);
            try {
                // a lot of object juggling is needed to get this to MainActivity
                JSONObject finalResult = Parse.readJSON(response);
                List<LavatoryData> lavatories = Parse.lavatoryList(finalResult);
                Parcelable[] parcs = new Parcelable[lavatories.size()];
                lavatories.toArray(parcs);
                Bundle results = new Bundle(1);
                results.putParcelableArray("results", parcs);
                intent.putExtra(SEARCH_RESULTS, results);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                Log.d("tagged", e.toString());
            }
            startActivity(intent);
        } else {
            //the connection failed
            getLoaderManager().destroyLoader(loader.getId());
            LayoutInflater inflater = (LayoutInflater)
                    this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.no_connection_popup,
                    (ViewGroup) findViewById(R.id.no_connection_layout));

            connectionPopup = new PopupWindow(layout, 350, 250, true);
            connectionPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        }
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
    public void onLoaderReset(Loader<RESTResponse> loader) {
        // TODO Nullify loader's data once it has data to nullify
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
        //and finally pass it to the loader to be sent to the server
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

}

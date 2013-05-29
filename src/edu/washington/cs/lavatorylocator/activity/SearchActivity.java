package edu.washington.cs.lavatorylocator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.washington.cs.lavatorylocator.R;

/**
 * {@link android.app.Activity} displayed to search for lavatories. Shows a form
 * to fill out with search parameters, which are sent back to the
 * {@link MainActivity}.
 *
 * @author Wil Sunseri
 * @author Keith Miller
 * @author Chris Rovillos
 *
 */
public class SearchActivity extends SherlockActivity {

    // ----------------------------------------------------------
    // CONSTANTS
    // ----------------------------------------------------------
    // Keys used in the Intent sent from and to the MainActivity with the user's
    // search parameters
    public static final String SEARCH_PARAMETERS = "searchParameters";
    public static final String BUILDING_PARAMETER = "buildingParameter";
    public static final String ROOM_PARAMETER = "roomParameter";
    public static final String FLOOR_PARAMETER = "floorParameter";
    public static final String MIN_RATING_PARAMETER = "minRatingParameter";
    public static final String RADIUS_PARAMETER = "radiusParameter";
    public static final String LATITUDE_PARAMETER = "latitudeParameter";
    public static final String LONGITUDE_PARAMETER = "longitudeParameter";
    public static final String TYPE_PARAMETER = "typeParameter";

    private static final String TAG = "SearchActivity";

    // ----------------------------------------------------------
    // ACTIVITY LIFECYCLE
    // ----------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu called");

        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected, item: " + item.toString());

        switch (item.getItemId()) {
        case android.R.id.home:
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    // -------------------------------------------------------------
    // VIEW EVENT HANDLERS
    // -------------------------------------------------------------
    /**
     * Submits the user's entered search parameters back to the
     * {@link MainActivity}.
     *
     * @param item
     *            the {@link MenuItem} that was selected
     */
    public void submitSearchParameters(MenuItem item) {
        Log.d(TAG, "submitSearchParameters called");

        final String building = ((EditText) findViewById(
                R.id.activity_search_building_name))
                .getText().toString().trim();
        final String room = ((EditText) findViewById(
                R.id.activity_search_room_number))
                .getText().toString().trim();
        final String floor = ((EditText) findViewById(
                R.id.activity_search_floor))
                .getText().toString().trim();
        final String longitude = ((EditText) findViewById(
                R.id.activity_search_longitude))
                .getText().toString().trim();
        final String latitude = ((EditText) findViewById(
                R.id.activity_search_latitude))
                .getText().toString().trim();
        final String radiusText = ((EditText) findViewById(
                R.id.activity_search_max_distance))
                .getText().toString().trim();

        final double minRating = ((RatingBar) findViewById(
                R.id.activity_search_rating))
                .getRating();
        final int lavatoryTypeRadioButtonId = ((RadioGroup) findViewById(
                R.id.activity_search_type))
                .getCheckedRadioButtonId();

        String type = "";
        if (lavatoryTypeRadioButtonId == R.id.activity_search_male) {
            type = "M";
        } else if (lavatoryTypeRadioButtonId == R.id.activity_search_female) {
            type = "F";
        }

        final boolean textParametersSpecified = (!TextUtils.isEmpty(building)
                || !TextUtils.isEmpty(room) || !TextUtils.isEmpty(floor));
        final boolean locationParametersSpecified = (
                !TextUtils.isEmpty(latitude)
                && !TextUtils.isEmpty(longitude) && !TextUtils
                .isEmpty(radiusText));
        final boolean typeParametersSpecified = ((RadioGroup) findViewById(
                R.id.activity_search_type))
                .getCheckedRadioButtonId() != -1;

        if (textParametersSpecified || locationParametersSpecified
                || typeParametersSpecified) {
            final Intent intent = getIntent();
            intent.putExtra(BUILDING_PARAMETER, building);
            intent.putExtra(FLOOR_PARAMETER, floor);
            intent.putExtra(ROOM_PARAMETER, room);
            intent.putExtra(MIN_RATING_PARAMETER, minRating);
            intent.putExtra(RADIUS_PARAMETER, radiusText);
            intent.putExtra(LATITUDE_PARAMETER, latitude);
            intent.putExtra(LONGITUDE_PARAMETER, longitude);
            intent.putExtra(TYPE_PARAMETER, type);

            this.setResult(RESULT_OK, intent);
            finish();
        } else {
            Log.d(TAG, "No search parameters entered");

            // TODO: move to string resources XML file
            final String message = "Enter some search parameters!";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

}

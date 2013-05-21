package edu.washington.cs.lavatorylocator.activity;

import java.util.List;

import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.springandroid
    .SpringAndroidSpiceRequest;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.adapter.ReviewsListAdapter;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.ReviewData;
import edu.washington.cs.lavatorylocator.model.Reviews;
import edu.washington.cs.lavatorylocator.network.GetLavatoryReviewsRequest;
import edu.washington.cs.lavatorylocator.network.UpdateHelpfulnessRequest;

/**
 * {@link android.app.Activity} for viewing information about a specific
 * lavatory.
 *
 * @author Chris Rovillos
 * @author Wil Sunseri
 *
 */
public class LavatoryDetailActivity extends
        JacksonSpringSpiceSherlockFragmentActivity {

    // --------------------------------------------------------------------
    // CONSTANTS
    // --------------------------------------------------------------------
    /**
     * Key for caching reviews.
     */
    private static final String REVIEWS_JSON_CACHE_KEY = "reviewsJson";

    /**
     * Cache duration, in milliseconds.
     */
    private static final long JSON_CACHE_DURATION =
            DurationInMillis.ALWAYS_EXPIRED;

    /**
     * Key for storing the loaded lavatory in persistent storage.
     */
    public static final String LAVATORY_DATA = "lavatoryData";

    // TODO: replace when user IDs are implemented
    private static final int STUB_USER_ID = 1;

    public static final int POPUP_WIDTH = 350;
    public static final int POPUP_HEIGHT = 250;

    // ------------------------------------------------------------------
    // INSTANCE VARIABLES
    // ------------------------------------------------------------------
    private PopupWindow popup;
    private LavatoryData lavatory;

    // -------------------------------------------------------------
    // ACTIVITY LIFECYCLE
    // -------------------------------------------------------------
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the Up button in the action bar.
        setupActionBar();

        final Intent intent = getIntent();

        // restore the state saved in persistent storage if necessary
        if (intent.hasExtra(MainActivity.LAVATORY_DATA)) {
            // called from the list of bathrooms, data is passed in
            lavatory = intent.getParcelableExtra(MainActivity.LAVATORY_DATA);
        } else if (savedInstanceState != null
                && savedInstanceState.containsKey(MainActivity.LAVATORY_DATA)) {
            // called from restore, get lav info from passed bundle
            lavatory = savedInstanceState
                    .getParcelable(MainActivity.LAVATORY_DATA);
        } else if (getSharedPreferences("User", MODE_PRIVATE).contains("ID")) {
            // called after destruction and saveInstanceState did not get called
            // have to build lavatory from data stored in onPause
            final SharedPreferences data =
                    getSharedPreferences("User", MODE_PRIVATE);
            final int id = data.getInt("ID", 0);
            final char type = data.getString("Gender", "").charAt(0);
            final String building = data.getString("Building", "");
            final String floor = data.getString("Floor", "");
            final String room = data.getString("RoomNumber", "");
            final double longitude = data.getFloat("Long", 0);
            final double latitude = data.getFloat("Lat", 0);
            final int reviews = data.getInt("NumReviews", 0);
            final float avgRating = data.getFloat("Average", 0);

            lavatory = new LavatoryData(id, type, building, floor, room,
                    latitude, longitude, reviews, avgRating);

        } /*else
            // Some bad state, do nothing and throw a null pointer exception
        }*/

        setContentView(R.layout.activity_lavatory_detail);

        setTitle(lavatory.getName());

        setUpListView();

        // TODO: replace when Facebook authentication is implemented
        loadReviews("0", Integer.toString(lavatory.getLid()), "1",
                "helpfulness", "descending");
    }

    @Override
    protected void onPause() {
        super.onPause();

        final SharedPreferences settings =
                getSharedPreferences("User", MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();

        // save the current lavatory data
        editor.putInt("ID", lavatory.getLid());
        editor.putString("Gender", String.valueOf(lavatory.getType()));
        editor.putString("Building", lavatory.getBuilding());
        editor.putString("Floor", lavatory.getFloor());
        editor.putString("RoomNumber", lavatory.getRoom());
        editor.putFloat("Long", (float) lavatory.getLongitude());
        editor.putFloat("Lat", (float) lavatory.getLatitude());
        editor.putInt("NumReviews", lavatory.getReviews());
        editor.putFloat("Average", lavatory.getAvgRating());
        editor.commit();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.LAVATORY_DATA, lavatory);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lavatory = (LavatoryData) savedInstanceState
                .get(MainActivity.LAVATORY_DATA);
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.lavatory_detail, menu);
        return true;
    }

    // ----------------------------------------------------------------
    // VIEW EVENT HANDLERS
    // ----------------------------------------------------------------
    /**
     * Logs the user in so that they can add missing lavatories.
     *
     * @param target
     *           the {@link View} that was selected
     */
    public final void loginUser(final View target) {
        final SharedPreferences userDetails = getApplicationContext()
                .getSharedPreferences("User", MODE_PRIVATE);
        userDetails.edit().putBoolean("isLoggedIn", true).commit();
        dismissLoginPrompt();
    }

    /**
     * Closes the popup window.
     */
    public final void dismissLoginPrompt() {
        popup.dismiss();
    }

    /**
     * Goes to the {@link AddReviewActivity} to allow the user to add a review
     * about the current lavatory.
     *
     * @param item
     *            the {@link MenuItem} that was selected
     */
    public final void goToAddReviewActivity(final MenuItem item) {
        final SharedPreferences userDetails = getApplicationContext()
                .getSharedPreferences("User", MODE_PRIVATE);
        final boolean loggedIn = userDetails.getBoolean("isLoggedIn", false);

        if (!loggedIn) {
            final LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.login_popup,
                    (ViewGroup) findViewById(R.id.login_popup_layout));

            popup = new PopupWindow(layout, POPUP_WIDTH, POPUP_HEIGHT, true);
            popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        } else {
            final Intent intent = new Intent(this, AddReviewActivity.class);
            intent.putExtra(LAVATORY_DATA, lavatory);
            startActivityForResult(intent, 0);
        }
    }

    /**
     * Allows the user to edit the current lavatory's information.
     *
     * @param item
     *            the {@link MenuItem} that was selected
     */
    public final void goToEditLavatoryDetailActivity(final MenuItem item) {
        final Intent intent = new Intent(
                this, EditLavatoryDetailActivity.class);
        intent.putExtra(LAVATORY_DATA, lavatory);
        intent.putExtra(EditLavatoryDetailActivity.USER_ID_KEY, STUB_USER_ID);
        startActivity(intent);
    }
    
    /**
     * Mark a review as being helpful.
     *
     * @param v
     *            the {@link View} that was selected
     */
    public final void markHelpful(final View v) {
        final SharedPreferences userDetails = getApplicationContext()
            .getSharedPreferences("User", MODE_PRIVATE);
        final boolean loggedIn = userDetails.getBoolean("isLoggedIn", false);

        if (!loggedIn) {
            final LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.login_popup,
                    (ViewGroup) findViewById(R.id.login_popup_layout));

            final int width = 350;
            final int height = 250;
            popup = new PopupWindow(layout, width, height, true);
            popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        } else {
            getSherlock().setProgressBarIndeterminateVisibility(true);
            final int uid = STUB_USER_ID;  
            // TODO: change once facebook login works
            final int reviewId = (Integer) v.getId();
            final int helpful = 1;

            /*Toast.makeText(this, "uid: " + uid + " reviewId: " + reviewId
                    + " helpful: " + helpful,
                    Toast.LENGTH_LONG).show();*/ // for debugging
            SpringAndroidSpiceRequest<ResponseEntity> request;

            request = new UpdateHelpfulnessRequest(uid, reviewId, helpful);
            getSpiceManager().execute(request,
                    new UpdateHelpfulnessRequestListener());
        }
    }

    /**
     * Mark a review as being not helpful.
     *
     * @param v
     *            the {@link View} that was selected
     */
    public final void markNotHelpful(final View v) {
        final SharedPreferences userDetails = getApplicationContext()
                .getSharedPreferences("User", MODE_PRIVATE);
        final boolean loggedIn = userDetails.getBoolean("isLoggedIn", false);

        if (!loggedIn) {
            final LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.login_popup,
                    (ViewGroup) findViewById(R.id.login_popup_layout));

            final int width = 350;
            final int height = 250;
            popup = new PopupWindow(layout, width, height, true);
            popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        } else {
            getSherlock().setProgressBarIndeterminateVisibility(true);
            // TODO: change once facebook login works
            final int uid = STUB_USER_ID; 
            final int reviewId = (Integer) v.getId();
            final int helpful = -1;
            SpringAndroidSpiceRequest<ResponseEntity> request;
            /*Toast.makeText(this, "uid: " + uid + " reviewId: " + reviewId
                    + " helpful: " + helpful,
                    Toast.LENGTH_LONG).show();*/ // for debugging
            request = new UpdateHelpfulnessRequest(uid, reviewId, helpful);
            getSpiceManager().execute(request,
                    new UpdateHelpfulnessRequestListener());
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            /* This ID represents the Home or Up button. In the case of this
             * activity, the Up button is shown. Use NavUtils to allow users
             * to navigate up one level in the application structure. For
             * more details, see the Navigation pattern on Android Design:
             *
             * http://developer.android.com/design/patterns/navigation.html
             */
            NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ---------------------------------------------------
    // PRIVATE HELPER METHODS
    // ---------------------------------------------------
    /**
     * Displays the given {@link List} of reviews.
     *
     * @param reviews
     *            a {@link List} of reviews to display
     */
    private void displayReviews(final List<ReviewData> reviews) {
        final ReviewsListAdapter adapter = new ReviewsListAdapter(this,
                R.layout.review_item, R.id.review_author, reviews);

        final ListView listView = (ListView) findViewById(
                R.id.lavatory_detail_list_view);
        listView.setAdapter(adapter);
    }

    /**
     * Gets a page of (10) reviews for the lavatory.
     *
     * @param userId
     *            the ID number of the user
     * @param lavatoryId
     *            the ID number for the lavatory
     * @param page
     *            the page of reviews to get
     * @param sortParam
     *            the manner by which reviews are sorted
     * @param sortDirection
     *            ascending or descending
     */
    private void loadReviews(final String userId, final String lavatoryId,
            final String page, final String sortParam,
            final String sortDirection) {
        getSherlock().setProgressBarIndeterminateVisibility(true);

        getSpiceManager().execute(
                new GetLavatoryReviewsRequest(userId, lavatoryId, page,
                        sortParam, sortDirection), REVIEWS_JSON_CACHE_KEY,
                        JSON_CACHE_DURATION, new ReviewsRequestListener());
    }

    /**
     * Sets up the list view.
     */
    private void setUpListView() {
        final View headerView = getLayoutInflater().inflate(
                R.layout.activity_lavatory_detail_header, null);
        ((TextView) headerView.findViewById(R.id.lavatory_detail_name_text))
        .setText(lavatory.getName());
        ((TextView) headerView.findViewById(R.id.lavatory_detail_building_text))
        .setText(lavatory.getBuilding());
        ((RatingBar) headerView.findViewById(R.id.lavatory_detail_rating))
        .setRating(lavatory.getAvgRating());

        final ListView listView = (ListView) findViewById(
                R.id.lavatory_detail_list_view);
        listView.addHeaderView(headerView, null, false);
    }

    // ----------------------------------------------------------------------
    // PRIVATE INNER CLASSES
    // ----------------------------------------------------------------------
    /**
     * {@code RequestListener} for reviews from the LavatoryLocator service.
     *
     * @author Chris Rovillos
     *
     */
    private class ReviewsRequestListener implements RequestListener<Reviews> {

        @Override
        public void onRequestFailure(final SpiceException spiceException) {
            // TODO: move to string resources XML file
            final String errorMessage = "Reviews request failed: "
                    + spiceException.getMessage();

            Log.e(getLocalClassName(), errorMessage);
            Toast.makeText(LavatoryDetailActivity.this, errorMessage,
                    Toast.LENGTH_LONG).show();
            LavatoryDetailActivity.this
            .getSherlock().setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void onRequestSuccess(final Reviews reviews) {
            LavatoryDetailActivity.this.displayReviews(reviews.getReviews());

            LavatoryDetailActivity.this
            .getSherlock().setProgressBarIndeterminateVisibility(false);
        }
    }

    /**
     * {@code RequestListener} for helpfulness updates from the
     *      LavatoryLocator service.
     *
     * @author Keith Miller
     *
     */
    private class UpdateHelpfulnessRequestListener implements
        RequestListener<ResponseEntity> {

        @Override
        public void onRequestFailure(final SpiceException spiceException) {
            // TODO: move to string resources XML file
            final String errorMessage = "Submission failed: "
                    + spiceException.getMessage();

            Log.e(getLocalClassName(), errorMessage);
            Toast.makeText(LavatoryDetailActivity.this, errorMessage,
                    Toast.LENGTH_LONG).show();
            LavatoryDetailActivity.this
                    .getSherlock().setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void onRequestSuccess(final ResponseEntity responseEntity) {
            LavatoryDetailActivity.this
                    .getSherlock().setProgressBarIndeterminateVisibility(false);

            // TODO: move to string resources XML file
            final String message = "Submitted!";
            Toast.makeText(LavatoryDetailActivity.this, message,
                    Toast.LENGTH_LONG).show();
        }
    }

}

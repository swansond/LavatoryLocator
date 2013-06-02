package edu.washington.cs.lavatorylocator.activity;

import java.util.List;

import org.springframework.http.ResponseEntity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.springandroid.
        SpringAndroidSpiceRequest;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.activity.libraryabstract.
        JacksonSpringSpiceSherlockFragmentActivity;
import edu.washington.cs.lavatorylocator.adapter.ReviewsListAdapter;
import edu.washington.cs.lavatorylocator.googleplus.PlusClientFragment;
import edu.washington.cs.lavatorylocator.googleplus.PlusClientFragment.
        OnSignedInListener;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.ReviewData;
import edu.washington.cs.lavatorylocator.model.Reviews;
import edu.washington.cs.lavatorylocator.network.DeleteLavatoryRequest;
import edu.washington.cs.lavatorylocator.network.GetLavatoryReviewsRequest;
import edu.washington.cs.lavatorylocator.network.UpdateHelpfulnessRequest;
import edu.washington.cs.lavatorylocator.view.ReviewListItemView;

/**
 * {@link android.app.Activity} for viewing information about a specific
 * lavatory.
 *
 * @author Chris Rovillos
 * @author Wil Sunseri
 *
 */
public class LavatoryDetailActivity extends
        JacksonSpringSpiceSherlockFragmentActivity implements
        OnSignedInListener {

    // --------------------------------------------------------------------
    // CONSTANTS
    // --------------------------------------------------------------------
    /**
     * Key for caching reviews.
     */
    private static final String REVIEWS_JSON_CACHE_KEY = "reviewsJson";
    
    private static final String DELETE_LAVATORY_REQUEST_CACHE_KEY =
            "deleteLavatoryRequest";
    
    private static final String REVIEW_HELPFULNESS_REQUEST_CACHE_KEY =
            "reviewHelpfulnessRequest";

    /**
     * Cache duration, in milliseconds.
     */
    private static final long JSON_CACHE_DURATION =
            DurationInMillis.ALWAYS_EXPIRED;

    /**
     * Key for storing the loaded lavatory in persistent storage.
     */
    public static final String LAVATORY_DATA = "lavatoryData";

    private static final String TAG = "LavatoryDetailActivity";
    
    private static final int REVIEW_PAGE_NUMBER = 1;
    
    private static final int REQUEST_CODE_PLUS_CLIENT_FRAGMENT = 0;

    // ------------------------------------------------------------------
    // INSTANCE VARIABLES
    // ------------------------------------------------------------------
    private PopupWindow popup;
    private LavatoryData lavatory;
    
    private PlusClientFragment mPlusClientFragment;
    private String uid;

    // -------------------------------------------------------------
    // ACTIVITY LIFECYCLE
    // -------------------------------------------------------------
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");

        super.onCreate(savedInstanceState);
        // Show the Up button in the action bar.
        setupActionBar();
        
        mPlusClientFragment = PlusClientFragment.getPlusClientFragment(this,
                null);

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

        }

        setContentView(R.layout.activity_lavatory_detail);

        setTitle(lavatory.getName());

        setUpListView();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause called");
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
    public void onResume() {
        super.onResume();
        mPlusClientFragment.signIn(REQUEST_CODE_PLUS_CLIENT_FRAGMENT);
    }
    
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        Log.d(TAG, "onSaveInstanceState called");

        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.LAVATORY_DATA, lavatory);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState called");

        super.onRestoreInstanceState(savedInstanceState);
        lavatory = (LavatoryData) savedInstanceState
                .get(MainActivity.LAVATORY_DATA);
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        Log.d(TAG, "setupActionBar called");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu called");

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
        Log.d(TAG, "loginUser called");

        final SharedPreferences userDetails = getApplicationContext()
                .getSharedPreferences("User", MODE_PRIVATE);
        userDetails.edit().putBoolean("isLoggedIn", true).commit();
        dismissLoginPrompt();
    }

    /**
     * Closes the popup window.
     */
    public final void dismissLoginPrompt() {
        Log.d(TAG, "dismissLoginPrompt called");

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
        Log.d(TAG, "goToAddReviewActivity called");

        final Intent intent = new Intent(this, AddReviewActivity.class);
        intent.putExtra(LAVATORY_DATA, lavatory);
        startActivityForResult(intent, 0);
    }

    /**
     * Allows the user to edit the current lavatory's information.
     *
     * @param item
     *            the {@link MenuItem} that was selected
     */
    public final void goToEditLavatoryDetailActivity(final MenuItem item) {
        Log.d(TAG, "goToEditLavatoryDetailActivity called");

        final Intent intent = new Intent(
                this, EditLavatoryDetailActivity.class);
        intent.putExtra(LAVATORY_DATA, lavatory);
        startActivity(intent);
    }

    /**
     * Requests that the current lavatory be deleted from the database.
     *
     * @param item
     *            the {@link MenuItem} that was selected
     */
    public final void requestDeletion(final MenuItem item) {
        Log.d(TAG, "requestDeletion called");

        getSherlock().setProgressBarIndeterminateVisibility(true);
        final DeleteLavatoryRequest request =
                new DeleteLavatoryRequest(lavatory.getLid(), uid);

        Log.d(TAG, "requestDeletion: executing DeleteLavatoryRequest...");
        getSpiceManager().execute(request, DELETE_LAVATORY_REQUEST_CACHE_KEY,
                JSON_CACHE_DURATION, new DeleteRequestListener());
    }

    /**
     * Mark a review as being helpful.
     *
     * @param v
     *            the {@link View} that was selected
     */
    @SuppressWarnings("rawtypes")
    public final void markHelpful(final View v) {
        Log.d(TAG, "markHelpful called");

        final int reviewId = v.getId();
        final int helpful = 1;

        SpringAndroidSpiceRequest<ResponseEntity> request;
        
        request = new UpdateHelpfulnessRequest(uid, reviewId, helpful);

        Log.d(TAG, "executing UpdateHelpfulnessRequest...");

        final ReviewListItemView thisView =
                (ReviewListItemView) v.getParent().getParent().getParent();
        getSpiceManager().execute(request,
                REVIEW_HELPFULNESS_REQUEST_CACHE_KEY, JSON_CACHE_DURATION,
                new UpdateHelpfulnessRequestListener(thisView));
    }

    /**
     * Mark a review as being not helpful.
     *
     * @param v
     *            the {@link View} that was selected
     */
    @SuppressWarnings("rawtypes")
    public final void markNotHelpful(final View v) {
        Log.d(TAG, "markNotHelpful called");

        getSherlock().setProgressBarIndeterminateVisibility(true);
        
        final int reviewId = v.getId();
        final int helpful = -1;
        SpringAndroidSpiceRequest<ResponseEntity> request;
        
        request = new UpdateHelpfulnessRequest(uid, reviewId, helpful);
        
        Log.d(TAG, "markHelpful: executing UpdateHelpfulnessRequest...");

        final ReviewListItemView thisView =
                (ReviewListItemView) v.getParent().getParent().getParent();
        getSpiceManager().execute(request,
                REVIEW_HELPFULNESS_REQUEST_CACHE_KEY, JSON_CACHE_DURATION,
                new UpdateHelpfulnessRequestListener(thisView));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected called");

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
    
    /**
     * Called when the {@link com.google.android.gms.plus.PlusClient} has been
     * connected successfully.
     *
     * @param plusClient
     *                 The connected {@link PlusClient} for making APIrequests.
     */
    @Override
    public void onSignedIn(PlusClient plusClient) {
        final Person user = plusClient.getCurrentPerson();
        uid = user.getId();
        loadReviews(uid, Integer.toString(lavatory.getLid()),
                REVIEW_PAGE_NUMBER,
                "helpfulness", "descending");
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
        Log.d(TAG, "displayReviews called");

        final ReviewsListAdapter adapter = new ReviewsListAdapter(this,
                R.layout.review_item, R.id.review_author, reviews);

        final ListView listView = (ListView) findViewById(
                R.id.lavatory_detail_list_view);
        listView.setAdapter(adapter);
    }

    /**
     * Gets a page of (10) reviews for the lavatory.
     *
     * @param uid
     *            the ID number of the user
     * @param lid
     *            the ID number for the lavatory
     * @param page
     *            the page of reviews to get
     * @param sortParam
     *            the manner by which reviews are sorted
     * @param sortDirection
     *            ascending or descending
     */
    private void loadReviews(final String uid, final String lid,
            final int page, final String sortParam,
            final String sortDirection) {
        Log.d(TAG, "loadReviews called");

        getSherlock().setProgressBarIndeterminateVisibility(true);

        Log.d(TAG, "loadReviews: executing GetLavatoryReviewsRequest...");
        getSpiceManager().execute(
                new GetLavatoryReviewsRequest(uid, lid, page,
                        sortParam, sortDirection), REVIEWS_JSON_CACHE_KEY,
                        JSON_CACHE_DURATION, new ReviewsRequestListener());
    }

    /**
     * Sets up the list view.
     */
    private void setUpListView() {
        Log.d(TAG, "setUpListView called");

        final View headerView = getLayoutInflater().inflate(
                R.layout.activity_lavatory_detail_header, null);
        final TextView lavatoryNameTextView = (TextView) headerView
                .findViewById(R.id.lavatory_detail_name_text);
        final TextView floorTextView = (TextView) headerView
                .findViewById(R.id.lavatory_detail_floor_text);
        final TextView buildingTextView = (TextView) headerView
                .findViewById(R.id.lavatory_detail_building_text);
        final RatingBar averageRatingBar = (RatingBar) headerView
                .findViewById(R.id.lavatory_detail_rating);
        
        final String lavatoryName = lavatory.getName();
        final String floor = lavatory.getFloor();
        final String building = lavatory.getBuilding();
        final float avgRating = lavatory.getAvgRating();
        
        final String floorTextPrefix = getString(R.string.floor_prefix);
        
        lavatoryNameTextView.setText(lavatoryName);
        floorTextView.setText(floorTextPrefix + floor);
        buildingTextView.setText(building);
        averageRatingBar.setRating(avgRating);

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
        private static final String TAG = "ReviewsRequestListener";

        @Override
        public void onRequestSuccess(final Reviews reviews) {
            final String logSuccessMessage = "Review helpfulness request "
                    + "succeeded";
            Log.d(TAG, logSuccessMessage);

            LavatoryDetailActivity.this.displayReviews(reviews.getReviews());

            LavatoryDetailActivity.this.getSherlock().
                    setProgressBarIndeterminateVisibility(false);
        }
        
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            final String logErrorMessage = "Get lavatory reviews request "
                    + "failed: " + spiceException.getMessage();
            Log.e(TAG, logErrorMessage);

            Toast.makeText(LavatoryDetailActivity.this,
                    R.string.activity_lavatory_detail_get_reviews_error,
                    Toast.LENGTH_LONG).show();
            LavatoryDetailActivity.this.getSherlock().
                    setProgressBarIndeterminateVisibility(false);
        }
    }

    /**
     * {@code RequestListener} for helpfulness updates from the
     *      LavatoryLocator service.
     *
     * @author Keith Miller
     *
     */
    @SuppressWarnings("rawtypes")
    private class UpdateHelpfulnessRequestListener implements
            RequestListener<ResponseEntity> {
        private static final String TAG = "UpdateHelpfulnessRequestListener";

        private ReviewListItemView thisReview;

        /**
         * Creates a new UpdateHelpfulnessRequestListener.
         * 
         * @param review
         *              the ReviewListItem whose helpfulness button launched
         *              this request
         */
        public UpdateHelpfulnessRequestListener(ReviewListItemView review) {
            super();
            thisReview = review;
        }
        
        @Override
        public void onRequestSuccess(ResponseEntity responseEntity) {
            final String logSuccessMessage = "Review helpfulness request "
                    + "succeeded";
            Log.d(TAG, logSuccessMessage);

            LavatoryDetailActivity.this.getSherlock().
                    setProgressBarIndeterminateVisibility(false);
            
            thisReview.disableHelpfulnessButtons();

            Toast.makeText(LavatoryDetailActivity.this, R.string.
                activity_lavatory_detail_review_helpfulness_submission_success,
                    Toast.LENGTH_SHORT).show();
        }
        
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            final String logErrorMessage = "Review helpfulness request "
                    + "failed: " + spiceException.getMessage();
            Log.e(TAG, logErrorMessage);

            Toast.makeText(LavatoryDetailActivity.this, R.string.
                activity_lavatory_detail_review_helpfulness_submission_error,
                    Toast.LENGTH_LONG).show();
            LavatoryDetailActivity.this.getSherlock().
                    setProgressBarIndeterminateVisibility(false);
        }
    }

    /**
     * {@code RequestListener} for requests to have a lavatory deleted.
     *
     * @author Wilkes Sunseri
     *
     */
    @SuppressWarnings("rawtypes")
    private class DeleteRequestListener implements
            RequestListener<ResponseEntity> {
        private static final String TAG = "DeleteRequestListener";
        
        @Override
        public void onRequestSuccess(ResponseEntity responseEntity) {
            final String logSuccessMessage = "Lavatory deletion request "
                    + "succeeded";
            Log.d(TAG, logSuccessMessage);

            LavatoryDetailActivity.this.getSherlock().
                    setProgressBarIndeterminateVisibility(false);

            Toast.makeText(LavatoryDetailActivity.this,
                    R.string.activity_lavatory_detail_delete_lavatory_success,
                    Toast.LENGTH_SHORT).show();
        }
        
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            final String logErrorMessage = "Lavatory deletion request "
                    + "failed: " + spiceException.getMessage();
            Log.e(TAG, logErrorMessage);

            Toast.makeText(LavatoryDetailActivity.this,
                    R.string.activity_lavatory_detail_delete_lavatory_error,
                    Toast.LENGTH_LONG).show();
            LavatoryDetailActivity.this.getSherlock().
                    setProgressBarIndeterminateVisibility(false);
        }
    }
}

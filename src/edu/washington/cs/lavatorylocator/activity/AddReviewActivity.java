package edu.washington.cs.lavatorylocator.activity;


import org.apache.http.HttpStatus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.util.RESTLoader;
import edu.washington.cs.lavatorylocator.util.RESTLoader.RESTResponse;

/**
 * <code>Activity</code> for adding a review on a lavatory.
 *
 * @author Chris Rovillos
 *
 */
public class AddReviewActivity extends SherlockFragmentActivity
        implements LoaderCallbacks<RESTLoader.RESTResponse> {

    private static final String SUBMIT_REVIEW
            = "http://lavlocdb.herokuapp.com/submitreview.php";
    private static final int MANAGER_ID = 3;

    private ProgressDialog loadingScreen;
    private PopupWindow connectionPopup;

    //saved data in case we need to retry a query
    private String lastUid;
    private String lastLid;
    private String lastRating;
    private String lastReview;

    /**
     * Starts submitting the entered review to the LavatoryLocator service.
     *
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void addReview(MenuItem item) {
        Intent intent = getIntent();
        LavatoryData ld = intent.getParcelableExtra("LAVATORY");
        RatingBar ratingbar = ((RatingBar) findViewById(R.id.add_review_rating));
        float rating = ratingbar.getRating();
        EditText reviewText = ((EditText) findViewById(R.id.add_review_text));
        String reviewTextString = reviewText.getText().toString();
        updateReview("1", Integer.toString(ld.getLid()), Float.toString(rating), reviewTextString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        // Show the Up button in the action bar.
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.add_review, menu);
        return true;
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

            //TODO: save entered review as a draft when user navigates away

            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
     * @return A Loader to submit a review
     */
    @Override
    public Loader<RESTResponse> onCreateLoader(int id, Bundle args) {
        Uri searchAddress =
                Uri.parse(SUBMIT_REVIEW);
        return new RESTLoader(getApplicationContext(), searchAddress,
                RESTLoader.requestType.POST, args);
    }

    /**
     * Thanks the user for their submission if it is successful, or prompts
     * them to try again otherwise.
     *
     * This is called automatically when the load finishes.
     *
     * @author Wilkes Sunseri
     *
     * @param loader the Loader that did the submission request
     * @param response the server response
     */
    @Override
    public void onLoadFinished(Loader<RESTResponse> loader,
            RESTResponse response) {
        loadingScreen.dismiss();
        getLoaderManager().destroyLoader(loader.getId());
        if (response.getCode() == HttpStatus.SC_OK) {
            Toast.makeText(this, "Thank you for your submission",
                    Toast.LENGTH_SHORT).show();
        } else {
            LayoutInflater inflater = (LayoutInflater)
                    this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.no_connection_popup,
                    (ViewGroup) findViewById(R.id.no_connection_layout));

            connectionPopup = new PopupWindow(layout, 350, 250, true);
            connectionPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        }

    }

    /**
     * Nullifies the data of the Loader being reset so that it can be garbage
     * collected.
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
     * Sends a new review to the server.
     *
     * @author Wilkes Sunseri
     *
     * @param uid the ID of the user making the review
     * @param lid the ID of the lavatory being reviewed
     * @param rating the rating the user is giving the lavatory
     * @param review the review itself
     */
    private void updateReview(String uid, String lid, String rating,
            String review) {

        //save the search params in case we need them later
        lastUid = uid;
        lastLid = lid;
        lastRating = rating;
        lastReview = review;

        //set up the request
        Bundle args = new Bundle(4);
        if (!uid.equals("")) {
            args.putString("uid", uid);
        }
        if (!lid.equals("")) {
            args.putString("lid", lid);
        }
        if (!rating.equals("")) {
            args.putString("rating", rating);
        }
        if (!review.equals("")) {
            args.putString("review", review);
        }

        loadingScreen = ProgressDialog.show(this, "Loading...",
                "Getting data just for you!", true);
        //and initialize the Loader
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
        updateReview(lastUid, lastLid, lastRating, lastReview);
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
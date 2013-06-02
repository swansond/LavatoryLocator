package edu.washington.cs.lavatorylocator.activity;

import org.springframework.http.ResponseEntity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.activity.libraryabstract.JacksonSpringSpiceSherlockFragmentActivity;
import edu.washington.cs.lavatorylocator.googleplus.PlusClientFragment;
import edu.washington.cs.lavatorylocator.googleplus.PlusClientFragment.
    OnSignedInListener;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.network.AddReviewRequest;

/**
 * {@link android.app.Activity} for adding a review on a lavatory.
 * 
 * @author Chris Rovillos
 * @author Wil Sunseri
 * 
 */
public class AddReviewActivity extends
        JacksonSpringSpiceSherlockFragmentActivity implements
        OnSignedInListener {
    // -------------------------------------------------------------------
    // CONSTANTS
    // -------------------------------------------------------------------
    private static final int REQUEST_CODE_PLUS_CLIENT_FRAGMENT = 0;
    private static final String TAG = "AddReviewActivity";

    // -------------------------------------------------------------------
    // INSTANCE VARIABLES
    // -------------------------------------------------------------------
    private PlusClientFragment mPlusClientFragment;

    private String uid;

    // -------------------------------------------------------------------
    // ACTIVITY LIFECYCLE
    // -------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        // Show the Up button in the action bar.
        setupActionBar();

        mPlusClientFragment = PlusClientFragment.getPlusClientFragment(this,
                null);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mPlusClientFragment.signIn(REQUEST_CODE_PLUS_CLIENT_FRAGMENT);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu called");

        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.add_review, menu);
        return true;
    }
    
    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        Log.d(TAG, "setupActionBar called");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    // ----------------------------------------------------------------------
    // VIEW EVENT HANDLERS
    // ----------------------------------------------------------------------
    /**
     * Starts submitting the entered review to the LavatoryLocator service.
     * 
     * @param item
     *            the {@link MenuItem} that was selected
     */
    public void addReview(MenuItem item) {
        Log.d(TAG, "addReview called");

        final Intent intent = getIntent();
        final LavatoryData lavatory = intent
                .getParcelableExtra(LavatoryDetailActivity.LAVATORY_DATA);
        
        final RatingBar ratingBar = ((RatingBar) findViewById(
                R.id.add_review_rating));
        final EditText reviewTextView = ((EditText) findViewById(
                R.id.add_review_text));
        
        final int lid = lavatory.getLid();
        final float rating = ratingBar.getRating();
        final String reviewText = reviewTextView.getText().toString();
        
        AddReviewRequest request = new AddReviewRequest(uid, lid, rating,
                reviewText);
        getSpiceManager().execute(request, new AddReviewRequestListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected called");

        switch (item.getItemId()) {
        case android.R.id.home:
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html
            //

            // TODO: save entered review as a draft when user navigates away

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
     *            The connected {@link PlusClient} for making API requests.
     */
    @Override
    public void onSignedIn(PlusClient plusClient) {
        final Person user = plusClient.getCurrentPerson();
        uid = user.getId();
    }

    // ------------------------------------------------------------------
    // PRIVATE INNER CLASSES
    // ------------------------------------------------------------------
    /**
     * {@code RequestListener} for result of submitting the lavatory review to
     * the LavatoryLocator service.
     *
     * @author Chris Rovillos
     *
     */
    @SuppressWarnings("rawtypes")
    private class AddReviewRequestListener implements
            RequestListener<ResponseEntity> {
        private static final String TAG = "AddReviewRequestListener";

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.d(TAG, "onRequestFailure called");

            // TODO: move to string resources XML file
            final String errorMessage = "Submission failed: "
                    + spiceException.getMessage();

            Log.e(getLocalClassName(), errorMessage);
            Toast.makeText(AddReviewActivity.this, errorMessage,
                    Toast.LENGTH_LONG).show();
            AddReviewActivity.this.getSherlock().
                    setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void onRequestSuccess(ResponseEntity responseEntity) {
            Log.d(TAG, "onRequestSuccess called");

            AddReviewActivity.this.getSherlock().
                    setProgressBarIndeterminateVisibility(false);

            // TODO: move to string resources XML file
            final String message = "Submitted!";
            Toast.makeText(AddReviewActivity.this, message,
                    Toast.LENGTH_LONG).show();

            finish();
        }
    }
}

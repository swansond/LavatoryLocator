package edu.washington.cs.lavatorylocator.activity;

import org.springframework.http.ResponseEntity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.
    springandroid.SpringAndroidSpiceRequest;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.activity.libraryabstract.
    JacksonSpringSpiceSherlockFragmentActivity;
import edu.washington.cs.lavatorylocator.googleplus.PlusClientFragment;
import edu.washington.cs.lavatorylocator.googleplus.PlusClientFragment.
    OnSignedInListener;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.network.AddLavatoryRequest;
import edu.washington.cs.lavatorylocator.network.EditLavatoryDetailRequest;

/**
 * {@link android.app.Activity} for adding a new lavatory into the
 * LavatoryLocator service.
 *
 * @author Chris Rovillos
 * @author Wil Sunseri
 *
 */
public class EditLavatoryDetailActivity extends
        JacksonSpringSpiceSherlockFragmentActivity implements
        OnSignedInListener {
    // -------------------------------------------------------------------
    // CONSTANTS
    // -------------------------------------------------------------------
    private static final String TAG = "EditLavatoryDetailActivity";
    
    private static final int REQUEST_CODE_PLUS_CLIENT_FRAGMENT = 0;
    
    /**
     * Key for caching the result from a edit lavatory detail request.
     */
    private static final String EDIT_LAVATORY_DETAIL_CACHE_KEY =
            "editLavatoryDetailJson";

    /**
     * Cache duration, in milliseconds.
     */
    private static final long JSON_CACHE_DURATION =
            DurationInMillis.ALWAYS_EXPIRED;
    
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
        setContentView(R.layout.activity_edit_lavatory);
        // Show the Up button in the action bar.
        setupActionBar();
        
        mPlusClientFragment = PlusClientFragment.getPlusClientFragment(this,
                null);

        final LavatoryData lavatoryData = getIntent().getParcelableExtra(
                LavatoryDetailActivity.LAVATORY_DATA);
        if (lavatoryData != null) {
            setTitle(R.string.title_edit_lavatory_detail);

            final String building = lavatoryData.getBuilding();
            final String floor = lavatoryData.getFloor();
            final String room = lavatoryData.getRoom();
            final char type = lavatoryData.getType();
            final double latitude = lavatoryData.getLatitude();
            final double longitude = lavatoryData.getLongitude();

            ((EditText) findViewById(R.id.activity_add_lavatory_building_name))
                    .setText(building);

            ((EditText) findViewById(R.id.activity_add_lavatory_floor))
                    .setText(floor);
            ((EditText) findViewById(R.id.activity_add_lavatory_room))
                    .setText(room);

            if (type == 'M') {
                ((RadioButton) findViewById(R.id.activity_add_lavatory_male))
                        .setChecked(true);
            } else {
                ((RadioButton) findViewById(R.id.activity_add_lavatory_female))
                        .setChecked(true);
            }

            ((EditText) findViewById(R.id.activity_add_lavatory_latitude))
                    .setText(Double.toString(latitude));
            ((EditText) findViewById(R.id.activity_add_lavatory_longitude))
                    .setText(Double.toString(longitude));

        } else {
            setTitle(R.string.title_activity_add_lavatory);
        }
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
        getSupportMenuInflater().inflate(R.menu.edit_lavatory, menu);
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (mPlusClientFragment.handleOnActivityResult(requestCode, resultCode,
                data)) {
            switch (resultCode) {
            case RESULT_CANCELED:
                // User canceled sign in.
                Toast.makeText(this, R.string.google_sign_in_required,
                        Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                break;
            }
        }
    }

    // ----------------------------------------------------------------------
    // VIEW EVENT HANDLERS
    // ----------------------------------------------------------------------
    /**
     * Starts submitting the new lavatory to the LavatoryLocator service.
     *
     * @param item
     *            the {@link MenuItem} that was selected
     */
    @SuppressWarnings("rawtypes")
    public void submit(MenuItem item) {
        Log.d(TAG, "submit called");

        if (uid != null) {
            getSherlock().setProgressBarIndeterminateVisibility(true);
    
            final String building = ((EditText) findViewById(
                    R.id.activity_add_lavatory_building_name))
                    .getText().toString();
            final String floor = ((EditText) findViewById(
                    R.id.activity_add_lavatory_floor))
                    .getText().toString();
            final String room = ((EditText) findViewById(
                    R.id.activity_add_lavatory_room))
                    .getText().toString();
            final int typeRadioButtonSelected = ((RadioGroup) findViewById(
                    R.id.activity_add_lavatory_type))
                    .getCheckedRadioButtonId();
    
            char type;
            if (typeRadioButtonSelected == R.id.activity_add_lavatory_male) {
                type = 'M';
            } else {
                type = 'F';
            }
    
            final String latitudeString = ((EditText) findViewById(
                    R.id.activity_add_lavatory_latitude))
                    .getText().toString();
            final String longitudeString = ((EditText) findViewById(
                    R.id.activity_add_lavatory_longitude))
                    .getText().toString();
    
            if (!TextUtils.isEmpty(building) && !TextUtils.isEmpty(floor)
                    && !TextUtils.isEmpty(room)
                    && !TextUtils.isEmpty(latitudeString)
                    && !TextUtils.isEmpty(longitudeString)) {
                final double latitude = Double.parseDouble(latitudeString);
                final double longitude = Double.parseDouble(longitudeString);
    
                final Intent intent = getIntent();
    
                final LavatoryData lavatoryToEdit = intent
                        .getParcelableExtra(LavatoryDetailActivity.
                                LAVATORY_DATA);
    
                SpringAndroidSpiceRequest<ResponseEntity> request;
    
                if (lavatoryToEdit != null) {
                    final int lid = lavatoryToEdit.getLid();
    
                    request = new EditLavatoryDetailRequest(uid, lid, building,
                            floor, room, type, latitude, longitude);
                } else { // add a new lavatory
                    request = new AddLavatoryRequest(uid, building, floor, room,
                            type, latitude, longitude);
                }
    
                Log.d(TAG, "submit: executing Add or Edit LavatoryRequest");
                getSpiceManager().execute(request,
                        EDIT_LAVATORY_DETAIL_CACHE_KEY,
                        JSON_CACHE_DURATION, 
                        new AddOrEditLavatoryDetailRequestListener());
            } else { // some fields not filled
                Toast.makeText(this, R.string.
                        activity_edit_lavatory_detail_fields_not_filled,
                        Toast.LENGTH_SHORT).show();
            }
        }
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
     *                The connected {@link PlusClient} for making API requests.
     */
    @Override
    public void onSignedIn(PlusClient plusClient) {
        final Person user = plusClient.getCurrentPerson();
        uid = user.getId();
    }

    // -----------------------------------------------------------------
    // PRIVATE HELPER METHODS
    // -----------------------------------------------------------------
    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        Log.d(TAG, "setupActionBar called");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // ------------------------------------------------------------------
    // PRIVATE INNER CLASSES
    // ------------------------------------------------------------------
    /**
     * {@code RequestListener} for result of submitting the lavatory to the
     * LavatoryLocator service.
     *
     * @author Chris Rovillos
     *
     */
    @SuppressWarnings("rawtypes")
    private class AddOrEditLavatoryDetailRequestListener implements
            RequestListener<ResponseEntity> {
        private static final String TAG = "AddOrEditLavatory"
                + "DetailRequestListener";
        
        @Override
        public void onRequestSuccess(ResponseEntity responseEntity) {
            final String logSuccessMessage = "Review submission succeeded";
            Log.d(TAG, logSuccessMessage);

            EditLavatoryDetailActivity.this.getSherlock().
                    setProgressBarIndeterminateVisibility(false);

            Toast.makeText(EditLavatoryDetailActivity.this,
                    R.string.activity_edit_lavatory_detail_submission_success,
                    Toast.LENGTH_SHORT).show();

            finish();
        }
        
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            final String logErrorMessage = "Edit lavatory detail submission "
                    + "failed: " + spiceException.getMessage();
            Log.e(TAG, logErrorMessage);

            Toast.makeText(EditLavatoryDetailActivity.this,
                    R.string.activity_edit_lavatory_detail_submission_error,
                    Toast.LENGTH_LONG).show();
            EditLavatoryDetailActivity.this.getSherlock().
                    setProgressBarIndeterminateVisibility(false);
        }
    }

}

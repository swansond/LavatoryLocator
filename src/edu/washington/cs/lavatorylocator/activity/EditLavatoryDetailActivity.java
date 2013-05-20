package edu.washington.cs.lavatorylocator.activity;

import static junit.framework.Assert.*;
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
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import edu.washington.cs.lavatorylocator.R;
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
        JacksonSpringSpiceSherlockFragmentActivity {
    // --------------------------------------------------------------------------------------------
    // CONSTANTS
    // --------------------------------------------------------------------------------------------
    public static final String USER_ID_KEY = "uid";

    private static final int NO_ID = Integer.MIN_VALUE;

    // --------------------------------------------------------------------------------------------
    // ACTIVITY LIFECYCLE
    // --------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lavatory);
        // Show the Up button in the action bar.
        setupActionBar();

        LavatoryData lavatoryData = getIntent().getParcelableExtra(
                LavatoryDetailActivity.LAVATORY_DATA);
        if (lavatoryData != null) {
            setTitle(R.string.title_edit_lavatory_detail);

            String building = lavatoryData.getBuilding();
            String floor = lavatoryData.getFloor();
            String room = lavatoryData.getRoom();
            char type = lavatoryData.getType();
            double latitude = lavatoryData.getLatitude();
            double longitude = lavatoryData.getLongitude();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.edit_lavatory, menu);
        return true;
    }

    // --------------------------------------------------------------------------------------------
    // VIEW EVENT HANDLERS
    // --------------------------------------------------------------------------------------------
    /**
     * Starts submitting the new lavatory to the LavatoryLocator service.
     * 
     * @param item
     *            the {@link MenuItem} that was selected
     */
    public void submit(MenuItem item) {
        setProgressBarIndeterminateVisibility(true);

        String building = ((EditText) findViewById(R.id.activity_add_lavatory_building_name))
                .getText().toString();
        String floor = ((EditText) findViewById(R.id.activity_add_lavatory_floor))
                .getText().toString();
        String room = ((EditText) findViewById(R.id.activity_add_lavatory_room))
                .getText().toString();
        int typeRadioButtonSelected = ((RadioGroup) findViewById(R.id.activity_add_lavatory_type))
                .getCheckedRadioButtonId();

        char type;
        if (typeRadioButtonSelected == R.id.activity_add_lavatory_male) {
            type = 'M';
        } else {
            type = 'F';
        }

        String latitudeString = ((EditText) findViewById(R.id.activity_add_lavatory_latitude))
                .getText().toString();
        String longitudeString = ((EditText) findViewById(R.id.activity_add_lavatory_longitude))
                .getText().toString();

        if (!TextUtils.isEmpty(building) && !TextUtils.isEmpty(floor)
                && !TextUtils.isEmpty(room)
                && !TextUtils.isEmpty(latitudeString)
                && !TextUtils.isEmpty(longitudeString)) {
            double latitude = Double.parseDouble(latitudeString);
            double longitude = Double.parseDouble(longitudeString);

            Intent intent = getIntent();

            int uid = intent.getIntExtra(USER_ID_KEY, NO_ID);
            assertFalse(uid == NO_ID);

            LavatoryData lavatoryToEdit = intent
                    .getParcelableExtra(LavatoryDetailActivity.LAVATORY_DATA);

            SpringAndroidSpiceRequest<ResponseEntity> request;

            if (lavatoryToEdit != null) {
                int lid = lavatoryToEdit.getLid();

                request = new EditLavatoryDetailRequest(uid, lid, building,
                        floor, room, type, latitude, longitude);
            } else { // add a new lavatory
                request = new AddLavatoryRequest(uid, building, floor, room,
                        type, latitude, longitude);
            }

            getSpiceManager().execute(request,
                    new AddOrEditLavatoryDetailRequestListener());
        } else { // some fields not filled
            // TODO: move string resource to XML file
            String message = "You need to fill in all the fields!";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
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

    // --------------------------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    // --------------------------------------------------------------------------------------------
    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // --------------------------------------------------------------------------------------------
    // PRIVATE INNER CLASSES
    // --------------------------------------------------------------------------------------------
    /**
     * {@code RequestListener} for result of submitting the lavatory to the
     * LavatoryLocator service.
     * 
     * @author Chris Rovillos
     * 
     */
    private class AddOrEditLavatoryDetailRequestListener implements
            RequestListener<ResponseEntity> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            // TODO: move to string resources XML file
            String errorMessage = "Submission failed: "
                    + spiceException.getMessage();

            Log.e(getLocalClassName(), errorMessage);
            Toast.makeText(EditLavatoryDetailActivity.this, errorMessage,
                    Toast.LENGTH_LONG).show();
            EditLavatoryDetailActivity.this
                    .setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void onRequestSuccess(ResponseEntity responseEntity) {
            EditLavatoryDetailActivity.this
                    .setProgressBarIndeterminateVisibility(false);

            // TODO: move to string resources XML file
            String message = "Submitted!";
            Toast.makeText(EditLavatoryDetailActivity.this, message,
                    Toast.LENGTH_LONG).show();

            finish();
        }
    }

}

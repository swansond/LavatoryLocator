package edu.washington.cs.lavatorylocator.activity;

import com.google.android.gms.common.GooglePlayServicesUtil;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.washington.cs.lavatorylocator.R;

import android.os.Bundle;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

/**
 * {@link android.app.Activity} for displaying 
 * information about LavatoryLocator.
 * 
 * @author Chris Rovillos
 * 
 */
public class AboutActivity extends SherlockActivity {

    // --------------------------------------------------------
    // ACTIVITY LIFECYCLE
    // --------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // Show the Up button in the action bar.
        setupActionBar();

        final TextView googlePlayServicesAttributionTextView = 
                (TextView) findViewById(
                        R.id.google_play_services_attribution_text);
        googlePlayServicesAttributionTextView.setText(GooglePlayServicesUtil
                .getOpenSourceSoftwareLicenseInfo(getApplicationContext()));
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
        getSupportMenuInflater().inflate(R.menu.about, menu);
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
            // http://developer.android.com/design/patterns/navigation.html
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

}

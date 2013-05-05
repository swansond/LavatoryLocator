package edu.washington.cs.lavatorylocator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

/**
 * <code>Activity</code> for adding a new lavatory into LavatoryLocator.
 * 
 * @author Chris Rovillos
 * 
 */
public class AddLavatoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lavatory);
        // Show the Up button in the action bar.
        setupActionBar();
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

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_lavatory, menu);
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
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

package edu.washington.cs.lavatorylocator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class LavatoryDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lavatory_detail);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Intent intent = getIntent();
		//Bathroom bathroom = intent.getParcelableExtra(MainActivity.BATHROOM);
		Bathroom bathroom = new Bathroom(0, '0', "test", "test", new Coordinates(1,1), 0, 0);
		
		TextView lavatoryNameView = (TextView) findViewById(R.id.lavatoryName);
		String lavatoryName = bathroom.getBuilding() + ", floor: " + bathroom.getFloor();
		lavatoryNameView.setText(lavatoryName);
		
		
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
		getMenuInflater().inflate(R.menu.lavatory_detail, menu);
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

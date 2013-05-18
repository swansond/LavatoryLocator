package edu.washington.cs.lavatorylocator;

import org.apache.http.HttpStatus;

import edu.washington.cs.lavatorylocator.RESTLoader.RESTResponse;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

/**
 * <code>Activity</code> for adding a new lavatory into LavatoryLocator.
 * 
 * @author Chris Rovillos
 * 
 */
public class AddLavatoryActivity extends SherlockActivity
        implements LoaderCallbacks<RESTLoader.RESTResponse> {
  
    private static final String ADD_LAVA 
            = "http://lavlocdb.herokuapp.com/addlava.php";
    private static final int MANAGER_ID = 4;
    
    private PopupWindow connectionPopup;
    private ProgressDialog loadingScreen;

    
    //stored data in case we need to repeat a query
    private String lastUid;
    private String lastBldg;
    private String lastLavaType;
    private String lastLocLat;
    private String lastLocLong;
    private String lastFlr;
    



    /**
     * Starts submitting the new lavatory to the LavatoryLocator service.
     * 
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void addLavatory(MenuItem item) {
        String buildingName = ((EditText) findViewById(R.id.activity_add_lavatory_building_name)).getText().toString();
        String floor = ((EditText) findViewById(R.id.activity_add_lavatory_floor)).getText().toString();
        int lavaType = ((RadioGroup) findViewById(R.id.activity_add_lavatory_type)).getCheckedRadioButtonId();
        
        String lavaTypeString = "";
        
        switch (lavaType) {
            case R.id.activity_add_lavatory_male:
                lavaTypeString = "M";
                break;
            case R.id.activity_add_lavatory_female:
                lavaTypeString = "F";
                break;
        }
        
        String longitude = ((EditText) findViewById(R.id.activity_add_lavatory_longitude)).getText().toString();
        String latitude = ((EditText) findViewById(R.id.activity_add_lavatory_latitude)).getText().toString();
        
        
        requestAddLavatory(Integer.toString(1), buildingName, floor, lavaTypeString, longitude, latitude);
        
        finish();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lavatory);
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
		getSupportMenuInflater().inflate(R.menu.add_lavatory, menu);
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
	
	/**
     * Returns a new Loader to this activity's LoaderManager.
     * NOTE: We never need to call this directly as it is done automatically.
     * 
     * @author Wilkes Sunseri
     * 
     * @param id the id of the LoaderManager
     * @param args the Bundle of arguments to be passed to the Loader
     * 
     * @return A Loader to request the addition of a lavatory
     */
    @Override
    public Loader<RESTResponse> onCreateLoader(int id, Bundle args) {
        Uri searchAddress = Uri.parse(ADD_LAVA);
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
     * Sends the request to add a new lavatory to the server.
     * 
     * @author Wilkes Sunseri
     * 
     * @param uid the user's ID number
     * @param buildingName the name of the building the new lavatory is in
     * @param floor the floor the new lavatory is on
     * @param lavaType the gender of the new lavatory
     * @param longitude the new lavatory's longitude
     * @param latitude the new lavatory's latitude
     */
    private void requestAddLavatory(String uid, String buildingName,
            String floor, String lavaType, String longitude,
            String latitude) {
        //save our search params in case we need them later
        lastUid = uid;
        lastBldg = buildingName;
        lastFlr = floor;
        lastLocLong = longitude;
        lastLocLat = latitude;
        lastLavaType = lavaType;
        
        //set up the arguments
        Bundle args = new Bundle(6);
        if (!uid.equals("")) {
            args.putString("uid", uid);
        }
        if (!buildingName.equals("")) {
            args.putString("buildingName", buildingName);
        }
        if (!floor.equals("")) {
            args.putString("floor", floor);
        }
        if (!lavaType.equals("")) {
            args.putString("lavaType", lavaType);
        }
        if (!longitude.equals("")) {
            args.putString("longitude", longitude);
        }
        if (!latitude.equals("")) {
            args.putString("latitude", latitude);
        }

        loadingScreen = ProgressDialog.show(this, "Loading...",
                "Getting data just for you!", true);
        //and initialize the Loader
        getLoaderManager().initLoader(MANAGER_ID, args, this);
        }
    
    /** 
     * Retries the previous request and dismisses the popup box.
     * 
     * @author Wilkes Sunseri
     * 
     * @param target the popup box View to be dismissed
     */
    public void retryConnection(View target) {
        requestAddLavatory(lastUid, lastBldg, lastLavaType, lastLocLat,
                lastLocLong, lastFlr);
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
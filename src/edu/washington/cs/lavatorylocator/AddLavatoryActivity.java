package edu.washington.cs.lavatorylocator;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.support.v4.app.NavUtils;

/**
 * <code>Activity</code> for adding a new lavatory into LavatoryLocator.
 * 
 * @author Chris Rovillos
 * 
 */
public class AddLavatoryActivity extends SherlockActivity {
    
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
	
	//TODO: figure out where this goes and put it there
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
	    //set up the request
	    String URL = "http://lavlocdb.herokuapp.com/addlava.php";
	    HttpPost hp = new HttpPost(URL);
	    List<NameValuePair> paramList = new LinkedList<NameValuePair>();
	    if (!uid.equals("")) {
	        paramList.add(new BasicNameValuePair("uid", uid));
	    }
	    if (!buildingName.equals("")) {
	        paramList.add(new BasicNameValuePair("buildingName", buildingName));
	    }
	    if (!floor.equals("")) {
	        paramList.add(new BasicNameValuePair("floor", floor));
	    }
	    if (!lavaType.equals("")) {
	        paramList.add(new BasicNameValuePair("lavaType", lavaType));
	    }
	    if (!longitude.equals("")) {
	        paramList.add(new BasicNameValuePair("longitude", longitude));
	    }
	    if (!latitude.equals("")) {
	        paramList.add(new BasicNameValuePair("latitude", latitude));
	    }
	    //encode the parameters into the request
	    try {
            hp.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

	    //and finally pass it another string to be send to the server
	    new RequestAddLavTask().execute(hp);
	}
}
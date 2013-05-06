package edu.washington.cs.lavatorylocator;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class AddLavatoryActivity extends Activity {

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
	
	//TODO: uncomment this once the activity is reachable and this can be put in the right place
	
    //sends the information about the bathroom to be added to the server
//    private void requestAddLavatory(String uid, String buildingName,
//            String roomNumber, String floor, String lavaType, String longitude,
//            String latitude) throws UnsupportedEncodingException {
//        //set up the request
//        String URL = "http://lavlocdb.herokuapp.com/addlava.php";
//        HttpPost hp = new HttpPost(URL);
//        List<NameValuePair> paramList = new LinkedList<NameValuePair>();
//        if (!uid.equals("")) {
//            paramList.add(new BasicNameValuePair("uid", uid));
//        }
//        if (!buildingName.equals("")) {
//            paramList.add(new BasicNameValuePair("buildingName", buildingName));
//        }
//        if (!roomNumber.equals("")) {
//            paramList.add(new BasicNameValuePair("roomNumber", roomNumber));
//        }
//        if (!floor.equals("")) {
//            paramList.add(new BasicNameValuePair("floor", floor));
//        }
//        if (!lavaType.equals("")) {
//            paramList.add(new BasicNameValuePair("lavaType", lavaType));
//        }
//        if (!longitude.equals("")) {
//            paramList.add(new BasicNameValuePair("longitude", longitude));
//        }
//        if (!latitude.equals("")) {
//            paramList.add(new BasicNameValuePair("latitude", latitude));
//        }
//        hp.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
//        
//        //and finally pass it another string to be send to the server
//        new RequestAddLavTask().execute(hp);
//    }

}

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
import android.content.Intent;
import android.app.LoaderManager;
import android.content.Loader;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity 
        implements LoaderManager.LoaderCallbacks<List<LavatoryData>>{
    
    Button query;
    TextView text;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Example method calls for testing
        //TODO: move calls to the right part of the activity and delete these
        //lavatorySearch("1", "2", "3", "4", "5", "6", "7", "8", "9");
        //requestAddLavatory("1", "2", "3", "4", "5", "6");
        
//        query = (Button) findViewById(R.id.query);
//        text = (TextView) findViewById(R.id.text);
//        
//        query.setOnClickListener(new View.OnClickListener(){
//        	public void onClick(View view){
//        		text.setText("");
//        		try {
//        			query();
//        		} catch (ClientProtocolException e) {
//        			// TODO Auto-generated catch block
//        			e.printStackTrace();
//        		} catch (IOException e) {
//        			// TODO Auto-generated catch block
//        			e.printStackTrace();
//        		}
//        	}
//        });
    }
    
    //TODO: for testing before the results list is implement; remove when it is
    public void goToLavatoryDetailActivity(View view) {
        Intent intent = new Intent(this, LavatoryDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public Loader<List<LavatoryData>> onCreateLoader(int id, Bundle args) {
        return new LavSearchLoader(getApplicationContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<LavatoryData>> loader,
            List<LavatoryData> lavatories) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLoaderReset(Loader<List<LavatoryData>> loader) {
        // TODO Auto-generated method stub
        
    }
        
    //queries the server for lavatories that meet the passed parameters
    private void lavatorySearch(String bldgName, String lavaName, 
            String floor, String roomNumber, String locationLong, 
            String locationLat, String maxDist, String minRating,
            String lavaType) {
        Bundle args = new Bundle(9);

        //set up the request
        if (!bldgName.equals("")) {
            args.putString("bldgName", bldgName);
        }
        if (!floor.equals("")) {
            args.putString("floor", floor);
        }
        if (!roomNumber.equals("")) {
            args.putString("roomNumber", roomNumber);
        }
        if (!locationLong.equals("")) {
            args.putString("locationLong", locationLong);
        }
        if (!locationLat.equals("")) {
            args.putString("locationLat", locationLat);
        }
        if (!maxDist.equals("")) {
            args.putString("maxDist", maxDist);
        }
        if (!minRating.equals("")) {
            args.putString("minRating", minRating);
        }
        if (!lavaType.equals("")) {
            args.putString("lavaType", lavaType);
        }


        //and finally pass it to the loader to be sent to the server
        getLoaderManager().initLoader(0, args, this);
    }
    
    // TODO: Put back in AddLavatoryActivity
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
        //add the parameters to the request
        try {
            hp.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //and finally pass it another string to be sent to the server
        new RequestAddLavTask().execute(hp);
    }
}





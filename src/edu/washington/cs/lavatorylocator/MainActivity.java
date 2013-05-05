package edu.washington.cs.lavatorylocator;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class MainActivity extends Activity 
        implements LoaderManager.LoaderCallbacks<List<LavatoryData>>{
    
    Button query;
    TextView text;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
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
        if (!lavaName.equals("")) {
            args.putString("lavaName", lavaName);
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
        getLoaderManager().initLoader(0, args, 
                (LoaderCallbacks<List<ReviewData>>) this);
    }
    

    /*
    private void query() throws ClientProtocolException, IOException {
        //sets up httpPost to be executed by another string
        HttpPost httpPost = new HttpPost("http://lavlocdb.herokuapp.com/test.php");
        
        //adds the parameters
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        paramList.add(new BasicNameValuePair("query", "select * from team"));
        httpPost.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
        
        new DatabaseQueryTask().execute(httpPost);
    }
    
    private class DatabaseQueryTask extends AsyncTask<HttpPost, Integer, HttpResponse>{
    	
		protected HttpResponse doInBackground(HttpPost... hp) {
			HttpClient client = new DefaultHttpClient();
	    	
			//Maybe we should make the catch block look nicer. Or not. Whatever.
	    	try {
	    		return client.execute(hp[0]);
			} catch (Exception e) {
				return null;
			}
		}
		
		protected void onPostExecute(HttpResponse hr) {
			//hr should contain the data received from the database server
			//processing it is up to you
			HttpEntity entity = hr.getEntity();
			try{
				String response = EntityUtils.toString(entity);
				JSONArray js = new JSONArray(response);
				
				String display = "";
				int i = 0;
				while(!js.isNull(i)){
					JSONArray name = js.getJSONArray(i);
					for(int j = 0; j < name.length(); j++)
						display += name.getString(j) + " ";
					i++;
					display += "\n";
				}
				
				text.setText(display);
				
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "lol you goofed", Toast.LENGTH_LONG).show();
			}
		}
    } */


}





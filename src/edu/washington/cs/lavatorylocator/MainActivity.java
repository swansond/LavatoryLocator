package edu.washington.cs.lavatorylocator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
			query();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
            
    private void query() throws ClientProtocolException, IOException {
    	//sets up httpPost to be executed by another string
    	HttpPost httpPost = new HttpPost("http://lavlocdb.herokuapp.com/test.php");
    	
    	//adds the parameters
    	List<NameValuePair> paramList = new ArrayList<NameValuePair>();
    	paramList.add(new BasicNameValuePair("query", "select * from team"));
//    	paramList.add(new BasicNameValuePair("query", "insert into team values ('Scotch', 'Pilgrim')"));
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
		}
    }
}





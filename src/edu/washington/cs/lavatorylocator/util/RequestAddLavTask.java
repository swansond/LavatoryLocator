package edu.washington.cs.lavatorylocator.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

/**
 * This class sends to the server the data for the bathroom whose addition to 
 * the database is requested.
 * 
 * @author Wil
 *
 */
public class RequestAddLavTask extends AsyncTask<HttpPost, Void, HttpResponse> {

    //The server communication that occurs in the background
    @Override
    protected HttpResponse doInBackground(HttpPost... hp) {
        final HttpClient client = new DefaultHttpClient();
        try {
            return client.execute(hp[0]);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //executes when it finishes the server communication
    protected void onPostExecute(HttpResponse hr) {
        //toast goes here
    }

}

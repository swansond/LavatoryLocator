package edu.washington.cs.lavatorylocator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

/**
 * The GetUserReviewLoader class loads the user's review of the current 
 * lavatory, if one exists, from the server.
 * 
 * @author Wil
 *
 */
public class GetUserReviewLoader extends AsyncTaskLoader<List<ReviewData>>{
    private Bundle params;

    /**
     * Constructs a new GetUserReviewLoader object.
     * 
     * @param context the context of the activity that constructed this
     * @param args the query parameters
     */
    public GetUserReviewLoader(Context context, Bundle args) {
        super(context);
        params = args;
    }

    /**
     * Queries the server, parses the JSON that it gets back, and returns
     * a List containing the user's review or NULL if there isn't one.
     * 
     * @return  a List containing the user's review or NULL if it doesn't exist
     */
    @Override
    public List<ReviewData> loadInBackground() {
        HttpClient client = new DefaultHttpClient();
        String URL = "http://lavlocdb.herokuapp.com/fetchuserreview.php";
        HttpPost hp = new HttpPost(URL);
        List<NameValuePair> paramList = new LinkedList<NameValuePair>();

        //add each search parameter to the list of parameters for the request
        for (String key : params.keySet()) {
            paramList.add(new BasicNameValuePair(key, params.getString(key)));
        }
        try {
            hp.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Send the request and receive JSONs
        try {
            HttpResponse resp = client.execute(hp);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //TODO: process JSON in resp into list of reviews and return it
        return null;
    }

    /**
     * Sends the result of the query to the activity that created this object.
     * 
     * @param the List containing the user's review or NULL if there isn't one
     */
    @Override
    public void deliverResult(List<ReviewData> reviews) {
        if (isReset()) {
            return;
        } else if (isStarted()) {
            super.deliverResult(reviews);
        }
    }


    //makes the loader query the server and load the data
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    //cancels the load
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    /* stops the load if there is one and resets params
     * Note: the loader is basically useless now and this should only be called
     * when the loader doesn't need be used anymore
     */
    @Override
    protected void onReset() {
        onStopLoading();
        params = null;
    }

    //nullifies the found review list so that it can be garbage collected
    @Override
    public void onCanceled(List<ReviewData> reviews) {
        reviews = null;
    }
}

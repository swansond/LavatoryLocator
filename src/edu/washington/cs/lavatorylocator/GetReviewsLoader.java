package edu.washington.cs.lavatorylocator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

/**
 * The GetReviewsLoader class loads a page of a lavatory's reviews from the
 * server if they exist.
 * 
 * @author Wil
 * @author David Swanson
 *
 */
public class GetReviewsLoader extends AsyncTaskLoader<List<ReviewData>> {

    private Bundle params;

    /**
     * Constructs a new GetReviewsLoader object.
     * 
     * @param context the context of the activity that constructed this
     * @param args the query parameters
     */
    public GetReviewsLoader(Context context, Bundle args) {
        super(context);
        params = args;
    }

    /**
     * Queries the server, parses the JSON that it gets back, and returns
     * a List containing the a page of a lavatory's reviews or NULL if there 
     * aren't any.
     * 
     * @return  a List containing a page of a lavatory's reviews or NULL if 
     * there aren't any.
     */
    @Override
    public List<ReviewData> loadInBackground() {
        HttpClient client = new DefaultHttpClient();
        String URL = "http://lavlocdb.herokuapp.com/fetchreviews.php?";
//        HttpPost hp = new HttpPost(URL);
        List<NameValuePair> paramList = new LinkedList<NameValuePair>();

        //add each search parameter to the list of parameters for the request
        for (String key : params.keySet()) {
            paramList.add(new BasicNameValuePair(key, params.getString(key)));
        }
        String paramString = URLEncodedUtils.format(paramList, "UTF-8");
        URL += paramString;
        HttpGet hp = new HttpGet(URL);
        String finalResult = null;
        //Send the request and receive JSONs
        try {
            HttpResponse resp = client.execute(hp);
            finalResult = Parse.readJSON(resp);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            return Parse.reviewList(finalResult);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sends the result of the query to the activity that created this object.
     * 
     * @param the List containing a page of reviews or NULL if there aren't any
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
        super.onCanceled(reviews);
        reviews = null;
    }
}

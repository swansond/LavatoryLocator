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

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

/**
 * <code>Activity</code> for adding a review on a lavatory.
 * 
 * @author Chris Rovillos
 * 
 */
public class AddReviewActivity extends Activity {

    /**
     * Starts submitting the entered review to the LavatoryLocator service.
     * 
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void addReview(MenuItem item) {
        Intent intent = getIntent();
        LavatoryData ld = intent.getParcelableExtra("LAVATORY");
        
        RatingBar ratingbar = ((RatingBar) findViewById(R.id.add_review_rating));
        float rating = ratingbar.getRating();
        EditText reviewText = ((EditText) findViewById(R.id.add_review_text));
        String reviewTextString = reviewText.toString();
        
        updateReview("1", Integer.toString(ld.lavatoryID), Float.toString(rating), reviewTextString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
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
        getMenuInflater().inflate(R.menu.add_review, menu);
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

            //TODO: save entered review as a draft when user navigates away

            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sends a new review to the server.
     * 
     * @author Wilkes Sunseri
     * 
     * @param uid the ID of the user making the review
     * @param lid the ID of the lavatory being reviewed
     * @param rating the rating the user is giving the lavatory
     * @param review the review itself
     */
    private void updateReview(String uid, String lid, String rating,
            String review) {
        //set up the request
        String URL = "http://lavlocdb.herokuapp.com/submitreview.php";
        HttpPost hp = new HttpPost(URL);
        List<NameValuePair> paramList = new LinkedList<NameValuePair>();
        if (!uid.equals("")) {
            paramList.add(new BasicNameValuePair("uid", uid));
        }
        if (!lid.equals("")) {
            paramList.add(new BasicNameValuePair("lid", lid));
        }
        if (!rating.equals("")) {
            paramList.add(new BasicNameValuePair("rating", rating));
        }
        if (!review.equals("")) {
            paramList.add(new BasicNameValuePair("review", review));
        }
        //encode the parameters into the request
        try {
            hp.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //and finally pass it another string to be send to the server
        new UpdateReviewTask().execute(hp);
    }

    /**
     * This class sends the data of a new review to the server.
     * 
     * @author Wil
     *
     */
    private class UpdateReviewTask extends AsyncTask<HttpPost, Void, HttpResponse>{
       
        //The server communication that occurs in the background
        @Override
        protected HttpResponse doInBackground(HttpPost... hp) {
            HttpClient client = new DefaultHttpClient();
            try {
                return client.execute(hp[0]);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println(e);
                return null;
            }
        }
        
        //executes when it finishes the server communication
        protected void onPostExecute(HttpResponse hr) {
            Toast.makeText(AddReviewActivity.this, "test", Toast.LENGTH_SHORT);
            finish();
        }
    }
    
}

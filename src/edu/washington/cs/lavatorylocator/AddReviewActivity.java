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

public class AddReviewActivity extends Activity {

    public void addReview(MenuItem item) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        // Show the Up button in the action bar.
        setupActionBar();
        
        //Example method call for testing
        //TODO: move calls to the right part of the activity and delete this
        //updateReview("1", "2", "3", "4");

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

    //sends a new review to the server
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

        try {
            hp.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //and finally pass it another string to be send to the server
        new UpdateReviewTask().execute(hp);
    }

}

package edu.washington.cs.lavatorylocator;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.app.LoaderManager;

/**
 * Activity for viewing information about a specific lavatory.
 * 
 * @author Chris Rovillos
 * 
 */
public class LavatoryDetailActivity extends ListActivity 
        implements LoaderManager.LoaderCallbacks<List<ReviewData>> {

    /**
     * Goes to the AddReviewActivity, giving it information about the bathroom
     * being displayed.
     * 
     * @param item the MenuItem that was clicked
     */
    public void addReview(MenuItem item) {
        // TODO: pass current Bathroom object to AddReviewActivity; Bathroom needs to be made Parcelable first
        
        
        Intent intent = new Intent(this, AddReviewActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the Up button in the action bar.
        setupActionBar();

        //Example method calls for testing
        //TODO: move calls to the right part of the activity and delete these
        //getUserReview("1", "2");
        //getReviews("3", "4", "5", "6");
        
        //Intent intent = getIntent();
        //Bathroom bathroom = intent.getParcelableExtra(MainActivity.BATHROOM);
        LavatoryData testLav = new LavatoryData(1, 'M', "Mary Gates Hall", 
                "3", "rmNo", 1, 2, 5, 2.5);

        List<ReviewData> reviews = Arrays
                .asList(new ReviewData(0, 0, 0, 5, "lavatory 1"),
                new ReviewData(1, 1, 26, 1, "Bad!"),
                new ReviewData(2, 2, 97, 3, "OK."),
                new ReviewData(3, 3, 48, 5, "Amazing!"),
                new ReviewData(4, 4, 2, 4, "Good!"),
                new ReviewData(5, 5, 106, 5,
                        "Amazing! Amazing! Amazing! Amazing! Amazing! " +
                        "Amazing! Amazing! Amazing! Amazing! Amazing! " +
                        "Amazing! Amazing! Amazing! Amazing! Amazing! "));
        
        getListView().setFocusable(false); // TODO: remove when ReviewDetailActivity is implemented
        
        setTitle("Lavatory " + testLav.lavatoryID);
        
        View headerView = getLayoutInflater().inflate(
                R.layout.activity_lavatory_detail_header, null);
        ((TextView) headerView.findViewById(R.id.lavatory_detail_name_text))
                .setText("Lavatory " + testLav.lavatoryID);
        ((TextView) headerView.findViewById(R.id.lavatory_detail_building_text))
                .setText(testLav.building);
        ((RatingBar) headerView.findViewById(R.id.lavatory_detail_rating))
                .setRating((float) testLav.avgRating);
        getListView().addHeaderView(headerView, null, false);

        LavatoryDetailAdapter adapter = new LavatoryDetailAdapter(this,
                R.layout.review_row, R.id.review_author, reviews);

        getListView().setAdapter(adapter);

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
        getMenuInflater().inflate(R.menu.lavatory_detail, menu);
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

    public Loader<List<ReviewData>> onCreateLoader(int id, Bundle args) {
        
        if (id == 1) {
            return new GetReviewsLoader(getApplicationContext(), args);
        } else {
            return new GetUserReviewLoader(getApplicationContext(), args);
        }
    }

    public void onLoadFinished(Loader<List<ReviewData>> loader,
            List<ReviewData> reviews) {

        //TODO: process data
    }

    public void onLoaderReset(Loader<List<ReviewData>> loader) {
        //TODO: nullify the loader's data
    }

    //gets a page of (10) reviews for the lavatory
    private void getReviews(String lid, String pageNo, String sortparam,
            String direction) {

        Bundle args = new Bundle(4);

        //set up the request

        if (!lid.equals("")) {
            args.putString("lid", lid);
        }
        if (!pageNo.equals("")) {
            args.putString("pageNo", pageNo);
        }
        if (!sortparam.equals("")) {
            args.putString("sortparam", sortparam);
        }
        if (!direction.equals("")) {
            args.putString("direction", direction);
        }

        //and finally pass it to the loader to be sent to the server
        getLoaderManager().initLoader(1, args, this);
    }

    //gets the user's review if one exists
    private void getUserReview(String uid, String lid) {

        Bundle args = new Bundle(2);

        //set up the request
        if (!uid.equals("")) {
            args.putString("uid", uid);
        }
        if (!lid.equals("")) {
            args.putString("lid", lid);
        }
        
        //and finally pass it to the loader to be sent to the server
        getLoaderManager().initLoader(2, args, this);
    }
    
    /**
     * Adapter for displaying an array of Review objects.
     * 
     * @author Chris Rovillos
     * 
     */
    private class LavatoryDetailAdapter extends ArrayAdapter<ReviewData> {

        private List<ReviewData> reviews;

        // TODO: comments
        public LavatoryDetailAdapter(Context context, int resource,
                int textViewResourceId, List<ReviewData> objects) {
            super(context, resource, textViewResourceId, objects);
            this.reviews = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.review_row,
                        parent, false);
            }

            ((RatingBar) convertView.findViewById(R.id.review_stars))
                    .setRating(reviews.get(position).rating);
            ((TextView) convertView.findViewById(R.id.review_author))
                    .setText("User " + reviews.get(position).authorID);
            ((TextView) convertView.findViewById(R.id.review_text))
                    .setText(reviews.get(position).review);

            return convertView;
        }

    }

}

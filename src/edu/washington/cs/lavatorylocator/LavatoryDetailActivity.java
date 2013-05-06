package edu.washington.cs.lavatorylocator;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Loader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.LoaderManager;

/**
 * <code>Activity</code> for viewing information about a specific lavatory.
 * 
 * @author Chris Rovillos
 * 
 */
public class LavatoryDetailActivity extends ListActivity 
        implements LoaderManager.LoaderCallbacks<List<ReviewData>> {

    private PopupWindow popup;
    
    /**
     * Goes to the <code>AddReviewActivity</code> to allow the user to add a
     * review about the current lavatory.
     * 
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void goToAddReviewActivity(MenuItem item) {
        // TODO: pass current Bathroom object to AddReviewActivity; Bathroom
        // needs to be made Parcelable first
        SharedPreferences userDetails = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        Boolean loggedIn = userDetails.getBoolean("isLoggedIn", false);
        
        if(!loggedIn){
            LayoutInflater inflater = (LayoutInflater)
                    this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.login_popup, 
                    (ViewGroup) findViewById(R.id.login_popup_layout));
            
            popup = new PopupWindow(layout, 350, 250, true);
            popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        } else {
            Intent intent = new Intent(this, AddReviewActivity.class);
            startActivity(intent);
        }
    }
    
    /**
     * Logs the user in so that they can add missing lavatories
     * 
     * @param target
     */
    public void loginUser(View target){
        SharedPreferences userDetails = getApplicationContext().getSharedPreferences("User", MODE_PRIVATE);
        userDetails.edit().putBoolean("isLoggedIn", true).commit();
        dismissLoginPrompt(target);
    }
    
    /**
     * Closes the popup window
     * 
     * @param target
     */
    public void dismissLoginPrompt(View target){
        popup.dismiss();
    }
    
    /**
     * Goes to the <code>EditLavatoryDetailActivity</code> to allow the user to
     * edit the current lavatory's information.
     * 
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void goToEditLavatoryDetailActivity(MenuItem item) {
        // TODO: implement; remove stub message
        Context context = getApplicationContext();
        CharSequence notImplementedMessage = "Edit Lavatory Detail is not implemented yet!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, notImplementedMessage, duration);
        toast.show();
    }

    /**
     * Goes to the <code>SettingsActivity</code>.
     * 
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void goToSettingsActivity(MenuItem item) {
        // TODO: implement; remove stub message
        Context context = getApplicationContext();
        CharSequence notImplementedMessage = "Settings are not implemented yet!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, notImplementedMessage, duration);
        toast.show();
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
        
        Intent intent = getIntent();
        LavatoryData bathroom = intent.getParcelableExtra(MainActivity.LAVATORY);
        
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
        
        getListView().setFocusable(false); // TODO: remove when 
                                           //ReviewDetailActivity 
                                           //is implemented
        
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
                R.layout.review_item, R.id.review_author, reviews);

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

    /**
     * Returns a new GetReviewsLoader or GetUserReviewLoader its respective
     * LoaderManager in this activity.
     * NOTE: We never need to call this directly as it is done automatically.
     * 
     * @author Wilkes Sunseri
     * 
     * @param id the id of the LoaderManager (1 for getReviews and 2 for 
     *      getUserReview)
     * @param args the Bundle of arguments to be passed to the GetReviewsLoader
     *      or GetUserReviewLoader
     * 
     * @return A LavSearchLoader
     */
    public Loader<List<ReviewData>> onCreateLoader(int id, Bundle args) {
        if (id == 1) {
            return new GetReviewsLoader(getApplicationContext(), args);
        } else {
            return new GetUserReviewLoader(getApplicationContext(), args);
        }
    }

    /**
     * Anything that needs to be done to process a successful load's data is to
     * be done here.
     * This is called automatically when the load finishes.
     * 
     * @author Wilkes Sunseri
     * 
     * @param loader the Loader doing the loading
     * @param reviews List of ReviewData objects to be processed
     */
    public void onLoadFinished(Loader<List<ReviewData>> loader,
            List<ReviewData> reviews) {
        //TODO: process data
    }

    /**
     * Anything that needs to be done to nullify a reset Loader's data is to be
     * done here.
     * NOTE: This is called automatically when the Loader is reset.
     * 
     * @author Wilkes Sunseri
     * 
     * @param loader the Loader being reset
     */
    public void onLoaderReset(Loader<List<ReviewData>> loader) {
        //TODO: nullify the loader's data
    }

    /**
     * Gets a page of (10) reviews for the lavatory.
     * 
     * @author Wilkes Sunseri
     * 
     * @param lid the ID number for the lavatory
     * @param pageNo the page of reviews to get
     * @param sortparam the manner by which reviews are sorted
     * @param direction ascending or descending
     */
    private void getReviews(String lid, String pageNo, String sortparam,
            String direction) {
        //set up the request
        Bundle args = new Bundle(4);
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

    /**
     * Gets the user's review for this lavatory if one exists.
     * 
     * @author Wilkes Sunseri
     * 
     * @param uid ID number of the user whose reviews we're getting
     * @param lid ID number of the lavatory the review is for
     */
    private void getUserReview(String uid, String lid) {
        //set up the request
        Bundle args = new Bundle(2);
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
     * Custom <code>Adapter</code> for displaying an array of
     * <code>Review</code>s. Creates a custom <code>View</code> for each review
     * row.
     * 
     * @author Chris Rovillos
     * 
     */
    private class LavatoryDetailAdapter extends ArrayAdapter<ReviewData> {

        private List<ReviewData> reviews;

        /**
         * Constructs a new <code>LavatoryDetailAdapter</code> with given
         * <code>List</code> of <code>Review</code>s.
         * 
         * @param context
         *            the current context
         * @param reviewRowResource
         *            the resource ID for a layout file containing the review
         *            row layout to use when instantiating views
         * @param reviewAuthorTextViewResourceId
         *            the id of the <code>TextView</code> for displaying the
         *            reviewer's name in each row
         * @param reviews
         *            a <code>List</code> of <code>Review</code>s to display
         */
        public LavatoryDetailAdapter(Context context, int reviewRowResource,
                int reviewAuthorTextViewResourceId, List<ReviewData> reviews) {
            super(context, reviewRowResource, reviewAuthorTextViewResourceId,
                    reviews);
            this.reviews = reviews;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.review_item,
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

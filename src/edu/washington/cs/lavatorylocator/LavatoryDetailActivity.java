package edu.washington.cs.lavatorylocator;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

/**
 * <code>Activity</code> for viewing information about a specific lavatory.
 * 
 * @author Chris Rovillos
 * 
 */
public class LavatoryDetailActivity extends ListActivity {

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

        // Intent intent = getIntent();
        // Bathroom bathroom = intent.getParcelableExtra(MainActivity.BATHROOM);

        // TODO: for testing only; remove when networking code works
        Bathroom bathroom = new Bathroom(1, 'M', "Mary Gates Hall", "3",
                new Coordinates(1.0, 2.0), 5, 2.5);

        List<Review> reviews = Arrays
                .asList(new Review(0, 0, 0, 5, "lavatory 1"),
                        new Review(1, 1, 26, 1, "Bad!"),
                        new Review(2, 2, 97, 3, "OK."),
                        new Review(3, 3, 48, 5, "Amazing!"),
                        new Review(4, 4, 2, 4, "Good!"),
                        new Review(
                                5,
                                5,
                                106,
                                5,
                                "Amazing! Amazing! Amazing! Amazing! Amazing! "
                                        + "Amazing! Amazing! Amazing! Amazing! Amazing! "
                                        + "Amazing! Amazing! Amazing! Amazing! Amazing! "));

        getListView().setFocusable(false); // TODO: remove when
                                           // ReviewDetailActivity is
                                           // implemented

        setTitle("Lavatory " + bathroom.getBathroomID());
        
        View headerView = getLayoutInflater().inflate(
                R.layout.activity_lavatory_detail_header, null);
        ((TextView) headerView.findViewById(R.id.lavatory_detail_name_text))
                .setText("Lavatory " + bathroom.getBathroomID());
        ((TextView) headerView.findViewById(R.id.lavatory_detail_building_text))
                .setText(bathroom.getBuilding());
        ((RatingBar) headerView.findViewById(R.id.lavatory_detail_rating))
                .setRating((float) bathroom.getAverageRating());
        getListView().addHeaderView(headerView, null, false);

        LavatoryDetailAdapter adapter = new LavatoryDetailAdapter(this,
                R.layout.review_item, R.id.review_author, reviews);

        getListView().setAdapter(adapter);
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
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Custom <code>Adapter</code> for displaying an array of
     * <code>Review</code>s. Creates a custom <code>View</code> for each review
     * row.
     * 
     * @author Chris Rovillos
     * 
     */
    private class LavatoryDetailAdapter extends ArrayAdapter<Review> {

        private List<Review> reviews;

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
                int reviewAuthorTextViewResourceId, List<Review> reviews) {
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
                    .setRating(reviews.get(position).getRating());
            ((TextView) convertView.findViewById(R.id.review_author))
                    .setText("User " + reviews.get(position).getUserID());
            ((TextView) convertView.findViewById(R.id.review_text))
                    .setText(reviews.get(position).getReview());

            return convertView;
        }

    }

}

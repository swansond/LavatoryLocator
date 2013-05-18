package edu.washington.cs.lavatorylocator.activity;

import java.util.List;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.R.id;
import edu.washington.cs.lavatorylocator.R.layout;
import edu.washington.cs.lavatorylocator.R.menu;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.ReviewData;
import edu.washington.cs.lavatorylocator.util.Parse;
import edu.washington.cs.lavatorylocator.util.RESTLoader;
import edu.washington.cs.lavatorylocator.util.RESTLoader.RESTResponse;
import edu.washington.cs.lavatorylocator.util.RESTLoader.requestType;

/**
 * <code>Activity</code> for viewing information about a specific lavatory.
 *
 * @author Chris Rovillos
 *
 */
public class LavatoryDetailActivity extends SherlockFragmentActivity
        implements LoaderCallbacks<RESTLoader.RESTResponse> {

    private static final int REVIEW_MANAGER_ID = 1;
    private static final int USER_REVIEW_MANAGER_ID = 2;
    private static final String FETCH_REVIEWS
            = "http://lavlocdb.herokuapp.com/fetchreviews.php";
    private static final String FETCH_USER_REVIEW
            = "http://lavlocdb.herokuapp.com/fetchuserreview.php";

    private PopupWindow popup;
    private LavatoryData lav;
    private ProgressDialog loadingScreen;


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
            intent.putExtra("LAVATORY", lav);
            startActivityForResult(intent, 0);
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
     * the <code>MenuItem</code> that was selected
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
     * the <code>MenuItem</code> that was selected
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
        Log.d("tagged", getIntent().toString());
        if(intent.hasExtra(MainActivity.LAVATORY)){
            // called from the list of bathrooms, data is passed in
            lav = intent.getParcelableExtra(MainActivity.LAVATORY);
        } else if(savedInstanceState != null &&
                savedInstanceState.containsKey(MainActivity.LAVATORY)){
            // called from restore, get lav info from passed bundle
            lav = savedInstanceState.getParcelable(MainActivity.LAVATORY);
        } else if(getSharedPreferences("User", MODE_PRIVATE).contains("ID")){
            // called after destruction and saveInstanceState did not get called
            // have to build lavatory from data stored in onPause
            SharedPreferences data = getSharedPreferences("User", MODE_PRIVATE);
            int ID = data.getInt("ID", 0);
            char gender = data.getString("Gender", "").charAt(0); // this should get changed
            String bldg = data.getString("Building", "");
            String flr = data.getString("Floor", "");
            String rmNum = data.getString("RoomNumber", "");
            double lng = data.getFloat("Long", 0);
            double lat = data.getFloat("Lat", 0);
            int numRev = data.getInt("NumReviews", 0);
            double avg = data.getFloat("Average", 0);

            lav = new LavatoryData(ID, gender, bldg, flr, rmNum, lng, lat,
                    numRev, avg);

        } else {
            // Some bad state, do nothing and throw a null pointer exception
        }

        setContentView(R.layout.activity_lavatory_detail);

        setTitle("Lavatory " + lav.lavatoryID);

        View headerView = getLayoutInflater().inflate(
                R.layout.activity_lavatory_detail_header, null);
        ((TextView) headerView.findViewById(R.id.lavatory_detail_name_text))
                .setText("Lavatory " + lav.lavatoryID);
        ((TextView) headerView.findViewById(R.id.lavatory_detail_building_text))
                .setText(lav.building);
        ((RatingBar) headerView.findViewById(R.id.lavatory_detail_rating))
                .setRating((float) lav.avgRating);

        ListView listView = (ListView) findViewById(R.id.lavatory_detail_list_view);
        listView.addHeaderView(headerView, null, false);

        getReviews("0", Integer.toString(lav.lavatoryID), "1", "helpfulness",
                "descending");
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.lavatory_detail, menu);
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


    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.LAVATORY, lav);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        lav = (LavatoryData) savedInstanceState.get(MainActivity.LAVATORY);
    }

    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences settings = getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // save the current lavatory data
        editor.putInt("ID", lav.lavatoryID);
        editor.putString("Gender", String.valueOf(lav.lavatoryGender));
        editor.putString("Building", lav.building);
        editor.putString("Floor", lav.floor);
        editor.putString("RoomNumber", lav.roomNumber);
        editor.putFloat("Long", (float)lav.longitude);
        editor.putFloat("Lat", (float)lav.latitude);
        editor.putInt("NumReviews", lav.numReviews);
        editor.putFloat("Average", (float)lav.avgRating);
        editor.commit();
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
    @Override
    public Loader<RESTResponse> onCreateLoader(int id, Bundle args) {
        Uri searchAddress;
        if (id == REVIEW_MANAGER_ID) {
            searchAddress = Uri.parse(FETCH_REVIEWS);
        } else {
            searchAddress = Uri.parse(FETCH_USER_REVIEW);
        }
        return new RESTLoader(getApplicationContext(), searchAddress,
                RESTLoader.requestType.GET, args);
    }

    /**
     * Anything that needs to be done to process a successful load's data is to
     * be done here.
     * This is called automatically when the load finishes.
     *
     * @author Wilkes Sunseri
     *
     * @param loader the Loader doing the loading
     * @param response response from the Loader to be processed
     */
    @Override
    public void onLoadFinished(Loader<RESTLoader.RESTResponse> loader,
            RESTLoader.RESTResponse response) {
        loadingScreen.dismiss();
        if (response.getCode() == HttpStatus.SC_OK && !response.getData().equals("")) {
            try {
                JSONObject finalResult = Parse.readJSON(response);
                List<ReviewData> reviews = Parse.reviewList(finalResult);


                LavatoryDetailAdapter adapter = new LavatoryDetailAdapter(this,
                        R.layout.review_item, R.id.review_author, reviews);

                ListView listView = (ListView) findViewById(R.id.lavatory_detail_list_view);
                listView.setAdapter(adapter);
            } catch (Exception e) {
                Toast.makeText(this, "The data is ruined. I'm sorry.",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Connection failure. Try again later.",
                    Toast.LENGTH_SHORT).show();
        }
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
    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {
        //TODO: nullify the loader's data
    }

    /**
     * Gets a page of (10) reviews for the lavatory.
     *
     * @author Wilkes Sunseri
     *
     * @param uid the ID number of the user
     * @param lid the ID number for the lavatory
     * @param pageNo the page of reviews to get
     * @param sortparam the manner by which reviews are sorted
     * @param direction ascending or descending
     */
    private void getReviews(String uid, String lid, String pageNo, String sortparam,
            String direction) {
        //set up the request
        Bundle args = new Bundle(5);
        if (!uid.equals("")) {
            args.putString("uid", uid);
        }
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

        loadingScreen = ProgressDialog.show(this, "Loading...",
                "Getting data just for you!", true);
        // and finally pass it to the loader to be sent to the server
        getSupportLoaderManager().initLoader(REVIEW_MANAGER_ID, args, this);
    }

    /**
     * Gets the user's review for this lavatory if one exists.
     *
     * @author Wilkes Sunseri
     *
     * @param uid ID number of the user whose reviews we're getting
     * @param lid ID number of the lavatory the review is for
     */
    //TODO: actually call this
    private void getUserReview(String uid, String lid) {
        // set up the request
        Bundle args = new Bundle(2);
        if (!uid.equals("")) {
            args.putString("uid", uid);
        }
        if (!lid.equals("")) {
            args.putString("lid", lid);
        }

        // and finally pass it to the loader to be sent to the server
        getSupportLoaderManager().initLoader(USER_REVIEW_MANAGER_ID, args, this);
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

        private final List<ReviewData> reviews;

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

package edu.washington.cs.lavatorylocator;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class LavatoryDetailActivity extends ListActivity {
    
    /**
     * Goes to the AddReviewActivity, giving it information about the bathroom
     * being displayed.
     * 
     * @param item
     *            the MenuItem that was clicked
     */
    public void addReview(MenuItem item) {
        // TODO
        Intent intent = new Intent(this, AddReviewActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the Up button in the action bar.
        setupActionBar();

        // Intent intent = getIntent();
        // Bathroom bathroom = intent.getParcelableExtra(MainActivity.BATHROOM);
        
        //TODO: for testing only; remove when networking code works
        Bathroom bathroom = new Bathroom(1, 'M', "Mary Gates Hall", "3", new Coordinates(1.0, 2.0), 5, 2.5);
        
        List<Review> testReviews = Arrays.asList(new Review(0, 0, 0, 0, "lavatory 1"),
                new Review(1, 1, 26, 1, "Bad!"),
                new Review(2, 2, 97, 3, "OK."),
                new Review(3, 3, 48, 5, "Amazing!"),
                new Review(4, 4, 2, 4, "Good!"),
                new Review(5, 5, 106, 5, "Amazing! Amazing! Amazing! Amazing! Amazing! Amazing! Amazing! Amazing! Amazing! Amazing! Amazing! Amazing!"));
        
        View headerView = getLayoutInflater().inflate(R.layout.activity_lavatory_detail_header, null);
        ((TextView) headerView.findViewById(R.id.lavatory_detail_name_text)).setText("Lavatory " + bathroom.getBathroomID());
        ((TextView) headerView.findViewById(R.id.lavatory_detail_building_text)).setText(bathroom.getBuilding());
        ((RatingBar) headerView.findViewById(R.id.lavatory_detail_rating)).setRating((float) bathroom.getAverageRating());
        getListView().addHeaderView(headerView);
        
        LavatoryDetailAdapter adapter = new LavatoryDetailAdapter(this, android.R.layout.simple_list_item_1, R.id.review_author, testReviews);
        
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

    private class LavatoryDetailAdapter extends ArrayAdapter<Review> {

        private List<Review> reviews;
        
        public LavatoryDetailAdapter(Context context, int resource, int textViewResourceId,
                List<Review> objects) {
            super(context, resource, textViewResourceId, objects);
            this.reviews = objects;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.review_row, parent, false);
            }
            
            ((RatingBar) convertView.findViewById(R.id.review_stars)).setRating(reviews.get(position).getRating());
            ((TextView) convertView.findViewById(R.id.review_text)).setText(reviews.get(position).getReview());
            
            return convertView;
        }
        
    }

}

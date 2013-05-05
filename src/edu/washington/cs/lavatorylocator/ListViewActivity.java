package edu.washington.cs.lavatorylocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.view.View;
import android.support.v4.app.NavUtils;

/**
 * Activity for viewing search results and selecting lavatories
 * 
 * @author Keith Miller
 *
 */
public class ListViewActivity extends Activity {

    private ListView listview;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        // Show the Up button in the action bar.
        setupActionBar();
        
        listview = (ListView) findViewById(R.id.search_results);
        
        // create dummy list of search results
        // TODO: replace with actual search results once networking stuff gets finalized
        Bathroom[] results = new Bathroom[] {
            new Bathroom(1, 'M', "Mary Gates Hall", "3",
                    new Coordinates(1.0, 2.0), 5, 2.5),
            new Bathroom(2, 'F', "Mary Gates Hall", "3",
                    new Coordinates(1.0, 2.0), 20, 3),
            new Bathroom(3, 'M', "Paul G. Allen Center for Computer Science and Engineering", "B1",
                    new Coordinates(1.0, 2.0), 1, 5),
            new Bathroom(4, 'F', "Paul G. Allen Center for Computer Science and Engineering", "B1",
                    new Coordinates(1.0, 2.0), 1, 4.5),
            new Bathroom(5, 'M', "Paul G. Allen Center for Computer Science and Engineering", "6",
                    new Coordinates(1.0, 2.0), 16, 4)};
        List<Bathroom> resultsAsList = Arrays.asList(results);
        
        ResultsAdapter adapter = new ResultsAdapter(this, 
                android.R.layout.simple_list_item_1, resultsAsList);
        
        listview.setAdapter(adapter);
        
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                final Bathroom bathroom = (Bathroom) parent.getItemAtPosition(position);
                Intent intent = new Intent(parent.getContext(), LavatoryDetailActivity.class);
                //TODO: Once bathroom is Parcelable, pass it in to LavatoryDetail
                startActivity(intent);
            }
        });
        
        setTitle("Search Results");
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
        getMenuInflater().inflate(R.menu.list_view, menu);
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

    
    private class ResultsAdapter extends ArrayAdapter<Bathroom> {
        
        private List<Bathroom> bathrooms = new ArrayList<Bathroom>();
        
        public ResultsAdapter(Context context, int textViewResourceId, 
                List<Bathroom> bathrooms){
            super(context, textViewResourceId, bathrooms);
            this.bathrooms = bathrooms;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.search_result_row,
                        parent, false);
            }
            
            ((RatingBar) convertView.findViewById(R.id.avg_review_stars))
                .setRating((float)bathrooms.get(position).getAverageRating());
            
            ((TextView) convertView.findViewById(R.id.bathroom_name))
                .setText(bathrooms.get(position).getBuilding() + " " +
                bathrooms.get(position).getBathroomID() + " " +  
                bathrooms.get(position).getBathroomGender());
            
            ((TextView) convertView.findViewById(R.id.number_reviews))
                .setText("" + bathrooms.get(position).getNumberOfReviews());
            
            ((TextView) convertView.findViewById(R.id.floor))
                .setText("Floor " + bathrooms.get(position).getFloor());
            
            return convertView;
        }
    }
    
}

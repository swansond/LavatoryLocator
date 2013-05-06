package edu.washington.cs.lavatorylocator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <code>Activity</code> first displayed when LavatoryLocator is opened. Shows a
 * list of nearby lavatories.
 * 
 * @author Chris Rovillos
 * 
 */
public class MainActivity extends Activity {

    Button query;
    TextView text;

    /**
     * Activates the "Got2Go" feature, showing the user the nearest highly-rated
     * lavatory.
     * 
     * @param item
     *            the <code>MenuItem</code> that was selected
     */
    public void activateGot2go(MenuItem item) {
        // TODO: implement; remove stub message
        Context context = getApplicationContext();
        CharSequence notImplementedMessage = "Got2Go is not implemented yet!";
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

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.activity_main_search_results);

        // create dummy list of search results
        // TODO: replace with actual search results once networking stuff gets
        // finalized
        Bathroom[] results = new Bathroom[] {
                new Bathroom(1, 'M', "Mary Gates Hall", "3", new Coordinates(
                        1.0, 2.0), 5, 2.5),
                new Bathroom(2, 'F', "Mary Gates Hall", "3", new Coordinates(
                        1.0, 2.0), 20, 3),
                new Bathroom(
                        3,
                        'M',
                        "Paul G. Allen Center for Computer Science and Engineering",
                        "B1", new Coordinates(1.0, 2.0), 1, 5),
                new Bathroom(
                        4,
                        'F',
                        "Paul G. Allen Center for Computer Science and Engineering",
                        "B1", new Coordinates(1.0, 2.0), 1, 4.5),
                new Bathroom(
                        5,
                        'M',
                        "Paul G. Allen Center for Computer Science and Engineering",
                        "6", new Coordinates(1.0, 2.0), 16, 4) };
        List<Bathroom> resultsAsList = Arrays.asList(results);

        SearchResultsAdapter adapter = new SearchResultsAdapter(this,
                R.layout.search_result_item, R.id.search_result_item_lavatory_name, resultsAsList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                final Bathroom bathroom = (Bathroom) parent
                        .getItemAtPosition(position);
                Intent intent = new Intent(parent.getContext(),
                        LavatoryDetailActivity.class);
                intent.putExtra("lavatory", bathroom);
                startActivity(intent);
            }
        });
    }

    // TODO: for testing before the results list is implemented; remove when it
    // is
    public void goToSearchResultsListActivity(View view) {
        Intent intent = new Intent(this, ListViewActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Shows the search action view.
     * 
     * @param view
     *            the <code>MenuItem</code> that was selected
     */
    public void showSearchView(MenuItem item) {
        // TODO: implement; remove stub message
        Context context = getApplicationContext();
        CharSequence notImplementedMessage = "Search is not implemented yet!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, notImplementedMessage, duration);
        toast.show();
    }
}

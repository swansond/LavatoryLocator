package edu.washington.cs.lavatorylocator.test;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.test.AndroidTestCase;
import android.view.View;
import edu.washington.cs.lavatorylocator.adapter.LavatorySearchResultsAdapter;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.LavatorySearchResults;
import edu.washington.cs.lavatorylocator.model.LavatoryType;
import edu.washington.cs.lavatorylocator.view.LavatorySearchResultsListItemView;

/**
 * Tests the LavatorySearchResultsAdapter.
 * @author David Swanson
 *
 */
public class TestAdapter extends AndroidTestCase {
    private LavatorySearchResultsAdapter adapter;
    private LavatorySearchResults results;

    private static final double LATITUDE = 21.34;
    private static final double LONGITUDE = 12.34;
    private static final int ID = 5;
    private static final int REVIEW_COUNT = 2;
    private static final int LID = 0;
    private static final char TYPE = 'f';
    private static final String BUILDING = "rooftop";
    private static final String FLOOR = "1";
    private static final String ROOM = "105";
    private static final float AVG_RATING = 2.5f;
    private static final String ASSERT_MESSAGE = 
            "GetView properly returns a non-null value when input is null";
    private static final String ASSERT_MESSAGE_NEW_VIEW = 
            "GetView retains view if valid";
    /**
     * Setup method to prepare for testing.
     */
    @Override
    public void setUp() {
        final Context context = getContext();
        results = new LavatorySearchResults();
        final List<LavatoryData> list = new ArrayList<LavatoryData>();
        list.add(new LavatoryData(
                LID, LavatoryType.FEMALE, BUILDING, FLOOR, ROOM,
                LATITUDE, LONGITUDE,
                REVIEW_COUNT, AVG_RATING));
        results.setLavatories(list);
        adapter = new LavatorySearchResultsAdapter(
                context, 0, ID, results);
    }

    /**
     * Tests if the adapter can handle null values.
     * @black
     */
    public void test_getView_inputNull_getNull() {
        assertNotNull(ASSERT_MESSAGE,
                adapter.getView(0, null, null));
    }

    /**
     * Tests if the adapter returns the proper view.
     * @black
     */
    public void test_getView_usualCase_getNewView() {
        final View v = new LavatorySearchResultsListItemView(getContext());
        final View newView = adapter.getView(0, v, null);
        assertEquals(ASSERT_MESSAGE_NEW_VIEW, v, newView);
    }

}

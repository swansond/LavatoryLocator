package edu.washington.cs.lavatorylocator.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import android.test.mock.MockContext;
import android.view.View;
import edu.washington.cs.lavatorylocator.adapter.LavatorySearchResultsAdapter;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.LavatorySearchResults;
import edu.washington.cs.lavatorylocator.view.LavatorySearchResultsListItemView;

/**
 * Tests the LavatorySearchResultsAdapter.
 * @author David Swanson
 *
 */
public class TestAdapter extends TestCase {
    private LavatorySearchResultsAdapter adapter;
    private LavatorySearchResults results;
    
    private static final double TEST_DOUBLE_ONE = 21.34;
    private static final double TEST_DOUBLE_TWO = 12.34;
    private static final int TEST_INT_ONE = 5;
    private static final int TEST_INT_TWO = 2;
    
    /**
     * Setup method to prepare for testing.
     */
    public void setUp() {

        results = new LavatorySearchResults(); 
        final List<LavatoryData> list = new ArrayList<LavatoryData>();
        list.add(new LavatoryData(
                0, 'f', "rooftop", "1", "105", 
                TEST_DOUBLE_ONE, TEST_DOUBLE_TWO, 
                TEST_INT_ONE, TEST_INT_TWO));
        results.setLavatories(list);
        adapter = new LavatorySearchResultsAdapter(
                new MockContext(), 0, TEST_INT_ONE, results);
    }

    /**
     * Tests if the adapter can handle null values.
     */
    public void testGetViewNull() {
        assertNotNull("GetView properly returns " 
                + "a non-null value when input is null", 
                adapter.getView(0, null, null));
    }
    
    /**
     * Tests if the adapter returns the proper view.
     */
    public void testGetView() {
        final View v = new LavatorySearchResultsListItemView(new MockContext());
        final View newView = adapter.getView(0, v, null);
        assertEquals("GetView retains view if valid", v, newView);
    }

}

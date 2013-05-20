package edu.washington.cs.lavatorylocator.test;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import android.test.mock.MockContext;
import android.view.View;
import edu.washington.cs.lavatorylocator.adapter.LavatorySearchResultsAdapter;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.LavatorySearchResults;
import edu.washington.cs.lavatorylocator.view.LavatorySearchResultsListItemView;

public class AdapterTest {
    private LavatorySearchResultsAdapter adapter;
    LavatorySearchResults results;

    public AdapterTest() {
    }

    @Before
    public void setUp() throws Exception {

        results = new LavatorySearchResults(); 
        List<LavatoryData> list = new ArrayList<LavatoryData>();
        list.add(new LavatoryData(0, 'f', "rooftop", "1", "105", 21.34, 12.34, 5, 2));
        results.setLavatories(list);
        adapter = new LavatorySearchResultsAdapter(new MockContext(), 0, 5, results);
    }

    @Test
    public void testGetViewNull() {
        assertNotNull("GetView properly returns a non-null value when input is null", 
                adapter.getView(0, null, null));
    }
    
    @Test
    public void testGetView() {
        View v = new LavatorySearchResultsListItemView(new MockContext());
        View newView = adapter.getView(0, v, null);
        assertEquals("GetView retains view if valid", v, newView);
    }

}

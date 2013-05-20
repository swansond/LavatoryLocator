package edu.washington.cs.lavatorylocator.test;

import junit.framework.TestCase;
import android.os.Bundle;
import edu.washington.cs.lavatorylocator.model.LavatoryData;

/**
 * Tests for the main activity.
 * @author David Swanson
 *
 */
public class TestMainActivity extends TestCase {

    /**
     * Required constructor.
     */
    public TestMainActivity() {
    }
    
    /**
     * Basic test for testing testing.
     */
    public void testCreation() {
        assertTrue("Hello" != null);
    }
    
    /**
     * Showing how to bundle.
     */
    public void dummyShowOffBundle() {
        final Bundle b = new Bundle();
        final LavatoryData c = new LavatoryData();
        b.putParcelable("one", c);
        final LavatoryData d = b.getParcelable("one");
        assertTrue(c == d);
    }
    
    
    

}

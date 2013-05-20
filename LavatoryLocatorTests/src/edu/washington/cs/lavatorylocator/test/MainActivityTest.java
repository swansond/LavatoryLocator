package edu.washington.cs.lavatorylocator.test;

import edu.washington.cs.lavatorylocator.activity.MainActivity;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Tests for the main activity.
 * @author David Swanson
 *
 */
public class MainActivityTest extends 
        ActivityInstrumentationTestCase2<MainActivity> {

    /**
     * Required constructor.
     */
    public MainActivityTest() {
        super(MainActivity.class);
    }
    
    /**
     * Basic test for testing testing.
     */
    public void testCreation() {
        assertTrue("Hello" != null);
    }
    
    
    

}

package edu.washington.cs.lavatorylocator.test;

import edu.washington.cs.lavatorylocator.activity.MainActivity;
import android.test.ActivityInstrumentationTestCase2;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        //MainActivity main = getActivity();
        
    }
    
    public void testCreation() {
        assertTrue("Hello" != null);
    }
    
    

}

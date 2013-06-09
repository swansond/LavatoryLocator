package edu.washington.cs.lavatorylocator.test;

import junit.framework.TestCase;
import edu.washington.cs.lavatorylocator.network.Got2goRequest;

/**
 * Tests Got2go functionality.
 * @author David Swanson
 * @black
 */
public class TestGot2go extends TestCase {
    
    
    private static final double LAT = 0.0;
    private static final double LNG = 0.0;
    private Got2goRequest gtg;

    /**
     * Tests constructor.
     */
    public void testConstructor() {
        gtg = new Got2goRequest(LAT, LNG);
        assertNotNull(gtg);
    }
}

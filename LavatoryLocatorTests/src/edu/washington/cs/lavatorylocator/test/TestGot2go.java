package edu.washington.cs.lavatorylocator.test;

import junit.framework.TestCase;
import edu.washington.cs.lavatorylocator.network.Got2goRequest;

/**
 * Tests Got2go functionality.
 * @author David Swanson
 * @black
 */
public class TestGot2go extends TestCase {
    
    
    private double lat;
    private double lng;
    private Got2goRequest gtg;

    /**
     * Tests constructor.
     */
    public void testConstructor() {
        lat = 0.0;
        lng = 0.0;
        gtg = new Got2goRequest(lat, lng);
        assertNotNull(gtg);
    }
}

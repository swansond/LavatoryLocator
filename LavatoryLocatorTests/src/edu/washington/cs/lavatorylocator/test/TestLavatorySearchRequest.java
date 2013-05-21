package edu.washington.cs.lavatorylocator.test;

import edu.washington.cs.lavatorylocator.network.LavatorySearchRequest;
import junit.framework.TestCase;

/**
 * Tests LavatorySearchRequest class.
 * @author root
 *
 */
public class TestLavatorySearchRequest extends TestCase {

    private LavatorySearchRequest lsr;
    private final String building = "CSE";
    private final String floor = "B";
    private final String room = "B100";
    private final double minRating = 1.0;
    private final String type = "M";
    private final String latitude = "42.42";
    private final String longitude = "42.42";
    private final String radius = "1.0";

    /**
     * Tests constructor.
     * @black
     */
    public void test_constructor_fullArgs_notNull() {
        lsr = new LavatorySearchRequest(building, floor, room, minRating,
                type, latitude, longitude, radius);
        assertNotNull(lsr);
    }

}

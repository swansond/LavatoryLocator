package edu.washington.cs.lavatorylocator.test;

import junit.framework.TestCase;
import edu.washington.cs.lavatorylocator.network.GetLavatoryReviewsRequest;

/**
 * Tests for GetLavatoryReviewsRequest class.
 * @author David Swanson
 * @black
 */
public class TestGetReviewsRequest extends TestCase {

    private GetLavatoryReviewsRequest glr;
    private int uid;
    private int lid;
    
    /**
     * Tests constructor.
     */
    public void testConstructor() {
        uid = 0;
        lid = 0;
        glr = new GetLavatoryReviewsRequest(
                "" + uid, "" + lid, "0", "head", "ascending");
        assertNotNull(glr);
    }
}


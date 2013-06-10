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
    private static final int UID = 0;
    private static final int LID = 0;
    private static final String SORTPARAM = "head";
    private static final String SORTDIR = "ascending";
    
    /**
     * Tests constructor.
     */
    public void testConstructor() {
        glr = new GetLavatoryReviewsRequest(
                "" + UID, "" + LID, 0, SORTPARAM, SORTDIR);
        assertNotNull(glr);
    }
}


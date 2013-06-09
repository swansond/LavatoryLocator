package edu.washington.cs.lavatorylocator.test;

import edu.washington.cs.lavatorylocator.network.UpdateHelpfulnessRequest;
import junit.framework.TestCase;


/**
 * Tests for the UpdateHelpfulnessRequest class.
 * 
 * @author Wilkes Sunseri
 *
 */
public class TestUpdateHelpfulnessRequest extends TestCase {
    
    private static final String USERNAME = "-1";
    private static final String UID = "-1";
    private static final int REVIEW_ID = 1;
    private static final int HELPFUL = -1;
    
    
    /**
     * Tests the constructor.
     * @black
     */
    public void test_constructor_usualCase_newRequest() {
        final UpdateHelpfulnessRequest dlr = 
                new UpdateHelpfulnessRequest(USERNAME, UID, REVIEW_ID, 
                                             HELPFUL);
        assertNotNull(dlr);
    }
}

package edu.washington.cs.lavatorylocator.test;

import edu.washington.cs.lavatorylocator.network.AddReviewRequest;
import junit.framework.TestCase;

/**
 * Tests for the AddReviewRequest class.
 * 
 * @author Wilkes Sunseri
 *
 */
public class TestAddReviewRequest extends TestCase {
    
    /**
     * Tests constructor.
     * @black
     */
    public void test_constructor_usualCase_newRequest() {
        final AddReviewRequest arr = new AddReviewRequest("", "", 0, 0f,
                "");
        assertNotNull(arr);
    }
}

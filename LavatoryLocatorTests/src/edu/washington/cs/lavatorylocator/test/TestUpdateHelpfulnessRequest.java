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
    /**
     * Tests the constructor.
     * @black
     */
    public void test_constructor_usualCase_newRequest() {
        final UpdateHelpfulnessRequest dlr = new UpdateHelpfulnessRequest("-1",
                "-1", -1, -1);
        assertNotNull(dlr);
    }
}

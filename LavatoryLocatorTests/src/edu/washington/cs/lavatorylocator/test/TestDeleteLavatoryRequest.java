package edu.washington.cs.lavatorylocator.test;

import edu.washington.cs.lavatorylocator.network.DeleteLavatoryRequest;
import junit.framework.TestCase;

/**
 * Tests for the DeleteLavatoryRequest class.
 * 
 * @author Wilkes Sunseri
 *
 */
public class TestDeleteLavatoryRequest extends TestCase {

    /**
     * Tests the constructor.
     * @black
     */
    public void test_constructor_usualCase_newRequest() {
        final DeleteLavatoryRequest dlr = new DeleteLavatoryRequest(-1, "-1");
        assertNotNull(dlr);
    }
}

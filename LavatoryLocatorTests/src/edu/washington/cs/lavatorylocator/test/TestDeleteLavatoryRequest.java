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

    private static final int LID = -1;
    private static final String UID = "-1";
    
    /**
     * Tests the constructor.
     * @black
     */
    public void test_constructor_usualCase_newRequest() {
        final DeleteLavatoryRequest dlr = new DeleteLavatoryRequest(LID, UID);
        assertNotNull(dlr);
    }
}

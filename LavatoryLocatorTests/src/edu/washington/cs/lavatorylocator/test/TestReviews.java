package edu.washington.cs.lavatorylocator.test;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.lavatorylocator.model.ReviewData;
import edu.washington.cs.lavatorylocator.model.Reviews;
import junit.framework.TestCase;

/**
 * Tests Reviews class.
 * @author Aasav Prakash
 *
 */
public class TestReviews extends TestCase {

    private Reviews revs;

    /**
     * Test the basic getting and setting.
     * @black
     */
    public void test_gettersSetters_usualCase_expectedValues() {
        revs = new Reviews();
        final List<ReviewData> expected = new LinkedList<ReviewData>();
        revs.setReviews(expected);
        assertEquals(expected, revs.getReviews());
    }

}

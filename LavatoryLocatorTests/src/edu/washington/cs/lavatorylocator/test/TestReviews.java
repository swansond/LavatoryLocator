package edu.washington.cs.lavatorylocator.test;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.lavatorylocator.model.ReviewData;
import edu.washington.cs.lavatorylocator.model.Reviews;
import junit.framework.TestCase;

public class TestReviews extends TestCase {

	private Reviews revs;
	
	/**
	 * Test the basic getting and setting
	 */
	public void testGetAndSet() {
		revs = new Reviews();
		List<ReviewData> expected = new LinkedList<ReviewData>();
		revs.setReviews(expected);
		assertEquals(expected, revs.getReviews());
	}
	
}

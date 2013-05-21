package edu.washington.cs.lavatorylocator.test;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.LavatorySearchResults;

/**
 * Tests LavatorySearchResults class.
 * @author Aasav Prakash
 *
 */
public class TestLavatorySearchResults extends TestCase {

    /**
     * Test basic get and set.
     */
    public void testGetAndSet() {
        final LavatorySearchResults lsr = new LavatorySearchResults();
        final List<LavatoryData> exp = new LinkedList<LavatoryData>();
        lsr.setLavatories(exp);
        assertEquals(exp, lsr.getLavatories());
    }
    
}

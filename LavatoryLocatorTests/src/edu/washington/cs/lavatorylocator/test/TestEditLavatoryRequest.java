package edu.washington.cs.lavatorylocator.test;

import junit.framework.TestCase;
import edu.washington.cs.lavatorylocator.model.LavatoryType;
import edu.washington.cs.lavatorylocator.network.EditLavatoryDetailRequest;

/**
 * Tests for EditLavatoryRequest class.
 * @author David Swanson
 * @black
 */
public class TestEditLavatoryRequest extends TestCase {

    private EditLavatoryDetailRequest eldr;
    private static final String UID = "0";
    private static final int LID = 0;
    private static final String BUILDING = "CSE";
    private static final String FLOOR = "B";
    private static final String ROOM = "B100";
    private static final LavatoryType TYPE = LavatoryType.MALE;
    private static final double LATITUDE = 23.45;
    private static final double LONGITUDE = 12.34;
    
    /**
     * Tests constructor.
     */
    public void testConstructor() {
        eldr = new EditLavatoryDetailRequest(UID, LID, BUILDING, FLOOR, 
                ROOM, TYPE, LATITUDE, LONGITUDE);
        assertNotNull(eldr);
    }
}

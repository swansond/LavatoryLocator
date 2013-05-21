package edu.washington.cs.lavatorylocator.test;

import junit.framework.TestCase;
import edu.washington.cs.lavatorylocator.network.EditLavatoryDetailRequest;

/**
 * Tests for EditLavatoryRequest class.
 * @author David Swanson
 * @black
 */
public class TestEditLavatoryRequest extends TestCase {

    private EditLavatoryDetailRequest eldr;
    private int uid;
    private int lid;
    private String building;
    private String floor;
    private String room;
    private char type;
    private double latitude;
    private double longitude;
    
    /**
     * Tests constructor.
     */
    public void testConstructor() {
        uid = 0;
        lid = 0;
        building = "CSE";
        floor = "B";
        room = "B100";
        type = 'M';
        latitude = 00.00;
        longitude = 00.00;
        eldr = new EditLavatoryDetailRequest(uid, lid, building, floor, 
                room, type, latitude, longitude);
        assertNotNull(eldr);
    }
}

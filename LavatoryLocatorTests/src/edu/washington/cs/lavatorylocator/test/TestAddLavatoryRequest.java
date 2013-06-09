package edu.washington.cs.lavatorylocator.test;

import edu.washington.cs.lavatorylocator.model.LavatoryType;
import edu.washington.cs.lavatorylocator.network.AddLavatoryRequest;
import junit.framework.TestCase;

/**
 * Tests for AddLavatoryRequest class.
 * @author Aasav Prakash
 *
 */
public class TestAddLavatoryRequest extends TestCase {

    private AddLavatoryRequest alr;
    private String uid;
    private String building;
    private String floor;
    private String room;
    private LavatoryType type;
    private double latitude;
    private double longitude;

    /**
     * Tests constructor.
     * @black
     */
    public void test_constructor_usualCase_newRequest() {
        uid = "0";
        building = "CSE";
        floor = "B";
        room = "B100";
        type = LavatoryType.MALE;
        latitude = 00.00;
        longitude = 00.00;
        alr = new AddLavatoryRequest(uid, building, floor, room, type,
                latitude, longitude);
        assertNotNull(alr);
    }
}

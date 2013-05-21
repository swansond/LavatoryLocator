package edu.washington.cs.lavatorylocator.test;

import android.os.Bundle;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import junit.framework.TestCase;

/**
 * Tests LavatoryData class.
 * @author Aasav Prakash
 *
 */
public class TestLavatoryData extends TestCase {

    private LavatoryData ld;
    private final int lid = 1;
    private final char type = 'M';
    private final String building = "CSE";
    private final String floor = "B";
    private final String room = "B100";
    private final double latitude = 42.42;
    private final double longitude = 42.42;
    private final int reviews = 1;
    private final float avgRating = (float) 3.14;

    /**
     * Tests the basic constructor.
     * @black
     */
    public void testBasicConstructor() {
        ld = new LavatoryData();
        assertNotNull(ld);
    }

    /**
     * Tests the full constructor and getters.
     * @black
     */
    public void testFullConstructor() {
        ld = new LavatoryData(lid, type, building, floor, room,
                    latitude, longitude, reviews, avgRating);
        assertNotNull(ld);
        assertEquals(lid, ld.getLid());
        assertEquals(type, ld.getType());
        assertEquals(building, ld.getBuilding());
        assertEquals(floor, ld.getFloor());
        assertEquals(room, ld.getRoom());
        assertEquals(latitude, ld.getLatitude());
        assertEquals(longitude, ld.getLongitude());
        assertEquals(reviews, ld.getReviews());
        assertEquals(avgRating, ld.getAvgRating());
    }

    /**
     * Tests that the constructor doesn't return null.
     * @black
     */
    public void test_constructor_noArgs_notNull() {
        ld = new LavatoryData();
        assertNotNull(ld);
    }

    /**
     * Tests the setters.
     * @black
     */
    public void test_setters_usualCase_getExpected() {
        final LavatoryData expected = new LavatoryData(lid, type, building,
                floor, room, latitude, longitude, reviews, avgRating);

        ld = new LavatoryData();
        ld.setLid(lid);
        ld.setType(type);
        ld.setBuilding(building);
        ld.setFloor(floor);
        ld.setRoom(room);
        ld.setLatitude(latitude);
        ld.setLongitude(longitude);
        ld.setReviews(reviews);
        ld.setAvgRating(avgRating);
        assertTrue(expected.getAvgRating() == ld.getAvgRating()
                && expected.getBuilding().equals(ld.getBuilding())
                && expected.getFloor().equals(ld.getFloor())
                && expected.getRoom().equals(ld.getRoom())
                && expected.getLatitude() == ld.getLatitude()
                && expected.getLongitude() == ld.getLongitude()
                && expected.getLid() == ld.getLid()
                && expected.getReviews() == ld.getReviews()
                && expected.getType() == ld.getType());
    }

    /**
     * Tests the built name.
     * @white
     */
    public void testGetName() {
        ld = new LavatoryData(lid, type, building, floor, room,
                    latitude, longitude, reviews, avgRating);
        final String name = "Room " + room + ", " + type + ", Floor " + floor
                + ", " + building;
        assertEquals(name, ld.getName());
    }

    /**
     * Test parceling.
     * @black
     */
    public void testParcelable() {
        final Bundle b = new Bundle();
        final int testLid = 403;
        final LavatoryData c = new LavatoryData();
        c.setLid(testLid);
        b.putParcelable("one", c);
        final LavatoryData d = b.getParcelable("one");
        assertEquals(testLid, d.getLid());
    }

}

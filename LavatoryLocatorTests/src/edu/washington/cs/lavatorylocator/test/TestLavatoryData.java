package edu.washington.cs.lavatorylocator.test;

import android.os.Bundle;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import junit.framework.TestCase;

public class TestLavatoryData extends TestCase {
	
	private LavatoryData ld;
	private int lid;
    private char type;
    private String building;
    private String floor;
    private String room;
    private double latitude;
    private double longitude;
    private int reviews;
    private float avgRating;
	
    /**
     * Tests the basic constructor
     */
	public void testBasicConstructor() {
		ld = new LavatoryData();
		assertNotNull(ld);
	}
	
	/**
	 * Tests the full constructor and getters.
	 */
	public void testFullConstructor() {
		lid = 1;
		type = 'M';
		building = "CSE";
		floor = "B";
		room = "B100";
		latitude = 42.42;
		longitude = 42.42;
		reviews = 1;
		avgRating = (float) 3.14;
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
	 * Tests the setters
	 */
	public void testSetters() {
		lid = 1;
		type = 'M';
		building = "CSE";
		floor = "B";
		room = "B100";
		latitude = 42.42;
		longitude = 42.42;
		reviews = 1;
		avgRating = (float) 3.14;
		ld = new LavatoryData();
		assertNotNull(ld);
		ld.setLid(lid);
		ld.setType(type);
		ld.setBuilding(building);
		ld.setFloor(floor);
		ld.setRoom(room);
		ld.setLatitude(latitude);
		ld.setLongitude(longitude);
		ld.setReviews(reviews);
		ld.setAvgRating(avgRating);
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
	 * Tests the built name
	 */
	public void testGetName() {
		lid = 1;
		type = 'M';
		building = "CSE";
		floor = "B";
		latitude = 42.42;
		longitude = 42.42;
		reviews = 1;
		avgRating = (float) 3.14;
		ld = new LavatoryData(lid, type, building, floor, room,
					latitude, longitude, reviews, avgRating);
		String name = "Room " + room + ", " + type + ", Floor " + floor
                + ", " + building;
		assertEquals(name, ld.getName());
	}
	
	/**
	 * Test parceling
	 */
	public void testParcelable() {
		Bundle b = new Bundle();
		int testLid = 403;
        LavatoryData c = new LavatoryData();
        c.setLid(testLid);
        b.putParcelable("one", c);
        LavatoryData d = b.getParcelable("one");
        assertEquals(testLid, d.getLid());
	}
	
}

package edu.washington.cs.lavatorylocator;

/**
 * The Coordinates class is a simple immutable data type representing the
 * longitude and latitude of a point.
 * 
 * @author Wil
 *
 */
class Coordinates {
	private double longitude;
	private double latitude;
	
	/**
	 * Constructs a new Coordinates object at longitude x and latitude y.
	 * 
	 * @param x the longitude of the location
	 * @param y the latitude of the location
	 */
	public Coordinates(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	/**
	 * Gets the longitude of this Coordinates object.
	 * 
	 * @return the longitude of this Coordinates object
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Gets the latitude of this Coordinates object.
	 * 
	 * @return the latitude of this Coordinates object.
	 */
	public double getLatitude() {
		return latitude;
	}
}

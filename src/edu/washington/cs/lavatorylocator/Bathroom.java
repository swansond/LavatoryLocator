package edu.washington.cs.lavatorylocator;

/**
 * The Bathroom class is an immutable representation of the data related to
 * any particular bathroom. This includes its id number in the database, the 
 * gender who can use it, the building it's in as well as the floor it's on and
 * its latitude and longitude, its average rating, and the number of reviews for
 * that bathroom.
 * 
 * @author Wil
 *
 */
public class Bathroom {
	private final int bathroomID;
	private final char bathroomGender;
	
	private final String building;
	private final String floor;
	private final Coordinates location;
	
	private final int numReviews;
	private final double avgRating;

	/**
	 * Constructs a new Bathroom object.
	 * 
	 * @param id the bathroom's id number in the database
	 * @param gender the gender allowed to use the bathroom (M/F/U)
	 * @param build the building the bathroom is in
	 * @param flr the floor the bathroom is on
	 * @param lon the longitude of the bathroom
	 * @param lat the latitude of the bathroom
	 * @param numRev the number of reviews the bathroom has
	 */
	public Bathroom(int id, char gender, String build, String flr,
						Coordinates loc, int numRev, double rating) {
		bathroomID = id;
		bathroomGender = gender;
		building = build;
		floor = flr;
		location = loc;
		numReviews = numRev;
		avgRating = rating;
	}
	
	/**
	 * Gets this bathroom's id number in the database
	 * 
	 * @return this bathroom's id number in the database
	 */
	public int getBathroomID() {
		return bathroomID;
	}

	/**
	 * Gets the gender allowed to use this bathroom
	 * 
	 * @return the char M if this bathroom is for males,<br>
	 * the char F if this bathroom is for females,<br>
	 * the char U if this bathroom is unisex
	 */
	public char getBathroomGender() {
		return bathroomGender;
	}

	/**
	 * Gets the name of the building this bathroom is in
	 * 
	 * @return the name of the building this bathroom is in
	 */
	public String getBuilding() {
		return building;
	}

	/**
	 * Gets the floor this bathroom is on
	 * 
	 * @return the floor this bathroom is on
	 */
	public String getFloor() {
		return floor;
	}

	/**
	 * Gets the longitude of this bathroom
	 * 
	 * @return the longitude of this bathroom
	 */
	public float getLongitude() {
		return location.getLongitude();
	}

	/**
	 * Gets the latitude of this bathroom
	 * 
	 * @return the latitude of this bathroom
	 */
	public float getLatitude() {
		return location.getLatitude();
	}

	/**
	 * Gets the number of reviews this bathroom has received
	 * 
	 * @return the number of reviews this bathroom has received
	 */
	public int getNumberOfReviews() {
		return numReviews;
	}
	
	/**
	 * Gets the average rating of this bathroom
	 * 
	 * @return the average rating of this bathroom
	 */
	public double getAverageRating() {
		return avgRating;
	}
	
	/**
	 * Gets a page of reviews from the server.
	 * 
	 * @param pageNo the page number to get reviews for
	 * @param category the category being used to order reviews
	 */
	public void getReviews(int pageNo, String category) {
		ServerInterfacer.getReviews(bathroomID, pageNo, category);
	}
}

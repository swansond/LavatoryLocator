package edu.washington.cs.lavatorylocator;

/**
 * The LavatoryData class is an immutable representation of the data related to
 * any particular lavatory. This includes its id number in the database, the 
 * gender who can use it, the building it's in as well as the floor it's on and
 * its room number, its latitude and longitude, the number of reviews for that 
 * lavatory, and its average rating.
 * 
 * @author Wil
 *
 */
public class LavatoryData {
    public final int lavatoryID;
    public final char lavatoryGender;
    
    public final String building;
    public final String floor;
    public final String roomNumber;
    public final double longitude;
    public final double latitude;
    
    public final int numReviews;
    public final double avgRating;

    /**
     * Constructs a new LavatoryData object .
     * 
     * @param id the lavatory's id number in the database
     * @param gender the gender allowed to use the lavatory (M/F/U)
     * @param bldg the building the lavatory is in
     * @param flr the floor the lavatory is on
     * @param rmNo the lavatory's room number
     * @param lng the longitude of the lavatory
     * @param lat the latitude of the lavatory
     * @param numRev the number of reviews the lavatory has
     * @rating the average rating of the lavatory
     */
    public LavatoryData(int id, char gender, String bldg, String flr,
            String rmNo, double lng, double lat, int numRev, double rating) {
        lavatoryID = id;
        lavatoryGender = gender;
        building = bldg;
        floor = flr;
        roomNumber = rmNo;
        longitude = lng;
        latitude = lat;        
        numReviews = numRev;
        avgRating = rating;
    }
}

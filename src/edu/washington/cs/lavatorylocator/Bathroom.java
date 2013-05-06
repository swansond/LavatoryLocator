package edu.washington.cs.lavatorylocator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The Bathroom class is an immutable representation of the data related to
 * any particular bathroom. This includes its id number in the database, the 
 * gender who can use it, the building it's in as well as the floor it's on and
 * its latitude and longitude, its average rating, and the number of reviews for
 * that bathroom.
 * 
 * @author Wil
 * @author David Swanson
 *
 */
public class Bathroom implements Parcelable {
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
     * Constructs a new Bathroom object from a Parcel.
     * This needs to match the order that the fields are written.
     * 
     * @param in the parcel representing the bathroom
     * {@link #writeToParcel(Parcel, int)} 
     */
    public Bathroom(Parcel in) {
        bathroomID = in.readInt();
        bathroomGender = (char)in.readInt();
        building = in.readString();
        floor = in.readString();
        location = new Coordinates(in.readDouble(), in.readDouble());
        numReviews = in.readInt();
        avgRating = in.readDouble();      
    }
    
    /**
     * The CREATOR is a static field that allows for serialization of Bathrooms.  
     */
    public static final Creator<Bathroom> CREATOR = new Creator<Bathroom>() {

        /**
         * createFromParcel is used to create a new Bathroom object when needed.
         */
        @Override
        public Bathroom createFromParcel(Parcel source) {
            return new Bathroom(source);
        }

        /**
         * This is an unused method for when making a Bathroom array is necessary.
         */
        @Override
        public Bathroom[] newArray(int size) {
            return new Bathroom[size];
        }
    };
    
    /**
     * This method is unused, but required for the Parcelable interface. 
     * If this class is subclassed, the value will need to be increased.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Takes the fields of the Bathroom objects and writes them into the Parcel.
     * It is imperative that the order here matches the order in the constructor.
     * 
     * @param out the destination Parcel
     * @param flags unused flags for special cases
     * {@link #Bathroom(Parcel)}
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(bathroomID);
        out.writeInt(bathroomGender);
        out.writeString(building);
        out.writeString(floor);
        out.writeDouble(location.getLongitude());
        out.writeDouble(location.getLatitude());
        out.writeInt(numReviews);
        out.writeDouble(avgRating);        
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
    public double getLongitude() {
        return location.getLongitude();
    }

    /**
     * Gets the latitude of this bathroom
     * 
     * @return the latitude of this bathroom
     */
    public double getLatitude() {
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

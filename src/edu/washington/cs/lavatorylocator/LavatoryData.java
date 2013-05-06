package edu.washington.cs.lavatorylocator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The LavatoryData class is an immutable representation of the data related to
 * any particular lavatory. This includes its id number in the database, the 
 * gender who can use it, the building it's in as well as the floor it's on and
 * its room number, its latitude and longitude, the number of reviews for that 
 * lavatory, and its average rating.
 * 
 * @author Wilkes Sunseri
 * @author David Swanson
 */
public class LavatoryData implements Parcelable {
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
     * The CREATOR is a static field that allows for serialization of Bathrooms.  
     */
    public static final Creator<LavatoryData> CREATOR = new Creator<LavatoryData>() {

        /**
         * createFromParcel is used to create a new Bathroom object when needed.
         */
        @Override
        public LavatoryData createFromParcel(Parcel source) {
            return new LavatoryData(source);
        }

        /**
         * This is an unused method for when making a Bathroom array is necessary.
         */
        @Override
        public LavatoryData[] newArray(int size) {
            return new LavatoryData[size];
        }
    };

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
    
    /**
     * Constructs a new LavatoryData object.
     * @param in the Parcel that represents a LavatoryData object
     */
    public LavatoryData(Parcel in) {
        lavatoryID = in.readInt();
        lavatoryGender = (char)in.readInt();
        building = in.readString();
        floor = in.readString();
        roomNumber = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();        
        numReviews = in.readInt();
        avgRating = in.readDouble();
    }
    
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
     * {@link #LavatoryData(Parcel)}
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(lavatoryID);
        out.writeInt(lavatoryGender);
        out.writeString(building);
        out.writeString(floor);
        out.writeString(roomNumber);
        out.writeDouble(longitude);
        out.writeDouble(latitude);
        out.writeInt(numReviews);
        out.writeDouble(avgRating);
    }
}

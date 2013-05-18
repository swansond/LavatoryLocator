package edu.washington.cs.lavatorylocator.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The {@link LavatoryData} class is a representation of the data related to any
 * particular lavatory. This includes its ID in the database, type, building,
 * floor, room latitude and longitude, review count, and average rating.
 * 
 * @author Wilkes Sunseri
 * @author David Swanson
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LavatoryData implements Parcelable {
    // --------------------------------------------------------------------------------------------
    // INSTANCE VARIABLES
    // --------------------------------------------------------------------------------------------
    private int lid;

    private char type;

    private String building;
    private String floor;
    private String room;

    private double latitude;
    private double longitude;

    private int reviews;
    private double avgRating;

    /**
     * @return the lid
     */
    public int getLid() {
        return lid;
    }

    /**
     * @param lid
     *            the lid to set
     */
    public void setLid(int lid) {
        this.lid = lid;
    }

    /**
     * @return the reviews
     */
    public int getReviews() {
        return reviews;
    }

    /**
     * @param reviews
     *            the reviews to set
     */
    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(char type) {
        this.type = type;
    }

    /**
     * @param building
     *            the building to set
     */
    public void setBuilding(String building) {
        this.building = building;
    }

    /**
     * @param floor
     *            the floor to set
     */
    public void setFloor(String floor) {
        this.floor = floor;
    }

    /**
     * @param room
     *            the room to set
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * @param latitude
     *            the latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @param longitude
     *            the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * @param avgRating
     *            the avgRating to set
     */
    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    // --------------------------------------------------------------------------------------------
    // CONSTRUCTORS AND CREATORS
    // --------------------------------------------------------------------------------------------
    /**
     * {@link Creator} that allows for serialization of {@link LavatoryData}
     * objects.
     */
    public static final Creator<LavatoryData> CREATOR = new Creator<LavatoryData>() {
        /**
         * Constructs a new {@link LavatoryData} object from a {@link Parcel}.
         * 
         * @param the
         *            {@link Parcel} that represents a {@link LavatoryData}
         *            object
         */
        @Override
        public LavatoryData createFromParcel(Parcel source) {
            return new LavatoryData(source);
        }

        /**
         * Unused method.
         * 
         * @throws {@link UnsupportedOperationException}
         */
        @Override
        public LavatoryData[] newArray(int size) {
            throw new UnsupportedOperationException();
        }
    };

    /**
     * Constructs a new empty {@link LavatoryData} object.
     */
    public LavatoryData() {
    }

    /**
     * Constructs a new {@link LavatoryData} object.
     * 
     * @param id
     *            the lavatory's id number in the database
     * @param gender
     *            the gender allowed to use the lavatory (M/F/U)
     * @param bldg
     *            the building the lavatory is in
     * @param flr
     *            the floor the lavatory is on
     * @param rmNo
     *            the lavatory's room number
     * @param lng
     *            the longitude of the lavatory
     * @param lat
     *            the latitude of the lavatory
     * @param numRev
     *            the number of reviews the lavatory has
     * @param rating
     *            the average rating of the lavatory
     */
    public LavatoryData(int id, char type, String building, String floor,
            String room, double latitude, double longitude, int reviewCount,
            double avgRating) {
        this.lid = id;
        this.type = type;
        this.building = building;
        this.floor = floor;
        this.room = room;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reviews = reviewCount;
        this.avgRating = avgRating;
    }

    /**
     * Constructs a new {@link LavatoryData} object from a {@link Parcel}.
     * 
     * @param the
     *            {@link Parcel} that represents a {@link LavatoryData} object
     */
    public LavatoryData(Parcel source) {
        lid = source.readInt();
        type = (char) source.readInt();
        building = source.readString();
        floor = source.readString();
        room = source.readString();
        longitude = source.readDouble();
        latitude = source.readDouble();
        reviews = source.readInt();
        avgRating = source.readDouble();
    }

    // --------------------------------------------------------------------------------------------
    // GETTERS
    // --------------------------------------------------------------------------------------------

    /**
     * Returns the lavatory ID in the database.
     * 
     * @return the lavatory ID in the database
     */
    public int getLavatoryID() {
        return lid;
    }

    /**
     * Returns the type of this lavatory (e.g., male, female, etc.)
     * 
     * @return the type of this lavatory
     */
    public char getType() {
        return type;
    }

    /**
     * Returns the building that contains this lavatory.
     * 
     * @return the building that contains this lavatory
     */
    public String getBuilding() {
        return building;
    }

    /**
     * Returns the floor that contains this lavatory
     * 
     * @return the floor that contains this lavatory
     */
    public String getFloor() {
        return floor;
    }

    /**
     * Returns this lavatory’s room.
     * 
     * @return this lavatory’s room
     */
    public String getRoom() {
        return room;
    }

    /**
     * Returns this lavatory’s latitude.
     * 
     * @return this lavatory’s latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns this lavatory’s longitude.
     * 
     * @return this lavatory’s longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns the number of reviews for this lavatory.
     * 
     * @return the number of reviews for this lavatory
     */
    public int getReviewCount() {
        return reviews;
    }

    /**
     * Returns this lavatory’s average rating.
     * 
     * @return this lavatory’s average rating
     */
    public double getAvgRating() {
        return avgRating;
    }

    // --------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    // --------------------------------------------------------------------------------------------

    /**
     * Takes the fields of this object and writes them into the {@link Parcel}.
     * It is imperative that the order here matches the order in the
     * constructor.
     * 
     * @param out
     *            the destination Parcel
     * @param flags
     *            unused flags for special cases {@link #LavatoryData(Parcel)}
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(lid);
        out.writeInt(type);
        out.writeString(building);
        out.writeString(floor);
        out.writeString(room);
        out.writeDouble(longitude);
        out.writeDouble(latitude);
        out.writeInt(reviews);
        out.writeDouble(avgRating);
    }

    // --------------------------------------------------------------------------------------------
    // UNUSED
    // --------------------------------------------------------------------------------------------

    /**
     * This method is unused, but required for the {@link Parcelable} interface.
     * If this class is subclassed, the value will need to be increased.
     */
    @Override
    public int describeContents() {
        return 0;
    }
}

package edu.washington.cs.lavatorylocator.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.util.Log;

/**
 * The {@link Review} class is an representation of the data related to any one
 * review. This includes its database ID number, the database ID number of the
 * user who wrote it, the database ID number of the bathroom it is a review of,
 * the rating it gives, and the review itself. that bathroom.
 *
 * @author Wil Sunseri
 * @author Chris Rovillos
 * @author David Swanson
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewData {

    // -----------------------------------------------------------
    // INSTANCE VARIABLES
    // -----------------------------------------------------------
    private int lid;
    private int uid;
    private String datetime;
    private String review;
    private float rating;
    private int helpfulness;
    private int uservote;
    private int reviewId;

    
    private static final int DATE_TIME_OFFSET = 3;
    // -----------------------------------------------------------
    // CONSTRUCTORS AND CREATORS
    // -----------------------------------------------------------
    /**
     * Constructs an empty {@link ReviewData} object.
     */
    public ReviewData() {
    }

    // -----------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------
    /**
     * Returns this review's author.
     *
     * @return this review's author
     */
    public String getAuthor() {
        return "User " + uid;
    }

    /**
     * Returns this review's date and time.
     *
     * @return this review's date and time, as represented in a String
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * Returns this review's helpfulness.
     *
     * @return this review's helpfulness
     */
    public int getHelpfulness() {
        return helpfulness;
    }

    /**
     * Returns this review's lavatory ID, as stored in the LavatoryLocator
     * service.
     *
     * @return this review's lavatory ID, as stored in the LavatoryLocator
     *         service
     */
    public int getLid() {
        return lid;
    }

    /**
     * Returns this review's rating.
     *
     * @return this review's rating
     */
    public float getRating() {
        return rating;
    }

    /**
     * Returns this review's text.
     *
     * @return this review's text
     */
    public String getReview() {
        return review;
    }

    /**
     * Returns this review's uservote.
     *
     * @return this review's uservote
     */
    public int getUservote() {
        return uservote;
    }

    /**
     * Returns this review's ID number.
     *
     * @return this review's ID number.
     */
    public int getRid() {
        return reviewId;
    }

    // --------------------------------------------------------------
    // SETTERS
    // --------------------------------------------------------------
    /**
     * Sets this review's date and time.
     *
     * @param datetime
     *            this review's date and time, as represented in a String
     */
    public void setDatetime(String datetime) {
        // In format: YYYY-MM-DD HH:MM:SS:JJJJJJ
        // J seems to be fractions of a second
        // Out format: MM/DD/YYYY HH:MMXM without 0 padding
        try {
            Log.e("datetime", datetime);
            SimpleDateFormat format = 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            final Date time = format.parse(datetime.substring(
                    0, datetime.length() - DATE_TIME_OFFSET));
            Log.e("converted", time.toString());
            format = new SimpleDateFormat("M/d/yyyy h:mm a");
            format.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            this.datetime = format.format(time);
        } catch (ParseException e) {
            this.datetime = "Date Unavailable";
        }
    }

    /**
     * Sets this review's helpfulness.
     *
     * @param helpfulness
     *            this review's helpfulness
     */
    public void setHelpfulness(int helpfulness) {
        this.helpfulness = helpfulness;
    }

    /**
     * Sets this review's lavatory ID, as stored in the LavatoryLocator service.
     *
     * @param lid
     *            this review's lavatory ID, as stored in the LavatoryLocator
     *            service
     */
    public void setLid(int lid) {
        this.lid = lid;
    }

    /**
     * Sets this review's rating.
     *
     * @param rating
     *            this review's rating
     */
    public void setRating(float rating) {
        this.rating = rating;
    }

    /**
     * Sets this review's text.
     *
     * @param review
     *            this review's text
     */
    public void setReview(String review) {
        this.review = review;
    }

    /**
     * Sets this review's author ID, as stored in the LavatoryLocator service.
     *
     * @param uid
     *            this review's author ID, as stored in the LavatoryLocator
     *            service
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     * Sets this review's uservote.
     *
     * @param uservote
     *            this review's uservote
     */
    public void setUservote(int uservote) {
        this.uservote = uservote;
    }

    /**
     * Sets this review's ID number.
     *
     * @param reviewId
     *            this review's ID number, as represented in an int
     */
    public void setRid(int reviewId) {
        this.reviewId = reviewId;
    }

}

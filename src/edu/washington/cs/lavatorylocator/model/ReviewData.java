package edu.washington.cs.lavatorylocator.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.content.Context;
import android.util.Log;
import edu.washington.cs.lavatorylocator.R;

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

    private static final String DATE_INPUT = "yyyy-MM-dd HH:mm:ss";
    // -----------------------------------------------------------
    // INSTANCE VARIABLES
    // -----------------------------------------------------------
    private int lid;
    private String uid;
    private String username;
    private Date datetime;
    private String review;
    private float rating;
    private int helpfulness;
    private int totalVotes;
    private int uservote;
    private int reviewId;

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
        return username;
    }

    /**
     * Returns this review's date and time.
     *
     * @param c the Context this ReviewData belongs to
     * @return this review's date and time, as represented in a String
     */
    public String getDatetime(Context c) {
        if (datetime == null) {
            return c.getString(R.string.date_unavailable);
        }
        final SimpleDateFormat format = new SimpleDateFormat(c.getString(
                R.string.date_output));
        format.setTimeZone(TimeZone.getDefault());
        return format.format(datetime);
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
     * Returns this review's total number of helpfulness votes.
     * 
     * @return this reviews vote count
     */
    public int getTotalVotes() {
        return totalVotes;
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
            final SimpleDateFormat format = 
                    new SimpleDateFormat(DATE_INPUT);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            final int index = datetime.indexOf('.');
            String tempDatetime = datetime;
            if (index != -1) {
                tempDatetime = datetime.substring(0, index);
            }
            this.datetime = format.parse(tempDatetime);
        } catch (ParseException e) {
            this.datetime = null;
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
     * Sets this review's totalVotes.
     * 
     * @param totalVotes
     *            this review's total vote count
     */
    public void setTotalvotes(int totalvotes) {
        this.totalVotes = totalvotes;
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
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Sets this review's author displayed name, as stored in the
     * LavatoryLocator service.
     * 
     * @param username
     *            this review's author displayed name, as stored in the
     *            LavatoryLocator service
     */
    public void setUsername(String username) {
        this.username = username;
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

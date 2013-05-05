package edu.washington.cs.lavatorylocator;

/**
 * The Review class is an immutable representation of the data related to
 * any one review. This includes its database ID number, the database ID number
 * of the user who wrote it, the database ID number of the bathroom it is a
 * review of, the rating it gives, and the review itself.
 * that bathroom.
 * 
 * @author Wil
 *
 */
public class ReviewData {
    public final int reviewID;
    public final int authorID;
    public final int bathroomID;
    public final int rating;
    public final String review;
    
    
    public ReviewData(int reviewID, int authorID, int bathroomID, int rating,
            String review) {
        this.reviewID = reviewID;
        this.authorID = authorID;
        this.bathroomID = bathroomID;
        this.rating = rating;
        this.review = review;
    }
}

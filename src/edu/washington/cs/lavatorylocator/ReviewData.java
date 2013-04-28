package edu.washington.cs.lavatorylocator;

/**
 * The ReviewData class is an immutable representation of the data related to
 * any one review. This includes its database ID number, the database ID number
 * of the user who wrote it, the database ID number of the bathroom it is a
 * review of, the rating it gives, and the review itself.
 * that bathroom.
 * 
 * @author Wil
 *
 */
public class ReviewData {
	private final int reviewID;
	private final int authorID;
	private final int bathroomID;
	private final int rating;
	private final String review;
	
	
	public ReviewData(int reviewID, int authorID, int bathroomID, int rating,
					  String review) {
		this.reviewID = reviewID;
		this.authorID = authorID;
		this.bathroomID = bathroomID;
		this.rating = rating;
		this.review = review;
	}
	
	/**
	 * Gets the database ID number of this review.
	 * 
	 * @return the the ID of this review in the database
	 */
	public int getReviewID() {
		return reviewID;
	}
	
	/**
	 * Gets the database ID number of the user who wrote this review.
	 * 
	 * @return the the database ID number of the user who wrote this review
	 */
	public int getUserID() {
		return authorID;
	}
	
	/**
	 * Gets the database ID number of the reviewed lavatory.
	 * 
	 * @return the the database ID number of the reviewed lavatory
	 */
	public int getBathroomID() {
		return bathroomID;
	}

	/**
	 * Gets the rating this review gives.
	 * 
	 * @return the rating this review gives
	 */
	public int getRating() {
		return rating;
	}	
	
	/**
	 * Gets this review's review text.
	 * 
	 * @return this review's review text
	 */
	public String getReview() {
		return review;
	}
	
	public void markAsHelpful(int userID) {
		ServerInterfacer.markAsHelpful(userID, reviewID);
	}
}

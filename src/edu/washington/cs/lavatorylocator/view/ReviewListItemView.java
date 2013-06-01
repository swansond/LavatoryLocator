package edu.washington.cs.lavatorylocator.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.model.ReviewData;

/**
 * This class controls the way that reviews are shown.
 * @author Chris Rovillos
 */
public class ReviewListItemView extends RelativeLayout {

    private TextView reviewAuthorTextView;
    private RatingBar ratingBar;
    private TextView reviewTextView;
    private TextView promptHelpfulText;
    private Button markHelpfulButton;
    private Button markNotHelpfulButton;
    private TextView reviewDateTextView;

    /**
     * Constructs a new {@link LavatorySearchResultsListItemView} with the given
     * context.
     *
     * @param context
     *            the context in which this method was called
     */
    public ReviewListItemView(Context context) {
        super(context);
        inflateView(context);
    }

    /**
     * Updates the view with the new review.
     * @param review
     *              The review to add to the view.
     */
    public void updateView(ReviewData review) {
        final String reviewAuthor = review.getAuthor();
        final float rating = review.getRating();
        final String reviewText = review.getReview();
        final int reviewId = review.getRid();
        final String datetime = review.getDatetime();

        reviewAuthorTextView.setText(reviewAuthor);
        ratingBar.setRating(rating);
        reviewTextView.setText(reviewText);
        reviewDateTextView.setText(datetime);
        
        // Set up the call-backs for the helpfulness buttons
        markHelpfulButton.setTag(new Integer(reviewId));
        markHelpfulButton.setId(reviewId);
        markNotHelpfulButton.setTag(new Integer(reviewId));
        markNotHelpfulButton.setId(reviewId);
        if (review.getUservote() != 0) {
            disableHelpfulnessButtons();
        }
    }

    /**
     * Inflates this {@link LavatorySearchResultsListItemView} from the XML
     * layout resource.
     *
     * @param context
     *            the context
     */
    private void inflateView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.review_item, this);

        reviewAuthorTextView = ((TextView) findViewById(R.id.review_author));
        ratingBar = ((RatingBar) findViewById(R.id.review_stars));
        reviewTextView = ((TextView) findViewById(R.id.review_text));
        promptHelpfulText = ((TextView) findViewById(R.id.was_helpful_text));
        markHelpfulButton = ((Button) findViewById(R.id.was_helpful_button));
        markNotHelpfulButton = ((Button) findViewById(
                R.id.was_not_helpful_button));
        reviewDateTextView = ((TextView) findViewById(R.id.review_datetime));
    }

    /**
     * Disables this object's helpfullness rating buttons.
     */
    public final void disableHelpfulnessButtons() {
        markHelpfulButton.setEnabled(false);
        markNotHelpfulButton.setEnabled(false);
    }
}

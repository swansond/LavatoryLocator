package edu.washington.cs.lavatorylocator.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.model.ReviewData;

public class ReviewListItemView extends RelativeLayout {

    private TextView reviewAuthorTextView;
    private RatingBar ratingBar;
    private TextView reviewTextView;

    /**
     * Constructs a new {@link LavatorySearchResultsListItemView} with the given
     * context.
     * 
     * @param context
     *            the context
     */
    public ReviewListItemView(Context context) {
        super(context);
        inflateView(context);
    }

    public void updateView(ReviewData review) {
        final String reviewAuthor = review.getAuthor();
        final float rating = review.getRating();
        final String reviewText = review.getReview();

        reviewAuthorTextView.setText(reviewAuthor);
        ratingBar.setRating(rating);
        reviewTextView.setText(reviewText);
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
    }
}

package edu.washington.cs.lavatorylocator.view;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.model.LavatoryData;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Custom list item for displaying lavatory search results.
 * 
 * @author Chris Rovillos
 * 
 */
public class LavatorySearchResultsListItemView extends RelativeLayout {

    private TextView lavatoryNameTextView;
    private RatingBar averageRatingBar;
    private TextView reviewCountTextView;
    private TextView floorTextView;
    private TextView buildingTextView;

    /**
     * Constructs a new {@link LavatorySearchResultsListItemView} with the given
     * context.
     * 
     * @param context
     *            the context
     */
    public LavatorySearchResultsListItemView(Context context) {
        super(context);
        inflateView(context);
    }

    /**
     * Updates the lavatory displayed in this view.
     * 
     * @param lavatoryData
     *            the lavatory to display
     */
    public void updateView(LavatoryData lavatoryData) {
        final String name = lavatoryData.getName();
        final float avgRating = (float) lavatoryData.getAvgRating();
        final int reviewCount = lavatoryData.getReviews();
        final String floor = lavatoryData.getFloor();
        final String building = lavatoryData.getBuilding();
        
        final String floorTextPrefix = getContext().getString(R.string.
                floor_prefix);

        lavatoryNameTextView.setText(name);
        averageRatingBar.setRating(avgRating);

        // need to convert the count into a String; otherwise it is mistaken for
        // a resource id (which is an int)
        reviewCountTextView.setText(Integer.toString(reviewCount));

        floorTextView.setText(floorTextPrefix + floor);
        buildingTextView.setText(building);
    }

    /**
     * Inflates this {@link LavatorySearchResultsListItemView} from the XML
     * layout resource.
     * 
     * @param context
     *            the context
     */
    private void inflateView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.search_result_item, this);

        lavatoryNameTextView = ((TextView) findViewById(
                R.id.search_result_item_lavatory_name));
        averageRatingBar = ((RatingBar) findViewById(
                R.id.search_result_item_average_review));
        reviewCountTextView = ((TextView) findViewById(
                R.id.search_result_item_review_count));
        floorTextView = ((TextView) findViewById(
                R.id.search_result_item_floor));
        buildingTextView = ((TextView) findViewById(
                R.id.search_result_item_building));
    }
}

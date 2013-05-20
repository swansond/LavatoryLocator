package edu.washington.cs.lavatorylocator.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.washington.cs.lavatorylocator.model.ReviewData;
import edu.washington.cs.lavatorylocator.view.ReviewListItemView;

/**
 * Custom {@link Adapter} for displaying an array of{@link ReviewData}
 * objects. Inflates the custom {@link View} for each row.
 * 
 * @author Chris Rovillos
 * 
 */
public class ReviewsListAdapter extends ArrayAdapter<ReviewData> {
    
    /**
     * Constructs a new {@code ReviewsListAdapter} with the given
     * {@link List} of {@link ReviewData} objects.
     * 
     * @param context
     *            the current context
     * @param reviewRowResource
     *            the resource ID for a layout file containing the ReviewData
     *            row layout to use when instantiating views
     * @param reviewAuthorTextViewResourceId
     *            the id of the {@link TextView} for displaying the
     *            review author in each row
     * @param reviews
     *            a {@link List} of {@link ReviewData} objects to display
     */
    public ReviewsListAdapter(Context context, int reviewRowResourceId,
            int reviewAuthorTextViewResourceId, List<ReviewData> reviews) {
        super(context, reviewRowResourceId, reviewAuthorTextViewResourceId,
                reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        ReviewData currentItem = getItem(position);
        if (convertView != null) {
            view = convertView;
        } else {
            view = new ReviewListItemView(getContext());
        }

        ((ReviewListItemView) view).updateView(currentItem);

        return view;
    }
}
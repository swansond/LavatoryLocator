package edu.washington.cs.lavatorylocator.adapter;

import java.util.Collection;

import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.LavatorySearchResults;
import edu.washington.cs.lavatorylocator.view.LavatorySearchResultsListItemView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Custom {@link Adapter} for displaying an array of{@link LavatoryData}
 * objects. Inflates the custom {@link View} for each row.
 * 
 * @author Keith Miller
 * @author Chris Rovillos
 * 
 */
public class LavatorySearchResultsAdapter extends ArrayAdapter<LavatoryData> {

    /**
     * Constructs a new {@code LavatorySearchResultsAdapter} with no data.
     * 
     * @param context
     *            the current context
     * @param resultRowResourceId
     *            the resource ID for a layout file containing the LavatoryData
     *            row layout to use when instantiating views
     * @param lavatoryDataNameTextViewResourceId
     *            the id of the {@link TextView} for displaying the
     *            LavatoryData's name in each row
     */
    public LavatorySearchResultsAdapter(Context context,
            int resultRowResourceId, int lavatoryDataNameTextViewResourceId) {
        super(context, resultRowResourceId, lavatoryDataNameTextViewResourceId);
    }

    /**
     * Constructs a new <code>LavatorySearchResultsAdapter</code> with given
     * <code>List</code> of <code>LavatoryData</code>s.
     * 
     * @param context
     *            the current context
     * @param resultRowResourceId
     *            the resource ID for a layout file containing the LavatoryData
     *            row layout to use when instantiating views
     * @param lavatoryDataNameTextViewResourceId
     *            the id of the {@link TextView} for displaying the
     *            LavatoryData's name in each row
     * @param reviews
     *            a {@link List} of {@link ReviewData} objects to display
     */
    public LavatorySearchResultsAdapter(Context context,
            int resultRowResourceId, int lavatoryDataNameTextViewResourceId,
            LavatorySearchResults lavatorySearchResults) {
        super(context, resultRowResourceId, lavatoryDataNameTextViewResourceId,
                lavatorySearchResults.getLavatories());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        LavatoryData currentItem = getItem(position);
        if (convertView != null) {
            view = convertView;
        } else {
            view = new LavatorySearchResultsListItemView(getContext());
        }

        ((LavatorySearchResultsListItemView) view).updateView(currentItem);

        return view;
    }

    /**
     * This method exists in Android 3.0 and above, but not older versions.
     * 
     * @see android.widget.ArrayAdapter#addAll(java.util.Collection)
     */
    @Override
    public void addAll(Collection<? extends LavatoryData> collection) {
        for (LavatoryData lavatoryData : collection) {
            add(lavatoryData);
        }
    }

    /**
     * Improves {@link ListView} performance while scrolling.
     * <p>
     * See <a href=
     * "http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder"
     * >the Android developer documentation</a> for more information.
     */
    static class ViewHolder {
        TextView lavatoryNameTextView;
        RatingBar averageRatingBar;
        TextView reviewCountTextView;
        TextView floorTextView;
    }
}
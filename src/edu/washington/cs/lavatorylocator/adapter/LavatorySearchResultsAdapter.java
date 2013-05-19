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
 * Custom <code>Adapter</code> for displaying an array of
 * <code>LavatoryData</code>s. Creates a custom <code>View</code> for each
 * lavatory row.
 * 
 * @author Keith Miller
 * 
 */
public class LavatorySearchResultsAdapter extends ArrayAdapter<LavatoryData> {

    public LavatorySearchResultsAdapter(Context context, int resultRowResourceId, int lavatoryDataNameTextViewResourceId) {
        super(context, resultRowResourceId, lavatoryDataNameTextViewResourceId);
    }
    
    /**
     * Constructs a new <code>LavatorySearchResultsAdapter</code> with given
     * <code>List</code> of <code>LavatoryData</code>s.
     * 
     * @param context
     *            the current context
     * @param resultRowResource
     *            the resource ID for a layout file containing the LavatoryData
     *            row layout to use when instantiating views
     * @param LavatoryDataNameTextViewResourceId
     *            the id of the <code>TextView</code> for displaying the
     *            LavatoryData's name in each row
     * @param reviews
     *            a <code>List</code> of <code>Review</code>s to display
     */
    public LavatorySearchResultsAdapter(Context context, int resultRowResourceId,
            int lavatoryDataNameTextViewResourceId,
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
    
    // needed for because versions below Android 3.0 don't have this method
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
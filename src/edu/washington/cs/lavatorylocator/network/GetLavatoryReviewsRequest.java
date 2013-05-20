package edu.washington.cs.lavatorylocator.network;

import android.net.Uri;

import com.octo.android.robospice.request.
        springandroid.SpringAndroidSpiceRequest;

import edu.washington.cs.lavatorylocator.model.Reviews;

public class GetLavatoryReviewsRequest extends 
        SpringAndroidSpiceRequest<Reviews> {

    private static final String FETCH_REVIEWS_SERVICE_URL = 
            "http://lavlocdb.herokuapp.com/fetchreviews.php";
    
    private static final String LAVATORY_ID_SERVER_KEY = "lid";
    private static final String USER_ID_SERVER_KEY = "uid";
    private static final String PAGE_SERVER_KEY = "pageNo";
    private static final String SORT_PARAM_SERVER_KEY = "sortparam";
    private static final String SORT_DIRECTION_SERVER_KEY = "direction";
    
    private String lavatoryId;
    private String userId;
    private String page;
    private String sortParam;
    private String sortDirection;

    public GetLavatoryReviewsRequest(
            String userId, String lavatoryId, String page, 
            String sortParam, String sortDirection) {
        super(Reviews.class);
        
        this.lavatoryId = lavatoryId;
        this.userId = userId;
        this.page = page;
        this.sortParam = sortParam;
        this.sortDirection = sortDirection;
    }

    @Override
    public Reviews loadDataFromNetwork() throws Exception {
        final Uri.Builder uriBuilder = Uri.parse(FETCH_REVIEWS_SERVICE_URL)
                .buildUpon();
        
        uriBuilder.appendQueryParameter(LAVATORY_ID_SERVER_KEY, lavatoryId);
        uriBuilder.appendQueryParameter(USER_ID_SERVER_KEY, userId);
        uriBuilder.appendQueryParameter(PAGE_SERVER_KEY, page);
        uriBuilder.appendQueryParameter(SORT_PARAM_SERVER_KEY, sortParam);
        uriBuilder.appendQueryParameter(
                SORT_DIRECTION_SERVER_KEY, sortDirection);
        
        final String url = uriBuilder.build().toString();
        
        return getRestTemplate().getForObject(url, Reviews.class);
    }
}

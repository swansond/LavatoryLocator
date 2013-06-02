package edu.washington.cs.lavatorylocator.network;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.
        MappingJacksonHttpMessageConverter;

import android.net.Uri;

import com.octo.android.robospice.request.
        springandroid.SpringAndroidSpiceRequest;

import edu.washington.cs.lavatorylocator.model.Reviews;

/**
 * Class for getting reviews from the LavatoryLocator service.
 * 
 * @author Chris Rovillos
 * 
 */
public class GetLavatoryReviewsRequest extends 
        SpringAndroidSpiceRequest<Reviews> {

    private static final String FETCH_REVIEWS_SERVICE_URL = 
            "http://lavlocdb.herokuapp.com/fetchreviews.php";
    
    private static final String LAVATORY_ID_SERVER_KEY = "lid";
    private static final String USER_ID_SERVER_KEY = "uid";
    private static final String PAGE_SERVER_KEY = "pageNo";
    private static final String SORT_PARAM_SERVER_KEY = "sortparam";
    private static final String SORT_DIRECTION_SERVER_KEY = "direction";

    private String lid;
    private String uid;
    private int page;
    private String sortParam;
    private String sortDirection;

    /**
     * Creates a new {@link GetLavatoryReviewsRequest} with the given
     * parameters.
     * 
     * @param uid
     *            the user ID of the user requesting the reviews
     * @param lid
     *            the ID of the lavatory for which to get reviews
     * @param page
     *            the page of reviews to load
     * @param sortParam
     *            the sort parameter
     * @param sortDirection
     *            the sort direction
     */
    public GetLavatoryReviewsRequest(
            String uid, String lid, int page, 
            String sortParam, String sortDirection) {
        super(Reviews.class);

        this.lid = lid;
        this.uid = uid;
        this.page = page;
        this.sortParam = sortParam;
        this.sortDirection = sortDirection;
    }

    @Override
    public Reviews loadDataFromNetwork() throws Exception {
        final Uri.Builder uriBuilder = Uri.parse(FETCH_REVIEWS_SERVICE_URL)
                .buildUpon();

        uriBuilder.appendQueryParameter(LAVATORY_ID_SERVER_KEY, lid);
        uriBuilder.appendQueryParameter(USER_ID_SERVER_KEY, uid);
        uriBuilder.appendQueryParameter(PAGE_SERVER_KEY,
                Integer.toString(page));
        uriBuilder.appendQueryParameter(SORT_PARAM_SERVER_KEY, sortParam);
        uriBuilder.appendQueryParameter(
                SORT_DIRECTION_SERVER_KEY, sortDirection);
        
        // set the message converters for the request
        final List<HttpMessageConverter<?>> msgConverters =
                new ArrayList<HttpMessageConverter<?>>();
        final HttpMessageConverter<?> mappingJacksonConverter =
                new MappingJacksonHttpMessageConverter();
        msgConverters.add(mappingJacksonConverter);
        getRestTemplate().setMessageConverters(msgConverters);
        
        final String url = uriBuilder.build().toString();
        
        return getRestTemplate().getForObject(url, Reviews.class);
    }
}

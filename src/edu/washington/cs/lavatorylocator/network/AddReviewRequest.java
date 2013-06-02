package edu.washington.cs.lavatorylocator.network;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.octo.android.robospice.request.springandroid.
        SpringAndroidSpiceRequest;

/**
 * Class for submitting a lavatory review to the LavatoryLocator service.
 * 
 * @author Chris Rovillos
 * 
 */
@SuppressWarnings("rawtypes")
public class AddReviewRequest extends
        SpringAndroidSpiceRequest<ResponseEntity> {
    private static final String SUBMIT_REVIEW_SERVICE_URL =
            "http://lavlocdb.herokuapp.com/submitreview.php";

    private static final String USER_ID_SERVER_KEY = "uid";
    private static final String LAVATORY_ID_SERVER_KEY = "lid";
    private static final String RATING_SERVER_KEY = "rating";
    private static final String REVIEW_TEXT_SERVER_KEY = "review";
    private static final String USER_NAME_SERVER_KEY = "username";

    private String username;
    private String uid;
    private int lid;
    private float rating;
    private String reviewText;

    /**
     * Constructs a new {@link AddReviewRequest} with the given parameters.
     * 
     * @param username
     *            the username of the user submitting the review
     * @param uid
     *            the user ID of the user submitting the lavatory review
     * @param lid
     *            the ID of the lavatory being reviewed
     * @param rating
     *            the rating of the lavatory
     * @param reviewText
     *            the text of the lavatory review
     */
    public AddReviewRequest(String username, String uid, int lid, float rating,
            String reviewText) {
        super(ResponseEntity.class);

        this.uid = uid;
        this.lid = lid;
        this.rating = rating;
        this.reviewText = reviewText;
        this.username = username;
    }

    @Override
    public ResponseEntity loadDataFromNetwork() throws Exception {
        final String ratingString = Float.toString(rating);

        final MultiValueMap<String, String> parameters =
                new LinkedMultiValueMap<String, String>();
        parameters.add(LAVATORY_ID_SERVER_KEY, Integer.toString(lid));
        parameters.add(USER_ID_SERVER_KEY, uid);
        parameters.add(RATING_SERVER_KEY, ratingString);
        parameters.add(REVIEW_TEXT_SERVER_KEY, reviewText);
        parameters.add(USER_NAME_SERVER_KEY, username);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        // set the message converters for the request
        final HttpMessageConverter<String> stringConverter =
                new StringHttpMessageConverter();
        final FormHttpMessageConverter formConverter =
                new FormHttpMessageConverter();
        final List<HttpMessageConverter<?>> msgConverters =
                new ArrayList<HttpMessageConverter<?>>();
        msgConverters.add(formConverter);
        msgConverters.add(stringConverter);
        getRestTemplate().setMessageConverters(msgConverters);

        final HttpEntity<?> httpEntity = new HttpEntity<Object>(parameters,
                headers);

        return getRestTemplate().exchange(SUBMIT_REVIEW_SERVICE_URL,
                HttpMethod.POST, httpEntity, ResponseEntity.class);
    }

}

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

import com.octo.android.robospice.request.
        springandroid.SpringAndroidSpiceRequest;

/**
 * Class for updating a review's helpfulness in the LavatoryLocator service.
 * 
 * @author Keith Miller
 *
 */
@SuppressWarnings("rawtypes")
public class UpdateHelpfulnessRequest extends
        SpringAndroidSpiceRequest<ResponseEntity> {
    
    private static final String MARK_HELPFUL_SERVICE_URL = 
            "http://lavlocdb.herokuapp.com/submithelpful.php";
    private static final String USER_ID_KEY = "uid";
    private static final String USER_NAME_SERVER_KEY = "username";
    private static final String REVIEW_ID_KEY = "reviewId";
    private static final String HELPFULNESS_KEY = "helpful";
    
    private String uid;
    private String username;
    private int reviewId;
    private int helpful;

    
    /**
     * Constructs a new {@link UpdateHelpfulnessRequest} with the given
     * parameters.
     * 
     * @param username
     *            the username of hte user submitting the request
     * @param uid
     *            the user ID of the user submitting the request
     * @param reviewId
     *            the ID of the review to edit
     * @param helpful
     *            the helpfulness value to update
     */
    public UpdateHelpfulnessRequest(String username, String uid, 
            int reviewId, int helpful) {
        super(ResponseEntity.class);
        
        this.uid = uid;
        this.reviewId = reviewId;
        this.helpful = helpful;
        this.username = username;
    }
    
    @Override
    public ResponseEntity loadDataFromNetwork() throws Exception {
        final MultiValueMap<String, String> parameters = 
                new LinkedMultiValueMap<String, String>();
        parameters.add(USER_ID_KEY, uid);
        parameters.add(REVIEW_ID_KEY, Integer.toString(reviewId));
        parameters.add(HELPFULNESS_KEY, Integer.toString(helpful));
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

        final HttpEntity<?> httpEntity =
                new HttpEntity<Object>(parameters, headers);

        return getRestTemplate().exchange(MARK_HELPFUL_SERVICE_URL,
                HttpMethod.POST, httpEntity, ResponseEntity.class);
    }
}

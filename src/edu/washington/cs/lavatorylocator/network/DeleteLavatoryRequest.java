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
 * Class for deleting a lavatory.
 * 
 * @author Wilkes Sunseri
 *
 */
@SuppressWarnings("rawtypes")
public class DeleteLavatoryRequest extends
        SpringAndroidSpiceRequest<ResponseEntity> {

    private static final String DELETE_LAVATORY_SERVICE_URL = 
            "http://lavlocdb.herokuapp.com/deletelava.php";
    
    private static final String LAVATORY_ID_SERVER_KEY = "lid";
    private static final String USER_ID_SERVER_KEY = "uid";
    
    private final int lid;
    private final String uid;

    /**
     * Constructs a new {@link DeleteLavatoryRequest} with the given
     * parameters.
     *
     * @param uid
     *            the user ID of the user submitting the request
     * @param lid
     *            the ID of the lavatory to edit
     */
    public DeleteLavatoryRequest(final int lid, final String uid) {
        super(ResponseEntity.class);
        this.lid = lid;
        this.uid = uid;
    }

    @Override
    public ResponseEntity loadDataFromNetwork() throws Exception {
        final MultiValueMap<String, String> parameters =
                new LinkedMultiValueMap<String, String>();
        parameters.add(LAVATORY_ID_SERVER_KEY, Integer.toString(lid));
        parameters.add(USER_ID_SERVER_KEY, uid);

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

        return getRestTemplate().exchange(DELETE_LAVATORY_SERVICE_URL,
                HttpMethod.POST, httpEntity, ResponseEntity.class);
    }

}

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

import edu.washington.cs.lavatorylocator.model.LavatoryType;

/**
 * Class for adding a lavatory to the LavatoryLocator service.
 * 
 * @author Chris Rovillos
 * 
 */
@SuppressWarnings("rawtypes")
public class AddLavatoryRequest extends
        SpringAndroidSpiceRequest<ResponseEntity> {
    private static final String ADD_LAVATORY_SERVICE_URL = 
            "http://lavlocdb.herokuapp.com/addlava.php";

    private static final String USER_ID_SERVER_KEY = "uid";
    private static final String BUILDING_SERVER_KEY = "buildingName";
    private static final String FLOOR_SERVER_KEY = "floor";
    private static final String TYPE_SERVER_KEY = "lavaType";
    private static final String LATITUDE_SERVER_KEY = "latitude";
    private static final String LONGITUDE_SERVER_KEY = "longitude";

    private String uid;
    private String building;
    private String floor;
    private LavatoryType type;
    private double latitude;
    private double longitude;

    /**
     * Constructs a new {@link AddLavatoryRequest} with the given parameters.
     * 
     * @param uid
     *            the user ID of the user submitting the request
     * @param building
     *            the lavatory's building
     * @param floor
     *            the lavatory's floor
     * @param room
     *            the lavatory's room
     * @param type
     *            the lavatory's type
     * @param latitude
     *            the lavatory's latitude
     * @param longitude
     *            the lavatory's longitude
     */
    public AddLavatoryRequest(String uid, String building, String floor,
            String room, LavatoryType type, double latitude,
            double longitude) {
        super(ResponseEntity.class);

        this.uid = uid;
        this.building = building;
        this.floor = floor;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public ResponseEntity loadDataFromNetwork() throws Exception {
        final MultiValueMap<String, String> parameters = 
                new LinkedMultiValueMap<String, String>();
        parameters.add(USER_ID_SERVER_KEY, uid);
        parameters.add(BUILDING_SERVER_KEY, building);
        parameters.add(FLOOR_SERVER_KEY, floor);
        parameters.add(LATITUDE_SERVER_KEY, Double.toString(latitude));
        parameters.add(LONGITUDE_SERVER_KEY, Double.toString(longitude));
        parameters.add(TYPE_SERVER_KEY, getLavatoryTypeChar(type));

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

        return getRestTemplate().exchange(ADD_LAVATORY_SERVICE_URL,
                HttpMethod.POST, httpEntity, ResponseEntity.class);
    }
    
    /**
     * Returns a character representing the lavatory type in the JSON to be
     * sent to the server.
     * 
     * @param type a {@link LavatoryType}
     * @return a character representing the lavatory type in the JSON to be
     *         sent to the server
     */
    public static String getLavatoryTypeChar(LavatoryType type) {
        switch (type) {
        case MALE:
            return "M";
        case FEMALE:
            return "F";
        case FAMILY:
            return "A";
        case UNISEX:
            return "U";
        default:
            return "";
        }
    }
}

package edu.washington.cs.lavatorylocator.network;

import android.net.Uri;
import android.text.TextUtils;

import com.octo.android.robospice.request.
    springandroid.SpringAndroidSpiceRequest;

import edu.washington.cs.lavatorylocator.model.LavatorySearchResults;

/**
 * Class for sending search queries to the LavatoryLocator service.
 * 
 * @author Chris Rovillos
 * 
 */
public class LavatorySearchRequest extends
        SpringAndroidSpiceRequest<LavatorySearchResults> {
    private static final String LAVATORY_SEARCH_SERVICE_URL = 
            "http://lavlocdb.herokuapp.com/lavasearch.php";
    private static final String LATITUDE_SERVER_KEY = "locationLat";
    private static final String LONGITUDE_SERVER_KEY = "locationLong";
    private static final String RADIUS_SERVER_KEY = "maxDist";
    private static final String BUILDING_SERVER_KEY = "bldgName";
    private static final String FLOOR_SERVER_KEY = "floor";
    private static final String ROOM_SERVER_KEY = "room";
    private static final String MIN_RATING_SERVER_KEY = "minRating";
    private static final String TYPE_SERVER_KEY = "lavaType";

    private String building;
    private String floor;
    private String room;

    private double minRating;
    private String type;

    private String latitude;
    private String longitude;
    private String radius;

    private boolean loadAllLavatories;

    /**
     * Creates a new {@link LavatorySearchRequest} to request all lavatories.
     */
    public LavatorySearchRequest() {
        super(LavatorySearchResults.class);

        loadAllLavatories = true;
    }

    /**
     * Creates a new {@link LavatorySearchRequest} to request lavatories that
     * match the given parameters.
     * 
     * @param building
     *            the building the lavatory is in
     * @param floor
     *            the floor the lavatory is on
     * @param room
     *            the lavatory's room number
     * @param minRating
     *            the minimum rating of lavatories to be returned
     * @param type
     *            the type of lavatory
     * @param latitude
     *            the latitude of the search request point
     * @param longitude
     *            the longitude of the search request point
     * @param radius
     *            the radius of lavatories to return, as measured from the
     *            search request point
     */
    public LavatorySearchRequest(String building, String floor, String room,
            double minRating, String type, String latitude, String longitude,
            String radius) {
        super(LavatorySearchResults.class);

        this.building = building;
        this.floor = floor;
        this.room = room;
        this.minRating = minRating;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;

        this.loadAllLavatories = false;
    }

    @Override
    public LavatorySearchResults loadDataFromNetwork() throws Exception {
        if (loadAllLavatories) {
            return getRestTemplate().getForObject(LAVATORY_SEARCH_SERVICE_URL,
                    LavatorySearchResults.class);
        } else {
            final Uri.Builder uriBuilder = 
                    Uri.parse(LAVATORY_SEARCH_SERVICE_URL).buildUpon();
            
            appendQueryParameterToUriBuilderNoEmptyKeys(uriBuilder,
                    BUILDING_SERVER_KEY, building);
            appendQueryParameterToUriBuilderNoEmptyKeys(uriBuilder,
                    FLOOR_SERVER_KEY, floor);
            appendQueryParameterToUriBuilderNoEmptyKeys(uriBuilder,
                    ROOM_SERVER_KEY, room);
            appendQueryParameterToUriBuilderNoEmptyKeys(uriBuilder,
                    MIN_RATING_SERVER_KEY, Double.toString(minRating));
            appendQueryParameterToUriBuilderNoEmptyKeys(uriBuilder,
                    LATITUDE_SERVER_KEY, latitude);
            appendQueryParameterToUriBuilderNoEmptyKeys(uriBuilder,
                    LONGITUDE_SERVER_KEY, longitude);
            appendQueryParameterToUriBuilderNoEmptyKeys(uriBuilder,
                    RADIUS_SERVER_KEY, radius);
            appendQueryParameterToUriBuilderNoEmptyKeys(uriBuilder,
                    TYPE_SERVER_KEY, type);

            final String url = uriBuilder.build().toString();

            return getRestTemplate().getForObject(url,
                    LavatorySearchResults.class);
        }
    }

    /**
     * Appends the given query key and value parameter to the given
     * {@link Uri.Builder}. Keys with empty values are not appended.
     * 
     * @param uriBuilder
     *            the {@link Uri.Builder} in which to append the query
     *            parameters
     * @param key
     *            the key to append
     * @param value
     *            the value to append
     * @return the {@link Uri.Builder} passed in
     */
    private Uri.Builder appendQueryParameterToUriBuilderNoEmptyKeys(
            Uri.Builder uriBuilder, String key, String value) {
        if (!TextUtils.isEmpty(value)) {
            return uriBuilder.appendQueryParameter(key, value);
        } else {
            return uriBuilder;
        }
    }
}

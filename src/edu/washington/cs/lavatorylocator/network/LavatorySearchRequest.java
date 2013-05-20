package edu.washington.cs.lavatorylocator.network;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import edu.washington.cs.lavatorylocator.model.LavatorySearchResults;

/**
 * Class for sending search queries to the LavatoryLocator service.
 * 
 * @author Chris Rovillos
 * 
 */
public class LavatorySearchRequest extends
        SpringAndroidSpiceRequest<LavatorySearchResults> {
    private static final String LAVATORY_SEARCH_SERVICE_URL = "http://lavlocdb.herokuapp.com/lavasearch.php";

    private String building;
    private String floor;
    private String room;

    private double minRating;
    private char type;

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
            double minRating, char type, String latitude, String longitude,
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
            // TODO: use URL builder instead
            String requestUrl = LAVATORY_SEARCH_SERVICE_URL + "?locationLat="
                    + latitude + "&locationLong=" + longitude + "&maxDist="
                    + radius + "&bldgName=" + building + "&floor=" + floor
                    + "&room=" + room + "&minRating=" + minRating
                    + "&lavaType=" + type;
            return getRestTemplate().getForObject(requestUrl,
                    LavatorySearchResults.class);
        }
    }
}

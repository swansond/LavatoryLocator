package edu.washington.cs.lavatorylocator.network;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import edu.washington.cs.lavatorylocator.model.LavatorySearchResults;

/**
 * Class for sending lavatory search queries via the network.
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
     * Creates a new {@link LavatorySearchRequest}.
     */
    public LavatorySearchRequest() {
        super(LavatorySearchResults.class);
        
        loadAllLavatories = true;
    }
    
    /**
     * @param building
     * @param floor
     * @param room
     * @param minRating
     * @param maxDistance
     * @param type
     * @param latitude
     * @param longitude
     * @param radius
     * @param loadAllLavatories
     */
    public LavatorySearchRequest(String building, String floor, String room, double minRating, char type, String latitude, String longitude,
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
            String requestUrl = LAVATORY_SEARCH_SERVICE_URL + "?locationLat=" + latitude + "&locationLong=" + longitude +
                    "&maxDist=" + radius + "&bldgName=" + building + "&floor=" + floor + "&room=" + room + "&minRating=" + minRating + "&lavaType=" + type;
            return getRestTemplate().getForObject(requestUrl, LavatorySearchResults.class);
        }
    }
}

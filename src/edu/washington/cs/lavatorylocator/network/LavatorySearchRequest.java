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
    
    private double latitude;
    private double longitude;
    private double radius;
    private boolean loadAllLavatories;

    /**
     * Creates a new {@link LavatorySearchRequest}.
     */
    public LavatorySearchRequest() {
        super(LavatorySearchResults.class);
        
        loadAllLavatories = true;
    }
    
    /**
     * Creates a new {@link LavatorySearchRequest}.
     */
    public LavatorySearchRequest(double latitude, double longitude, double radius) {
        super(LavatorySearchResults.class);
        
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        loadAllLavatories = false;
    }

    @Override
    public LavatorySearchResults loadDataFromNetwork() throws Exception {
        if (loadAllLavatories) {
            return getRestTemplate().getForObject(LAVATORY_SEARCH_SERVICE_URL,
                LavatorySearchResults.class);
        } else {
            // TODO: use URL builder instead
            String requestUrl = LAVATORY_SEARCH_SERVICE_URL + "?locationLat=" + latitude + "&locationLong=" + longitude + "&maxDist=" + radius;
            return getRestTemplate().getForObject(requestUrl, LavatorySearchResults.class);
        }
    }
}

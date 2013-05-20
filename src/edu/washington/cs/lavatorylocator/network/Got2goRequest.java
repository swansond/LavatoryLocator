package edu.washington.cs.lavatorylocator.network;

import com.octo.android.robospice.request.
    springandroid.SpringAndroidSpiceRequest;

import edu.washington.cs.lavatorylocator.model.LavatoryData;

/**
 * Class for sending the Got2Go request to the LavatoryLocator service.
 * 
 * @author Chris Rovillos
 * 
 */
public class Got2goRequest extends SpringAndroidSpiceRequest<LavatoryData> {

    private static final String GOT2GO_SERVICE_URL = 
            "http://lavlocdb.herokuapp.com/got2go.php";

    private double latitude;
    private double longitude;

    /**
     * Creates a new {@link Got2goRequest} with the given latitude and
     * longitude.
     * 
     * @param latitude
     *            the latitude
     * @param longitude
     *            the longitude
     */
    public Got2goRequest(double latitude, double longitude) {
        super(LavatoryData.class);

        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public LavatoryData loadDataFromNetwork() throws Exception {
        // TODO: use URL builder instead
        final String requestUrl = GOT2GO_SERVICE_URL
                + "?locationLat=" + latitude
                + "&locationLong=" + longitude;
        return getRestTemplate().getForObject(requestUrl, LavatoryData.class);
    }
}

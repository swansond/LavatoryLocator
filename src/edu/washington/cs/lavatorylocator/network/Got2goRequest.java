package edu.washington.cs.lavatorylocator.network;

import android.net.Uri;

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
    private static final String LATITUDE_SERVER_KEY = "locationLat";
    private static final String LONGITUDE_SERVER_KEY = "locationLong";

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
        final String latitudeString = Double.toString(latitude);
        final String longitudeString = Double.toString(longitude);
        
        final Uri.Builder uriBuilder = Uri.parse(GOT2GO_SERVICE_URL)
                .buildUpon();

        uriBuilder.appendQueryParameter(LATITUDE_SERVER_KEY, latitudeString);
        uriBuilder.appendQueryParameter(LONGITUDE_SERVER_KEY, longitudeString);
        
        final String url = uriBuilder.build().toString();
        
        return getRestTemplate().getForObject(url, LavatoryData.class);
    }
}

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

    /**
     * Creates a new {@link LavatorySearchRequest}.
     */
    public LavatorySearchRequest() {
        super(LavatorySearchResults.class);
    }

    @Override
    public LavatorySearchResults loadDataFromNetwork() throws Exception {
        return getRestTemplate().getForObject(LAVATORY_SEARCH_SERVICE_URL,
                LavatorySearchResults.class);
    }
}

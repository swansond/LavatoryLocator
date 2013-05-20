package edu.washington.cs.lavatorylocator.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

// TODO: decide if this class is needed or if the 
// server should send back an array of JSON objects
// (if the server does, we can just use a List<LavatoryData> instead

/**
 * {@link LavatorySearchResults} contains a list of {@link LavatoryData} objects
 * that are returned from a search query to the LavatoryLocator service.
 * 
 * @author Chris Rovillos
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LavatorySearchResults {
    private List<LavatoryData> lavatories;

    /**
     * Returns a {@link List} of lavatories that represent the search results
     * from a query to the LavatoryLocator service.
     * 
     * @return a {@link List} of lavatories that represent the search results
     *         from a query to the LavatoryLocator service
     */
    public List<LavatoryData> getLavatories() {
        return lavatories;
    }

    /**
     * Sets the {@link List} of lavatories that represent the search results
     * from a query to the LavatoryLocator service.
     * 
     * @param lavatories
     *            a {@link List} of lavatories that represent the search results
     *            from a query to the LavatoryLocator service
     */
    public void setLavatories(List<LavatoryData> lavatories) {
        this.lavatories = lavatories;
    }
}

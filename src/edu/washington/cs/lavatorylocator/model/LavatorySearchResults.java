package edu.washington.cs.lavatorylocator.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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

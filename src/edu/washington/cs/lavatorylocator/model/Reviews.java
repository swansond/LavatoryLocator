package edu.washington.cs.lavatorylocator.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * {@link Reviews} contains a list of {@link ReviewData} objects that are
 * returned from a query to the LavatoryLocator service.
 * 
 * @author Chris Rovillos
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Reviews {
    private List<ReviewData> reviews;

    /**
     * Returns a {@link List} of reviews that represent the results from a query
     * to the LavatoryLocator service.
     * 
     * @return a {@link List} of reviews that represent the results from a query
     *         to the LavatoryLocator service
     */
    public List<ReviewData> getReviews() {
        return reviews;
    }

    /**
     * Sets the {@link List} of reviews that represent the results from a query
     * to the LavatoryLocator service.
     * 
     * @param reviews
     *            a {@link List} of reviews that represent the results from a
     *            query to the LavatoryLocator service.
     */
    public void setReviews(List<ReviewData> reviews) {
        this.reviews = reviews;
    }
}

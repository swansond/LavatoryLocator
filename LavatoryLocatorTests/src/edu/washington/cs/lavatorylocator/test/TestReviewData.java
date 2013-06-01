package edu.washington.cs.lavatorylocator.test;

import edu.washington.cs.lavatorylocator.model.ReviewData;
import junit.framework.TestCase;

/**
 * TestReviewData class contains tests for the ReviewData class.
 *
 * @author Wil
 *
 */
public class TestReviewData extends TestCase {

    private static final int FOUROHTHREE = 403;
    private static final float FOURPOINTOHTHREE = 4.03f;

    /**
     * Tests the constructor.
     * @black
     */
    public void test_reviewDataConstructor_new_isNotNull() {
        final ReviewData constructorTestReviewData = new ReviewData();
        assertTrue(constructorTestReviewData != null);
    }

    /**
     * Tests the getAuthor method.
     * @white
     */
    public void test_getAuthor_idIs403_getsUser403() {
        final ReviewData uidTestReviewData = new ReviewData();
        final String expectedTestUid = "User 403";
        uidTestReviewData.setUid(FOUROHTHREE);
        assertEquals(expectedTestUid, uidTestReviewData.getAuthor());
    }

    /**
     * Tests the getDatetime method.
     * @white
     */
    public void test_getDatetime_datetimeParses() {
        final ReviewData datetimeTestReviewData = new ReviewData();
        final String expectedOutput = "3/30/1990 7:56 AM";
        final String input = "1990-03-30 07:56:12.123456";
        datetimeTestReviewData.setDatetime(input);
        assertEquals(expectedOutput,
                datetimeTestReviewData.getDatetime());
    }

    /**
     * Tests the error capabilities of the getDatetime method.
     * @white
     */
    public void test_getDatetime_error() {
        final ReviewData datetimeTestReviewData = new ReviewData();
        final String expectedOutput = "error";
        final String input = "today was a good day";
        datetimeTestReviewData.setDatetime(input);
        assertEquals(expectedOutput,
                datetimeTestReviewData.getDatetime());
    }
    
    /**
     * Tests the getHelpfulness method.
     * @black
     */
    public void test_getHelpfulness_helpfulnessIs403_gets403() {
        final ReviewData helpfulnessTestReviewData = new ReviewData();
        final int expectedTestHelpfulness = 403;
        helpfulnessTestReviewData.setHelpfulness(FOUROHTHREE);
        assertEquals(expectedTestHelpfulness,
                helpfulnessTestReviewData.getHelpfulness());
    }

    /**
     * Tests the lid method.
     * @black
     */
    public void test_getLid_lidIs403_gets403() {
        final ReviewData lidTestReviewData = new ReviewData();
        final int expectedTestLid = 403;
        lidTestReviewData.setLid(FOUROHTHREE);
        assertEquals(expectedTestLid, lidTestReviewData.getLid());
    }

    /**
     * Tests the get rating feature.
     * @black
     */
    public void test_getRating_ratingIs4point03_gets4point03() {
        final ReviewData ratingTestReviewData = new ReviewData();
        final float expectedTestRating = 4.03f;
        ratingTestReviewData.setRating(FOURPOINTOHTHREE);
        assertEquals(expectedTestRating,
                ratingTestReviewData.getRating());
    }

    /**
     * Tests the get review system.
     * @black
     */
    public void test_getReview_reviewIsSoUnhelpful_getsSoUnhelpful() {
        final ReviewData reviewTestReviewData = new ReviewData();
        final String expectedTestReview = "So unhelpful";
        reviewTestReviewData.setReview("So unhelpful");
        assertEquals(expectedTestReview, reviewTestReviewData.getReview());
    }

    /**
     * Tests the uservoting system.
     * @black
     */
    public void test_getUservote_uservoteIs403_gets403() {
        final ReviewData uservoteTestReviewData = new ReviewData();
        final int expectedTestUservote = 403;
        uservoteTestReviewData.setUservote(FOUROHTHREE);
        assertEquals(expectedTestUservote,
                uservoteTestReviewData.getUservote());
    }
}

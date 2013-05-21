package edu.washington.cs.lavatorylocator.test;

import edu.washington.cs.lavatorylocator.model.ReviewData;
import junit.framework.TestCase;


public class TestReviewData extends TestCase {
    
    public void reviewDataConstructor_new_isNotNull() {
        ReviewData constructorTestReviewData = new ReviewData();
        assertTrue(constructorTestReviewData != null);
    }
    
    public void getUid_idIs403_gets403() {
        ReviewData uidTestReviewData = new ReviewData();
        int expectedTestUid = 403;
        uidTestReviewData.setUid(403);
        assertEquals(expectedTestUid, uidTestReviewData.getAuthor());
    }
    
    public void getDatetime_datetimeIsToday_getsToday() {
        ReviewData datetimeTestReviewData = new ReviewData();
        String expectedTestDatetime = "Today";
        datetimeTestReviewData.setDatetime("Today");
        assertEquals(expectedTestDatetime, datetimeTestReviewData
                .getDatetime());
    }
    
    public void getHelpfulness_helpfulnessIs403_gets403() {
        ReviewData helpfulnessTestReviewData = new ReviewData();
        int expectedTestHelpfulness = 403;
        helpfulnessTestReviewData.setHelpfulness(403);
        assertEquals(expectedTestHelpfulness, helpfulnessTestReviewData
                .getHelpfulness());
    }
    
    public void getLid_lidIs403_gets403() {
        ReviewData lidTestReviewData = new ReviewData();
        int expectedTestLid = 403;
        lidTestReviewData.setLid(403);
        assertEquals(expectedTestLid, lidTestReviewData.getLid());
    }
    
    public void getRating_ratingIs4point03_gets4point03() {
        ReviewData ratingTestReviewData = new ReviewData();
        float expectedTestRating = 4.03f;
        ratingTestReviewData.setRating(4.03f);
        assertEquals(expectedTestRating, ratingTestReviewData.getRating());
    }
    
    public void getReview_reviewIsSoUnhelpful_getsSoUnhelpful() {
        ReviewData reviewTestReviewData = new ReviewData();
        String expectedTestReview = "So unhelpful";
        reviewTestReviewData.setReview("So unhelpful");
        assertEquals(expectedTestReview, reviewTestReviewData.getReview());
    }
    
    public void getUservote_uservoteIs403_gets403() {
        ReviewData uservoteTestReviewData = new ReviewData();
        int expectedTestUservote = 403;
        uservoteTestReviewData.setUservote(403);
        assertEquals(expectedTestUservote, uservoteTestReviewData.getUservote());
    }
}

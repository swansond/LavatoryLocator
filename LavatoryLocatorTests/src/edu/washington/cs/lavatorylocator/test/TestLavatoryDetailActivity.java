package edu.washington.cs.lavatorylocator.test;

import java.util.ArrayList;
import java.util.List;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.activity.AddReviewActivity;
import edu.washington.cs.lavatorylocator.activity.EditLavatoryDetailActivity;
import edu.washington.cs.lavatorylocator.activity.LavatoryDetailActivity;
import edu.washington.cs.lavatorylocator.activity.MainActivity;
import edu.washington.cs.lavatorylocator.adapter.ReviewsListAdapter;
import edu.washington.cs.lavatorylocator.model.ReviewData;
import edu.washington.cs.lavatorylocator.test.utils.TestUtils;
import edu.washington.cs.lavatorylocator.view.ReviewListItemView;

/**
 * UI-related tests for {@link LavatoryDetailActivity}.
 *
 * @author Wilkes Sunseri
 *
 */
public class TestLavatoryDetailActivity extends
        ActivityInstrumentationTestCase2<MainActivity> {

    private static final int WAIT_TIME_MILLISECONDS = 5000;
    private static final float TEST_FLOAT = 4f;
    private static final int TEST_INT = 0;
    private Solo solo;

    // --------------------------------------------------------------
    // TEST CLASS LIFECYCLE
    // --------------------------------------------------------------

    /**
     * Required constructor.
     */
    public TestLavatoryDetailActivity() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
        
        TestUtils.dismissGooglePlayServicesErrorDialog(solo);
        solo.clickInList(0);
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    // --------------------------------------------------------------
    // TESTS
    // --------------------------------------------------------------

    /**
     * Tests to see if the {@link LavatoryDetailActivity} is loaded.
     */
    public void onLavatoryDetailActivity() {
        solo.assertCurrentActivity("Not on LavatoryDetailActivity",
                LavatoryDetailActivity.class);
    }

    /**
     * Tests to see if clicking on the action bar home button goes back to the
     * {@link MainActivity}.
     */
    public void test_backToMainActivity_actionBarHomeButton() {
        solo.clickOnActionBarHomeButton();
        solo.assertCurrentActivity(
                "Not on MainActivity after clicking on action bar home button",
                MainActivity.class);
    }

    /**
     * Tests to see if adding an incomplete review keeps you on the
     * {@link AddReviewActivity}.
     */
    public void test_addReview_noRating() {
        solo.clickOnActionBarItem(R.id.action_add_review);
        TestUtils.dismissGooglePlayServicesErrorDialog(solo);

        final EditText reviewTextField = (EditText) solo
                .getView(R.id.add_review_text);
        solo.enterText(reviewTextField, " ");
        solo.clickOnActionBarItem(R.id.action_search);

        solo.assertCurrentActivity(
                "Should be still on AddReviewActivity after submitting "
                        + "an incomplete review",
                        AddReviewActivity.class);
    }
    
    /**
     * Tests adding a review.
     */
    public void test_addReview() {
        // work around bug:
        // http://code.google.com/p/robotium/issues/detail?id=179
        solo = new Solo(getInstrumentation());
        
        solo.clickOnActionBarItem(R.id.action_add_review);
        TestUtils.dismissGooglePlayServicesErrorDialog(solo);
        
        solo.waitForActivity(AddReviewActivity.class);
        ((AddReviewActivity) solo.getCurrentActivity()).setUid("1");

        final EditText reviewTextField = (EditText) solo
                .getView(R.id.add_review_text);
        solo.enterText(reviewTextField, "Wow");
        solo.clickOnActionBarItem(R.id.action_search);

        solo.assertCurrentActivity(
                "Should be still on AddReviewActivity",
                        AddReviewActivity.class);
    }

    /**
     * Tests to see if clicking the button to go to 
     * {@link EditLavatoryDetailActivity} takes you there.
     */
    public void test_editLavatoryDetail_menuItem() {
        solo.clickOnActionBarItem(R.id.action_edit_lavatory_detail);
        solo.assertCurrentActivity("Not on editLavatoryDetail activity",
                EditLavatoryDetailActivity.class);
    }
    
    /**
     * Tests to see if submitting a change request with no modified details
     * keeps you on {@link EditLavatoryDetailActivity} because the emulator
     * can't log in.
     */
    public void test_editLavatoryDetail_noChange() {
        solo.clickOnActionBarItem(R.id.action_edit_lavatory_detail);
        solo.clickOnActionBarItem(R.id.action_submit);
        solo.assertCurrentActivity("Not still on EditLavatoryDetail activity",
                EditLavatoryDetailActivity.class);
    }
    
    /**
     * Tests to see if submitting a change request with no modified details
     * keeps you on {@link EditLavatoryDetailActivity} because the emulator
     * can't log in. This test uses a non-null user id.
     */
    public void test_editLavatoryDetail_submit() {
        // work around bug:
        // http://code.google.com/p/robotium/issues/detail?id=179
        solo = new Solo(getInstrumentation());
        
        solo.clickOnActionBarItem(R.id.action_edit_lavatory_detail);
        solo.waitForActivity(EditLavatoryDetailActivity.class);
        ((EditLavatoryDetailActivity) solo.getCurrentActivity()).setUid("1");
        
        solo.clickOnActionBarItem(R.id.action_submit);
        solo.assertCurrentActivity("Not still on EditLavatoryDetail activity",
                EditLavatoryDetailActivity.class);
    }
    
     /**
      * Tests to see if the top left back button sends you back to
      * {@link LavatoryDetailActivity} from {@link EditLavatoryDetailActivity}.
      */
    public void test_editLavatoryDetail_backButton() {
        solo.clickOnActionBarItem(R.id.action_edit_lavatory_detail);
        solo.clickOnActionBarItem(R.id.action_discard);
        solo.assertCurrentActivity("Not back to LavatoryDetail activity",
                LavatoryDetailActivity.class);
    }

    /**
     * Tests to see if requesting deletion leaves you on
     * {@link LavatoryDetailActivity}.
     */
    public void test_requestDeletion_menuItem() {
        solo.clickOnActionBarItem(R.id.action_request_delete_lavatory);
        solo.assertCurrentActivity("Not still on LavatoryDetail activity",
                LavatoryDetailActivity.class);
    }
    
    /**
     * Tests to see if requesting deletion leaves you on
     * {@link LavatoryDetailActivity}, but with a valid user id.
     */
    public void test_requestDeletion_menuItem_validUid() {
        // work around bug:
        // http://code.google.com/p/robotium/issues/detail?id=179
        solo = new Solo(getInstrumentation());
        
        solo.waitForActivity(LavatoryDetailActivity.class);
        ((LavatoryDetailActivity) solo.getCurrentActivity()).setUid("1");
        
        solo.clickOnActionBarItem(R.id.action_request_delete_lavatory);
        solo.assertCurrentActivity("Not still on LavatoryDetail activity",
                LavatoryDetailActivity.class);
    }
    
    /**
     * Tests the ReviewListItemView constructor.
     * This just needs to be in any test class with an activity, really.
     * @black
     */
    public void test_ReviewListItemView_constructor_notNull() {
        final ReviewListItemView rliv = new ReviewListItemView(getActivity());
        assertNotNull(rliv);
    }
    
    /**
     * Tests updateView() in ReviewListItemView.
     * @white
     */
    public void test_ReviewListItemView_updateView_notNull() {
        final ReviewListItemView rliv = new ReviewListItemView(getActivity());
        final ReviewData testReview = new ReviewData();
        testReview.setUsername("test");
        testReview.setRating(TEST_FLOAT);
        testReview.setReview("this is a test");
        testReview.setRid(-1);
        testReview.setDatetime("today");
        testReview.setHelpfulness(1);
        testReview.setUservote(1);
        rliv.updateView(testReview);
        assertNotNull(rliv);
    }
    
    /**
     * Tests ReviewsListAdapter constructor.
     * @black
     */
    public void test_ReviewsListAdapter_constructor_notNull() {
        final ReviewData testReview = new ReviewData();
        testReview.setUsername("test");
        testReview.setRating(TEST_FLOAT);
        testReview.setReview("this is a test");
        testReview.setRid(-1);
        testReview.setDatetime("today");
        testReview.setHelpfulness(1);
        testReview.setUservote(1);
        final List<ReviewData> reviewList = new ArrayList<ReviewData>();
        reviewList.add(testReview);
        final ReviewsListAdapter rla = new ReviewsListAdapter(getActivity(),
                TEST_INT, TEST_INT, reviewList);
        assertNotNull(rla);
    }
    
    /**
     * Tests getView() in ReviewsListAdapter.
     * @black
     */
    public void test_ReviewsListAdapter_getView_notNull() {
        final ReviewData testReview = new ReviewData();
        testReview.setUsername("test");
        testReview.setRating(TEST_FLOAT);
        testReview.setReview("this is a test");
        testReview.setRid(-1);
        testReview.setDatetime("today");
        testReview.setHelpfulness(1);
        testReview.setUservote(1);
        final List<ReviewData> reviewList = new ArrayList<ReviewData>();
        reviewList.add(testReview);
        final ReviewsListAdapter rla = new ReviewsListAdapter(getActivity(),
                TEST_INT, TEST_INT, reviewList);
        assertNotNull(rla.getView(TEST_INT, null, null));
    }
}

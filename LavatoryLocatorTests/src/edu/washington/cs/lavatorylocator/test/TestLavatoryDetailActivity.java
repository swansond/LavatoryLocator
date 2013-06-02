package edu.washington.cs.lavatorylocator.test;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.activity.AddReviewActivity;
import edu.washington.cs.lavatorylocator.activity.EditLavatoryDetailActivity;
import edu.washington.cs.lavatorylocator.activity.LavatoryDetailActivity;
import edu.washington.cs.lavatorylocator.activity.MainActivity;
import edu.washington.cs.lavatorylocator.test.utils.TestUtils;

/**
 * UI-related tests for {@link LavatoryDetailActivity}.
 *
 * @author Wilkes Sunseri
 *
 */
public class TestLavatoryDetailActivity extends
        ActivityInstrumentationTestCase2<MainActivity> {

    private static final int WAIT_TIME_MILLISECONDS = 5000;
    
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
}

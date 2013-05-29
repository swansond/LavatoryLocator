package edu.washington.cs.lavatorylocator.test;

import com.jayway.android.robotium.solo.Solo;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.activity.AboutActivity;
import edu.washington.cs.lavatorylocator.activity.LavatoryDetailActivity;
import edu.washington.cs.lavatorylocator.activity.MainActivity;
import edu.washington.cs.lavatorylocator.activity.SearchActivity;
import edu.washington.cs.lavatorylocator.test.utils.TestUtils;
import android.test.ActivityInstrumentationTestCase2;

/**
 * UI-related tests for {@link MainActivity}.
 * 
 * @author Chris Rovillos
 * 
 */
public class TestMainActivity extends
        ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    // --------------------------------------------------------------
    // TEST CLASS LIFECYCLE
    // --------------------------------------------------------------
    /**
     * Required constructor.
     */
    public TestMainActivity() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());

        TestUtils.dismissGooglePlayServicesErrorDialog(solo);
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    // --------------------------------------------------------------
    // TESTS
    // --------------------------------------------------------------
    /**
     * Tests to see if {@link MainActivity} is the first {@link Activity} to
     * load when the app is started.
     */
    public void test_mainActivity_onStart() {
        solo.assertCurrentActivity("Not on MainActivity when app is started",
                MainActivity.class);
    }

    /**
     * Tests to see if the About action bar item goes to the
     * {@link AboutActivity}.
     */
    public void test_about_menuItem() {
        solo.clickOnActionBarItem(R.id.action_about);

        solo.assertCurrentActivity(
                "Not on AboutActivity after About action bar menu item is "
                        + "clicked", AboutActivity.class);
    }

    /**
     * Tests to see if clicking on an item in the lavatory search results list
     * goes to the {@link LavatoryDetailActivity}.
     */
    public void test_searchResultsList_goToLavatoryDetailActivity() {
        solo.clickInList(0);

        solo.assertCurrentActivity(
                "Not on LavatoryDetailActivity after clicking on an search "
                        + "result item in the list",
                LavatoryDetailActivity.class, true);
    }

    /**
     * Tests to see if clicking on the Search action bar item goes to the
     * {@link SearchActivity}.
     */
    public void test_searchButton_goToSearchActivity() {
        solo.clickOnActionBarItem(R.id.action_search);

        solo.assertCurrentActivity(
                "Not on SearchActivity after clicking on search button",
                SearchActivity.class, true);
    }

}

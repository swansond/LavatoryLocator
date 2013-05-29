package edu.washington.cs.lavatorylocator.test;

import com.jayway.android.robotium.solo.Solo;

import edu.washington.cs.lavatorylocator.R;
import edu.washington.cs.lavatorylocator.activity.MainActivity;
import edu.washington.cs.lavatorylocator.activity.SearchActivity;
import edu.washington.cs.lavatorylocator.test.utils.TestUtils;
import android.test.ActivityInstrumentationTestCase2;
import android.text.Editable;
import android.widget.EditText;

/**
 * UI-related tests for {@link SearchActivity}.
 * <p>
 * Note: it looks like Robotium can't detect {@link Toast}s at all, so this
 * class just tests to see if invalid input doesn't cause the MainActivity to
 * be shown.
 * 
 * @author Chris Rovillos
 * 
 */
public class TestSearchActivity extends
        ActivityInstrumentationTestCase2<SearchActivity> {
    /**
     * The number of {@link EditText} fields in {@link SearchActivity} that accept arbitrary text input. (Robotium doesn't seem to recognize that the latitude, longitude, and max distance text fields exist at all.
     */
    private static final int EDITTEXT_FIELD_COUNT = 3;
    
    private Solo solo;

    // --------------------------------------------------------------
    // TEST CLASS LIFECYCLE
    // --------------------------------------------------------------

    /**
     * Required constructor.
     */
    public TestSearchActivity() {
        super(SearchActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
        
        TestUtils.dismissGooglePlayServicesErrorDialog(solo);

        // go to SearchActivity
        solo.clickOnActionBarItem(R.id.action_search);
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    // --------------------------------------------------------------
    // TESTS
    // --------------------------------------------------------------
    /**
     * Tests to see if the {@link SearchActivity} is loaded.
     */
    public void test_onSearchActivity() {
        solo.assertCurrentActivity("Not on SearchActivity",
                SearchActivity.class);
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
     * Tests the input validation for the lavatory search text fields by providing no input.
     */
    public void test_inputValidation_noInput() {
        solo.clickOnActionBarItem(R.id.action_search);
        
        solo.assertCurrentActivity("Should be still on SearchActivity after invalid search input", SearchActivity.class);
    }
    
    /**
     * Tests the input validation for the lavatory search text fields by providing spaces as input to all fields.
     */
    public void test_inputValidation_allFields_space() {
        final EditText buildingNameTextField = (EditText)solo.getView(R.id.activity_search_building_name);
        final EditText floorTextField = (EditText)solo.getView(R.id.activity_search_floor);
        final EditText roomTextField = (EditText)solo.getView(R.id.activity_search_room_number);
        final EditText latitudeTextField = (EditText)solo.getView(R.id.activity_search_latitude);
        final EditText longitudeTextField = (EditText)solo.getView(R.id.activity_search_longitude);
        final EditText maxDistanceTextField = (EditText)solo.getView(R.id.activity_search_max_distance);
        
        solo.enterText(buildingNameTextField, " ");
        solo.enterText(floorTextField, " ");
        solo.enterText(roomTextField, " ");
        solo.enterText(latitudeTextField, " ");
        solo.enterText(longitudeTextField, " ");
        solo.enterText(maxDistanceTextField, " ");
        
        solo.clickOnActionBarItem(R.id.action_search);
        
        solo.assertCurrentActivity("Should be still on SearchActivity after invalid search input", SearchActivity.class);
    }
    
    /**
     * Tests the input validation for the "Max Distance" text field by providing non-numeric characters.
     */
    public void test_inputValidation_maxDistanceField_space() {
        final EditText maxDistanceTextField = (EditText)solo.getView(R.id.activity_search_max_distance);
        
        try {
            solo.typeText(maxDistanceTextField, " ");
        } catch (Error err) {
            return;
        }
        
        fail("Spaces allowed to be entered on Max Distance field.");
    }
}

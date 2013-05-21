package edu.washington.cs.lavatorylocator.test;

import junit.framework.TestCase;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.LavatoryMapMarkerOptionsFactory;

/**
 * This class tests the LavatoryMapMarkerOptionsFactory class.
 * @author David Jung
 *
 */
public class TestLavatoryMapMarkerOptionsFactory extends TestCase {

    /**
     * Tests the creation method if passed a null parameter.
     */
    public void test_createLavatoryMapMarkerOptions_null_throwsException() {
        final LavatoryData nullData = null;

        try {
            LavatoryMapMarkerOptionsFactory
                .createLavatoryMapMarkerOptions(nullData);
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        assertTrue(false);
    }

    /**
     * Tests the usual case with a generic lavatorydata object.
     */
    public void test_createLavatoryMapMarkerOptions_usual_returnsExpected() {
        final LavatoryData toParse;
        final LatLng positionToUse;
        final String nameToUse;
        final MarkerOptions expected;
        final MarkerOptions actual;

        toParse = new LavatoryData(1, 'M', "1", "1", "101", 0, 0, 0, 0);
        positionToUse = new LatLng(toParse.getLatitude(),
                toParse.getLongitude());
        nameToUse = toParse.getName();
        expected = new MarkerOptions().position(positionToUse).title(nameToUse);

        actual = LavatoryMapMarkerOptionsFactory
                .createLavatoryMapMarkerOptions(toParse);

        assertEquals(expected, actual);
    }

}

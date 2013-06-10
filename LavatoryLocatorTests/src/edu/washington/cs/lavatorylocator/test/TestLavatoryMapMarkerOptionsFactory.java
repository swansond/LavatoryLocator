package edu.washington.cs.lavatorylocator.test;

import junit.framework.TestCase;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.washington.cs.lavatorylocator.model.LavatoryData;
import edu.washington.cs.lavatorylocator.model.LavatoryMapMarkerOptionsFactory;
import edu.washington.cs.lavatorylocator.model.LavatoryType;

/**
 * This class tests the LavatoryMapMarkerOptionsFactory class.
 * @author David Jung
 *
 */
public class TestLavatoryMapMarkerOptionsFactory extends TestCase {

    /**
     * Tests the creation method if passed a null parameter.
     * @black
     */
    public void test_createLavatoryMapMarkerOptions_null_throwsException() {
        final LavatoryData nullData = null;
        boolean flag = true;
        try {
            LavatoryMapMarkerOptionsFactory
                .createLavatoryMapMarkerOptions(nullData);
        } catch (NullPointerException e) {
            assertTrue(true);
            flag = false;
        }
        if (flag) {
            fail();
        }
    }

    /**
     * Tests the usual case with a generic lavatorydata object.
     * @black
     */
    public void test_createLavatoryMapMarkerOptions_usual_returnsExpected() {
        final LavatoryData toParse;
        final LatLng positionToUse;
        final String nameToUse;
        final MarkerOptions expected;
        final MarkerOptions actual;
        final LavatoryType type = LavatoryType.MALE;
        final String uid = "1";
        final String lid = "1";
        final String room = "101";

        toParse = new LavatoryData(1, type, uid, lid, room, 0, 0, 0, 0);
        positionToUse = new LatLng(toParse.getLatitude(),
                toParse.getLongitude());
        nameToUse = toParse.getName();
        expected = new MarkerOptions().position(positionToUse).title(nameToUse);

        actual = LavatoryMapMarkerOptionsFactory
                .createLavatoryMapMarkerOptions(toParse);

        assertEquals(expected.getPosition(), actual.getPosition());
    }

}

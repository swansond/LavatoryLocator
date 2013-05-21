package edu.washington.cs.lavatorylocator.activity;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

/**
 * Base class for {@link android.support.v4.app.FragmentActivity}s that use the
 * RoboSpice library along with the SherlockActionBar library. This class uses a
 * {@code Jackson} {@code SpiceManager} for transforming JSON into plain Java
 * objects.
 * <p>
 * This class cannot be instantiated; instead it must be subclassed.
 * 
 * @author Chris Rovillos
 * 
 */
public abstract class JacksonSpringSpiceSherlockFragmentActivity extends
        SherlockFragmentActivity {
    protected SpiceManager spiceManager = new SpiceManager(
            JacksonSpringAndroidSpiceService.class);

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    /**
     * Returns this class’s {@code SpiceManager}.
     * 
     * @return this class’s {@code SpiceManager}
     */
    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }
}

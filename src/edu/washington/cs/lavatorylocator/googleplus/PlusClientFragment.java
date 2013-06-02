package edu.washington.cs.lavatorylocator.googleplus;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.
        ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.
        OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;

import edu.washington.cs.lavatorylocator.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.Arrays;

/**
 * A fragment that you can add to your layout to automate connecting a
 * {@link PlusClient}. The key integration points are to attach the fragment
 * with {@link #getPlusClientFragment} in your Activity's {@code onCreate}
 * method, then call {@link #signIn} when the user clicks your sign-in button
 * and forward calls to {@code onActivityResult} to
 * {@link #handleOnActivityResult}.
 * <p>
 * Derived from the Google+ SDK sample code.
 * 
 * @author Chris Rovillos
 */
public final class PlusClientFragment extends SherlockFragment implements
        ConnectionCallbacks, OnConnectionFailedListener,
        OnAccessRevokedListener {

    /**
     * Tag to refer to this fragment.
     */
    private static final String TAG_PLUS_CLIENT = "plusClientFragment";

    /**
     * Tag to refer to an error (resolution) dialog.
     */
    private static final String TAG_ERROR_DIALOG =
            "plusClientFragmentErrorDialog";

    /**
     * Tag to refer to a progress dialog when sign in is requested.
     */
    private static final String TAG_PROGRESS_DIALOG =
            "plusClientFragmentProgressDialog";

    /**
     * Array of strings representing visible activities to request for
     * {@link #getArguments()}.
     */
    private static final String ARG_VISIBLE_ACTIVITIES = "visible_activities";

    /**
     * Integer request code to apply to requests, as set by {@link #signIn(int)}
     * .
     */
    private static final String STATE_REQUEST_CODE = "request_code";

    /**
     * Signed in successfully connection state.
     */
    private static final ConnectionResult CONNECTION_RESULT_SUCCESS =
            new ConnectionResult(ConnectionResult.SUCCESS, null);

    /**
     * An invalid request code to use to indicate that {@link #signIn(int)} 
     * hasn't been called.
     */
    private static final int INVALID_REQUEST_CODE = -1;

    // The PlusClient to connect.
    private PlusClient mPlusClient;

    // The last result from onConnectionFailed.
    private ConnectionResult mLastConnectionResult;

    // The request specified in signIn or INVALID_REQUEST_CODE if not signing
    // in.
    private int mRequestCode;

    // A handler to post callbacks (rather than call them in a potentially
    // reentrant way.)
    private Handler mHandler;

    /**
     * Local handler to send callbacks on sign in.
     */
    private final class PlusClientFragmentHandler extends Handler {
        public static final int WHAT_SIGNED_IN = 1;

        /**
         * Creates a {@code PlusClientFragmentHandler}.
         */
        public PlusClientFragmentHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what != WHAT_SIGNED_IN) {
                return;
            }

            final Activity activity = getActivity();
            if (mPlusClient.isConnected()
                    && activity instanceof OnSignedInListener) {
                ((OnSignedInListener) activity).onSignedIn(mPlusClient);
            }
        }
    }

    /**
     * Listener interface for sign in events. Activities hosting a
     * {@link PlusClientFragment} must implement this.
     */
    public interface OnSignedInListener {
        /**
         * Called when the {@link PlusClient} has been connected successfully.
         * 
         * @param plusClient
         *            The connected {@link PlusClient} to make requests on.
         */
        void onSignedIn(PlusClient plusClient);
    }

    /**
     * Attach a {@link PlusClient} managing fragment to you activity.
     * 
     * @param activity
     *            The activity to attach the fragment to.
     * @param visibleActivities
     *            An array of visible activities to request.
     * @return The fragment managing a {@link PlusClient}.
     */
    public static PlusClientFragment getPlusClientFragment(
            FragmentActivity activity, String[] visibleActivities) {
        if (!(activity instanceof OnSignedInListener)) {
            throw new IllegalArgumentException(
                    "The activity must implement OnSignedInListener to "
                            + "receive callbacks.");
        }

        // Check if the fragment is already attached.
        final FragmentManager fragmentManager = activity
                .getSupportFragmentManager();
        final Fragment fragment = fragmentManager
                .findFragmentByTag(TAG_PLUS_CLIENT);
        if (fragment instanceof PlusClientFragment) {
            // The fragment is attached. If it has the right visible activities,
            // return it.
            if (Arrays.equals(visibleActivities, fragment.getArguments()
                    .getStringArray(ARG_VISIBLE_ACTIVITIES))) {
                return (PlusClientFragment) fragment;
            }
        }

        final FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        // If a fragment was already attached, remove it to clean up.
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }

        // Create a new fragment and attach it to the fragment manager.
        final Bundle arguments = new Bundle();
        arguments.putStringArray(ARG_VISIBLE_ACTIVITIES, visibleActivities);

        final PlusClientFragment signInFragment = new PlusClientFragment();
        signInFragment.setArguments(arguments);
        fragmentTransaction.add(signInFragment, TAG_PLUS_CLIENT);
        fragmentTransaction.commit();
        return signInFragment;
    }

    /**
     * Creates a {@link PlusClient} and kicks-off the connection flow.
     * 
     * @param savedInstanceState
     *            the savedInstanceState from the activity lifecycle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain instance to avoid reconnecting on rotate. This means that
        // onDestroy and onCreate
        // will not be called on configuration changes.
        setRetainInstance(true);
        mHandler = new PlusClientFragmentHandler();

        // Create the PlusClient.
        final PlusClient.Builder plusClientBuilder = new PlusClient.Builder(
                getActivity().getApplicationContext(), this, this);
        final String[] visibleActivities = getArguments().getStringArray(
                ARG_VISIBLE_ACTIVITIES);
        if (visibleActivities != null && visibleActivities.length > 0) {
            plusClientBuilder.setVisibleActivities(visibleActivities);
        }
        plusClientBuilder.setScopes(Scopes.PLUS_PROFILE);
        mPlusClient = plusClientBuilder.build();

        if (savedInstanceState == null) {
            mRequestCode = INVALID_REQUEST_CODE;
        } else {
            mRequestCode = savedInstanceState.getInt(STATE_REQUEST_CODE,
                    INVALID_REQUEST_CODE);
        }
    }

    /**
     * Disconnects the {@link PlusClient} to avoid leaks.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlusClient.isConnecting() || mPlusClient.isConnected()) {
            mPlusClient.disconnect();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_REQUEST_CODE, mRequestCode);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestCode == INVALID_REQUEST_CODE) {
            // No user interaction, hide the progress dialog.
            hideProgressDialog();
            hideErrorDialog();
        } else if (mLastConnectionResult != null
                && !mLastConnectionResult.isSuccess()
                && !isShowingErrorDialog()) {
            showProgressDialog();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mRequestCode == INVALID_REQUEST_CODE) {
            mLastConnectionResult = null;
            connectPlusClient();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Successful connection!
        mLastConnectionResult = CONNECTION_RESULT_SUCCESS;
        mRequestCode = INVALID_REQUEST_CODE;

        if (isResumed()) {
            hideProgressDialog();
        }

        final Activity activity = getActivity();
        if (activity instanceof OnSignedInListener) {
            ((OnSignedInListener) activity).onSignedIn(mPlusClient);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mLastConnectionResult = connectionResult;
        // On a failed connection try again.
        if (isResumed() && mRequestCode != INVALID_REQUEST_CODE) {
            resolveLastResult();
        }
    }

    @Override
    public void onAccessRevoked(ConnectionResult status) {
        // Reconnect to get a new mPlusClient.
        mLastConnectionResult = null;
        // Cancel sign in.
        mRequestCode = INVALID_REQUEST_CODE;

        // Reconnect to fetch the sign-in (account chooser) intent from the plus
        // client.
        connectPlusClient();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Let new activities know the signed-in state.
        if (mPlusClient.isConnected()) {
            mHandler.sendEmptyMessage(PlusClientFragmentHandler.WHAT_SIGNED_IN);
        }
    }

    @Override
    public void onDisconnected() {
        // Do nothing.
    }

    /**
     * Shows any UI required to resolve the error connecting.
     * 
     * @param requestCode
     *            the request code for the {@link PlusClient}
     */
    public void signIn(int requestCode) {
        if (requestCode < 0) {
            throw new IllegalArgumentException(
                    "A non-negative request code is required.");
        }

        if (mPlusClient.isConnected()) {
            // Already connected! Schedule callback.
            mHandler.sendEmptyMessage(PlusClientFragmentHandler.WHAT_SIGNED_IN);
            return;
        }

        if (mRequestCode != INVALID_REQUEST_CODE) {
            // We're already signing in.
            return;
        }

        mRequestCode = requestCode;
        if (mLastConnectionResult == null) {
            // We're starting up, show progress.
            showProgressDialog();
            return;
        }

        resolveLastResult();
    }

    /**
     * Perform resolution given a non-null result.
     */
    private void resolveLastResult() {
        if (GooglePlayServicesUtil.isUserRecoverableError(mLastConnectionResult
                .getErrorCode())) {
            // Show a dialog to install or enable Google Play services.
            showErrorDialog(ErrorDialogFragment.create(
                    mLastConnectionResult.getErrorCode(), mRequestCode));
            return;
        }

        if (mLastConnectionResult.hasResolution()) {
            startResolution();
        }
    }

    /**
     * A {@link Fragment} for displaying error related to the user of Google+
     * sign in.
     * 
     * @author Chris Rovillos
     * 
     */
    public static final class ErrorDialogFragment extends
            GooglePlayServicesErrorDialogFragment {

        /**
         * Creates an {@link ErrorDialogFragment} from the given error code and
         * request code.
         * 
         * @param errorCode
         *            the error code
         * @param requestCode
         *            the request code
         * @return an {@link ErrorDialogFragment}
         */
        public static ErrorDialogFragment create(int errorCode,
                int requestCode) {
            final ErrorDialogFragment fragment = new ErrorDialogFragment();
            fragment.setArguments(createArguments(errorCode, requestCode));
            return fragment;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            final FragmentActivity activity = getActivity();
            if (activity == null) {
                return;
            }

            final Fragment fragment = activity.getSupportFragmentManager()
                    .findFragmentByTag(TAG_PLUS_CLIENT);
            if (fragment instanceof PlusClientFragment) {
                ((PlusClientFragment) fragment).onDialogCanceled(getTag());
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);

            final FragmentActivity activity = getActivity();
            if (activity == null) {
                return;
            }

            final Fragment fragment = activity.getSupportFragmentManager()
                    .findFragmentByTag(TAG_PLUS_CLIENT);
            if (fragment instanceof PlusClientFragment) {
                ((PlusClientFragment) fragment).onDialogDismissed(getTag());
            }
        }
    }

    /**
     * Called when the user cancels the dialog.
     * 
     * @param tag
     *            the tag of the dialog
     */
    private void onDialogCanceled(String tag) {
        mRequestCode = INVALID_REQUEST_CODE;
        hideProgressDialog();
    }

    /**
     * Called when the user dismisses the dialog.
     * 
     * @param tag
     *            the tag of the dialog
     */
    private void onDialogDismissed(String tag) {
        if (TAG_PROGRESS_DIALOG.equals(tag)) {
            mRequestCode = INVALID_REQUEST_CODE;
            hideProgressDialog();
        }
    }

    /**
     * Shows the progress dialog.
     */
    private void showProgressDialog() {
        DialogFragment progressDialog = (DialogFragment) getFragmentManager()
                .findFragmentByTag(TAG_PROGRESS_DIALOG);
        if (progressDialog == null) {
            progressDialog = ProgressDialogFragment.create();
            progressDialog.show(getFragmentManager(), TAG_PROGRESS_DIALOG);
        }
    }

    /**
     * A {@link Fragment} showing an indeterminate progress indicator used when
     * the user signs in to Google+.
     * 
     * @author Chris Rovillos
     * 
     */
    public static final class ProgressDialogFragment extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        /**
         * Creates a new {@link ProgressDialogFragment} with the given message
         * code.
         * 
         * @param message
         *            the message code
         * @return a new {@link ProgressDialogFragment}
         */
        public static ProgressDialogFragment create(int message) {
            final ProgressDialogFragment progressDialogFragment =
                    new ProgressDialogFragment();
            final Bundle args = new Bundle();
            args.putInt(ARG_MESSAGE, message);
            progressDialogFragment.setArguments(args);
            return progressDialogFragment;
        }

        /**
         * Creates a new {@link ProgressDialogFragment}.
         * 
         * @return a new {@link ProgressDialogFragment}
         */
        public static ProgressDialogFragment create() {
            return create(R.string.google_sign_in_progress_message);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog progressDialog = new ProgressDialog(
                    getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(getArguments().getInt(
                    ARG_MESSAGE)));
            return progressDialog;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);

            final FragmentActivity activity = getActivity();
            if (activity == null) {
                return;
            }

            final Fragment fragment = activity.getSupportFragmentManager()
                    .findFragmentByTag(TAG_PLUS_CLIENT);
            if (fragment instanceof PlusClientFragment) {
                ((PlusClientFragment) fragment).onDialogCanceled(getTag());
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);

            final FragmentActivity activity = getActivity();
            if (activity == null) {
                return;
            }

            final Fragment fragment = activity.getSupportFragmentManager()
                    .findFragmentByTag(TAG_PLUS_CLIENT);
            if (fragment instanceof PlusClientFragment) {
                ((PlusClientFragment) fragment).onDialogDismissed(getTag());
            }
        }
    }

    /**
     * Hides the progress dialog.
     */
    protected void hideProgressDialog() {
        final FragmentManager manager = getFragmentManager();
        if (manager != null) {
            final DialogFragment progressDialog = (DialogFragment) manager
                    .findFragmentByTag(TAG_PROGRESS_DIALOG);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    /**
     * Shows the given error {@link DialogFragment}.
     * 
     * @param errorDialog
     *            the {@link DialogFragment} to show
     */
    private void showErrorDialog(DialogFragment errorDialog) {
        final DialogFragment oldErrorDialog =
                (DialogFragment) getFragmentManager().findFragmentByTag(
                        TAG_ERROR_DIALOG);
        if (oldErrorDialog != null) {
            oldErrorDialog.dismiss();
        }

        errorDialog.show(getFragmentManager(), TAG_ERROR_DIALOG);
    }

    /**
     * Returns true if the error dialog is showing; returns false otherwise.
     * 
     * @return true if the error dialog is showing; returns false otherwise
     */
    private boolean isShowingErrorDialog() {
        final DialogFragment errorDialog =
                (DialogFragment) getFragmentManager().findFragmentByTag(
                        TAG_ERROR_DIALOG);
        return errorDialog != null && !errorDialog.isHidden();
    }

    /**
     * Hides the error dialog, if showing.
     */
    private void hideErrorDialog() {
        final DialogFragment errorDialog =
                (DialogFragment) getFragmentManager().findFragmentByTag(
                        TAG_ERROR_DIALOG);
        if (errorDialog != null) {
            errorDialog.dismiss();
        }
    }

    /**
     * Attempts to start a resolution for any error that occurs during the
     * Google+ sign-in process.
     */
    private void startResolution() {
        try {
            mLastConnectionResult.startResolutionForResult(
                    getSherlockActivity(), mRequestCode);
            hideProgressDialog();
        } catch (SendIntentException e) {
            // The intent we had is not valid right now, perhaps the remote
            // process died.
            // Try to reconnect to get a new resolution intent.
            mLastConnectionResult = null;
            showProgressDialog();
            connectPlusClient();
        }
    }

    /**
     * Called to handle the result of a Google+ sign-in error resolution
     * process.
     * 
     * @param requestCode
     *            the request code
     * @param resultCode
     *            the result code of the resolution
     * @param data
     *            the data from the resolution
     * @return true if requestCode matches the sign-in request code; false
     *         otherwise
     */
    public boolean handleOnActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode != mRequestCode) {
            return false;
        }

        switch (resultCode) {
        case Activity.RESULT_OK:
            mLastConnectionResult = null;
            connectPlusClient();
            break;
        case Activity.RESULT_CANCELED:
            // User canceled sign in, clear the request code.
            mRequestCode = INVALID_REQUEST_CODE;

            // Attempt to connect again.
            connectPlusClient();
            break;
        default:
            break;
        }
        return true;
    }

    /**
     * Sign out of the app.
     */
    public void signOut() {
        if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
        }

        if (mPlusClient.isConnecting() || mPlusClient.isConnected()) {
            mPlusClient.disconnect();
            // Reconnect to get a new mPlusClient.
            mLastConnectionResult = null;
            // Cancel sign in.
            mRequestCode = INVALID_REQUEST_CODE;

            // Reconnect to fetch the sign-in (account chooser) intent from the
            // plus client.
            connectPlusClient();
        }
    }

    /**
     * Revoke access to the current app.
     */
    public void revokeAccessAndDisconnect() {
        if (mPlusClient.isConnected()) {
            mPlusClient.revokeAccessAndDisconnect(this);
        }
    }

    /**
     * Attempts to connect the client to Google Play services if the client
     * isn't already connected, and isn't in the process of being connected.
     */
    private void connectPlusClient() {
        if (!mPlusClient.isConnecting() && !mPlusClient.isConnected()) {
            mPlusClient.connect();
        }
    }
}

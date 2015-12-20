package com.waynebjackson.gcmtesting.messaging;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.waynebjackson.gcmtesting.R;
import com.waynebjackson.gcmtesting.listeners.TokenRemovalListener;
import com.waynebjackson.gcmtesting.log.Logger;
import com.waynebjackson.gcmtesting.login.LoginActivity;
import com.waynebjackson.gcmtesting.models.GCMUser;
import com.waynebjackson.gcmtesting.preferences.QuickstartPreferences;
import com.waynebjackson.gcmtesting.services.RegistrationIntentService;
import com.waynebjackson.gcmtesting.system.Toaster;

public class MessagingActivity extends AppCompatActivity {
    private static final Logger LOGGER = new Logger(MessagingActivity.class.getSimpleName());

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private Toaster mToaster;
    private ProgressDialog mProgressDialog;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToaster = new Toaster(this);

        mProgressDialog = new ProgressDialog(this);
        showProgressDialog(getString(R.string.we_are_working_on_it),
                getString(R.string.registering_for_gcm));

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                hideProgressDialog();
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);

                if (sentToken) {
                    mToaster.showLongMessage(R.string.gcm_send_message);
                } else {
                    mToaster.showLongMessage(R.string.token_error_message);
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.`
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void showProgressDialog(String title, String message) {
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    public void signOut() {
        showProgressDialog(getString(R.string.we_are_working_on_it), getString(R.string.signing_out));

        // Remove this local gcm registration token from the servers first.
        GCMUser currentUser = GCMUser.getCurrentUser();
        currentUser.removeGoogleCloudRegistrationToken(this, new TokenRemovalListener() {

            @Override
            public void onSuccess() {
                // TODO: Clear any showing notifications
                hideProgressDialog();
                ParseUser.logOut();
                returnToLoginActivity();
            }

            @Override
            public void onError(ParseException e) {
                hideProgressDialog();
                mToaster.showShortMessage(R.string.error_signing_out);
                LOGGER.e(e, "Error signing out and removing token from Parse.");
            }
        });
    }

    private void returnToLoginActivity() {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        loginActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginActivity);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                LOGGER.i("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}

package com.waynebjackson.gcmtesting.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.waynebjackson.gcmtesting.models.GCMUser;
import com.waynebjackson.gcmtesting.preferences.QuickstartPreferences;
import com.waynebjackson.gcmtesting.configuration.GCMConfig;
import com.waynebjackson.gcmtesting.log.Logger;

import java.io.IOException;

/**
 * Created by Wayne on 11/8/15.
 */
public class RegistrationIntentService extends IntentService {

    private static final Logger LOGGER = new Logger(RegistrationIntentService.class.getSimpleName());

    public RegistrationIntentService() {
        super(RegistrationIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(GCMConfig.getSenderID(),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            // [END get_token]
            LOGGER.i("GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            LOGGER.e(e, "Failed to complete token refresh");
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        GCMUser currentUser = GCMUser.getCurrentUser();
        currentUser.addGoogleCloudMessengerRegistrationToken(this, token);
        currentUser.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    LOGGER.d("User GCM Token Successfully Updated");
                } else {
                    // TODO: Handle registration error here
                    LOGGER.e(e, "Error saving GCM registration token");
                }
            }
        });
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GCMUser currentUser = GCMUser.getCurrentUser();
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        pubSub.subscribe(token, "/topics/" + currentUser.getObjectId(), null);
    }
    // [END subscribe_topics]

}

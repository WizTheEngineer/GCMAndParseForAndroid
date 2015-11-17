package com.waynebjackson.gcmtesting.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.waynebjackson.gcmtesting.listeners.TokenRemovalListener;
import com.waynebjackson.gcmtesting.log.Logger;
import com.waynebjackson.gcmtesting.preferences.QuickstartPreferences;
import com.waynebjackson.gcmtesting.string.StringValidator;

import java.util.Arrays;

/**
 * Created by Wayne on 11/7/15.
 */
@ParseClassName("_User")
public class GCMUser extends ParseUser {
    private static final Logger LOGGER = new Logger(GCMUser.class.getSimpleName());

    // Required empty constructor
    public GCMUser() {}

    public static GCMUser getCurrentUser() {
        return (GCMUser) ParseUser.getCurrentUser();
    }

    public void addGoogleCloudMessengerRegistrationToken(Context context, String token) {
        // Check if previous token exists for this device
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.contains(QuickstartPreferences.LOCAL_GCM_TOKEN)) {

            String oldToken = preferences.getString(QuickstartPreferences.LOCAL_GCM_TOKEN, null);
            if (oldToken != null) {

                // The old token for this device exists check if the new token matches
                if (StringValidator.matches(oldToken, token)) {

                    // The two tokens match no further action is needed
                    LOGGER.i("New token matches old one no server action needed.");
                    return;
                } else {
                    LOGGER.d("Removing the old token from the server.");

                    // The new token is different remove the old one from the server
                    removeAll("gcmRegistrationTokens", Arrays.asList(new String[]{oldToken}));
                }
            }
        }

        // If this is reached save the token to the server
        addAllUnique("gcmRegistrationTokens", Arrays.asList(new String[] {token}));

        // Save this token to preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(QuickstartPreferences.LOCAL_GCM_TOKEN, token);
        editor.commit();
    }

    public void removeGoogleCloudRegistrationToken(Context context, final TokenRemovalListener listener) {
        // Check if previous token exists for this device
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (!preferences.contains(QuickstartPreferences.LOCAL_GCM_TOKEN)) {
            // No token exists for this device
            LOGGER.d("No token to remove for this user and this device.");
            listener.onSuccess();
        } else {
            String token = preferences.getString(QuickstartPreferences.LOCAL_GCM_TOKEN, null);
            if (token == null) {
                LOGGER.d("Stored token is null");
                listener.onSuccess();
            } else {
                // Remove the token from the local cache
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(QuickstartPreferences.LOCAL_GCM_TOKEN);
                editor.commit();

                // Remove the token from the server
                removeAll("gcmRegistrationTokens", Arrays.asList(new String[]{token}));
                saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            listener.onSuccess();
                        } else {
                            listener.onError(e);
                        }
                    }
                });
            }
        }
    }
}

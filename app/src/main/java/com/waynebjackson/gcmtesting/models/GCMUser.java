package com.waynebjackson.gcmtesting.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

/**
 * Created by Wayne on 11/7/15.
 */
@ParseClassName("_User")
public class GCMUser extends ParseUser {

    // Required empty constructor
    public GCMUser() {}

    public static GCMUser getCurrentUser() {
        return (GCMUser) ParseUser.getCurrentUser();
    }

    public void setGoogleCloudMessengerRegistrationToken(String token) {
        put("gcmRegistrationToken", token);
    }
}

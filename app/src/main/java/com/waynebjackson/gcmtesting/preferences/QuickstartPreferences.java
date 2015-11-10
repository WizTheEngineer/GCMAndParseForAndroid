package com.waynebjackson.gcmtesting.preferences;

import com.waynebjackson.gcmtesting.models.GCMUser;

/**
 * Created by Wayne on 11/8/15.
 */
public class QuickstartPreferences {
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer"
            + GCMUser.getCurrentUser().getObjectId();
    public static final String REGISTRATION_COMPLETE = "registrationComplete"
            + GCMUser.getCurrentUser().getObjectId();

}

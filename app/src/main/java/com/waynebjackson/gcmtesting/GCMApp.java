package com.waynebjackson.gcmtesting;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.waynebjackson.gcmtesting.configuration.ParseConfig;
import com.waynebjackson.gcmtesting.models.GCMUser;

/**
 * Created by Wayne on 11/7/15.
 */
public class GCMApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initParse();
    }

    private void initParse() {
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(GCMUser.class);
        Parse.initialize(this, ParseConfig.getApplicationId(), ParseConfig.getClientKey());
    }
}

package com.waynebjackson.gcmtesting.log;

import android.util.Log;

/**
 * Created by Wayne on 11/7/15.
 */
public class Logger {

    private String mTag;

    public Logger(String tag) {
        mTag = tag;
    }

    public void v(String... message) {
        Log.v(mTag,  getStringFromMessageArray(message));
    }

    public void i(String... message) {
        Log.i(mTag,  getStringFromMessageArray(message));
    }

    public void d(String... message) {
        Log.d(mTag,  getStringFromMessageArray(message));
    }

    public void e(String... message) {
        Log.e(mTag,  getStringFromMessageArray(message));
    }

    public void e(Exception e, String... message) {
        Log.e(mTag, getStringFromMessageArray(message), e);
    }

    private String getStringFromMessageArray(String... message) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length; i++) {
            builder.append(message[i]);
        }
        return builder.toString();
    }
}

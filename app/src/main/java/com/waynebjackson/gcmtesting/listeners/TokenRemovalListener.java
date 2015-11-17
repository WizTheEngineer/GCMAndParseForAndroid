package com.waynebjackson.gcmtesting.listeners;

import com.parse.ParseException;

/**
 * Created by Wayne on 11/16/15.
 */
public interface TokenRemovalListener {
    void onSuccess();
    void onError(ParseException e);
}

package com.waynebjackson.gcmtesting.system;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Wayne on 11/7/15.
 */
public class Toaster {

    private Context mContext;

    public Toaster(Context context) {
        mContext = context;
    }

    public void showShortMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void showShortMessage(int messageResourceId) {
        Toast.makeText(mContext, messageResourceId, Toast.LENGTH_SHORT).show();
    }

    public void showLongMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    public void showLongMessage(int messageResourceId) {
        Toast.makeText(mContext, messageResourceId, Toast.LENGTH_LONG).show();
    }
}

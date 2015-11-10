package com.waynebjackson.gcmtesting.login;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.parse.ParseUser;
import com.waynebjackson.gcmtesting.R;
import com.waynebjackson.gcmtesting.log.Logger;
import com.waynebjackson.gcmtesting.messaging.MessagingActivity;

/**
 * Created by Wayne on 11/7/15.
 */
public class LoginActivity extends AppCompatActivity {
    private static final Logger LOGGER = new Logger(LoginActivity.class.getSimpleName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            showDefaultFragment();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void showDefaultFragment() {

        if (ParseUser.getCurrentUser() != null) {
            showMessagingActivity();
        } else {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, SignUpFragment.newInstance())
                    .commit();
        }
    }

    public void showSignUpFragment() {
        performFragmentTransaction(SignUpFragment.newInstance(), true);
    }

    public void showSignInFragment() {
        performFragmentTransaction(SignInFragment.newInstance(), true);
    }

    private void performFragmentTransaction(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    public void showMessagingActivity() {
        Intent messagingActivity = new Intent(this, MessagingActivity.class);
        messagingActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(messagingActivity);
    }
}

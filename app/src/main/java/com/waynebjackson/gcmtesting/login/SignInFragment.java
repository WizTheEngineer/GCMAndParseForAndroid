package com.waynebjackson.gcmtesting.login;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.waynebjackson.gcmtesting.R;
import com.waynebjackson.gcmtesting.log.Logger;
import com.waynebjackson.gcmtesting.string.StringValidator;
import com.waynebjackson.gcmtesting.system.Toaster;

/**
 * Created by Wayne on 11/7/15.
 */
public class SignInFragment extends Fragment implements View.OnClickListener {
    private static final Logger LOGGER = new Logger(SignInFragment.class.getSimpleName());

    private Toaster mToaster;
    private EditText mEmailField, mPasswordField;
    private Button mSignInButton;
    private TextView mNotAUserTextView;
    private ProgressDialog mSignInDialog;

    public SignInFragment() {
    }

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToaster = new Toaster(getActivity());
        mSignInDialog = new ProgressDialog(getActivity());
        mSignInDialog.setTitle(getString(R.string.we_are_working_on_it));
        mSignInDialog.setMessage(getString(R.string.attempting_sign_in));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mEmailField = (EditText) rootView.findViewById(R.id.email_field);
        mPasswordField = (EditText) rootView.findViewById(R.id.password_field);
        mSignInButton = (Button) rootView.findViewById(R.id.sign_in_button);
        mNotAUserTextView = (TextView) rootView.findViewById(R.id.not_a_user_text_view);

        mSignInButton.setOnClickListener(this);
        mNotAUserTextView.setOnClickListener(this);

        // Underline already in user text view
        mNotAUserTextView.setPaintFlags(mNotAUserTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                attemptSignIn();
                break;
            case R.id.not_a_user_text_view:
                showSignUpFragment();
                break;
        }
    }

    private void attemptSignIn() {
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        if (fieldsValid(email, password)) {
            showProgressDialog();

            LOGGER.i("Sign in with ", email, password);
            ParseUser.logInInBackground(email, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    hideProgressDialog();
                    if (user != null) {
                        // Hooray! The user is logged in.
                        showMessagingActivity();
                    } else {
                        // Signup failed. Look at the ParseException to see what happened.
                        mToaster.showShortMessage(R.string.sign_in_unsuccessful);
                        LOGGER.e(e, "Sign up error");
                    }
                }
            });
        }
    }

    private boolean fieldsValid(String email, String password) {
        if (StringValidator.isNullOrEmpty(email, password)) {
            mToaster.showShortMessage(R.string.empty_fields);
            return false;
        } else if (!StringValidator.isValidEmail(email)) {
            mToaster.showShortMessage(R.string.invalid_email);
            return false;
        }
        return true;
    }

    private void showSignUpFragment() {
        ((LoginActivity) getActivity()).showSignUpFragment();
    }

    private void showMessagingActivity() {
        ((LoginActivity) getActivity()).showMessagingActivity();
    }

    private void showProgressDialog() {
        mSignInDialog.show();
    }

    private void hideProgressDialog() {
        mSignInDialog.dismiss();
    }
}

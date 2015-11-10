package com.waynebjackson.gcmtesting.login;


import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.waynebjackson.gcmtesting.R;
import com.waynebjackson.gcmtesting.log.Logger;
import com.waynebjackson.gcmtesting.models.GCMUser;
import com.waynebjackson.gcmtesting.string.StringValidator;
import com.waynebjackson.gcmtesting.system.Toaster;

import java.util.List;

public class SignUpFragment extends Fragment implements View.OnClickListener {
    private static final Logger LOGGER = new Logger(SignUpFragment.class.getSimpleName());

    private static final int MINIMUM_PASSWORD_LENGTH = 6;

    private Toaster mToaster;
    private EditText mEmailField, mPasswordField, mPasswordConfirmationField;
    private Button mRegisterButton;
    private TextView mAlreadyAUserTextView;
    private ProgressDialog mSignUpDialog;

    public SignUpFragment() {}

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToaster = new Toaster(getActivity());
        mSignUpDialog = new ProgressDialog(getActivity());
        mSignUpDialog.setTitle(getString(R.string.we_are_working_on_it));
        mSignUpDialog.setMessage(getString(R.string.attempting_sign_up));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mEmailField = (EditText) rootView.findViewById(R.id.email_field);
        mPasswordField = (EditText) rootView.findViewById(R.id.password_field);
        mPasswordConfirmationField = (EditText) rootView.findViewById(R.id.password_confirmation_field);
        mRegisterButton = (Button) rootView.findViewById(R.id.register_button);
        mAlreadyAUserTextView = (TextView) rootView.findViewById(R.id.already_a_user_text_view);

        mRegisterButton.setOnClickListener(this);
        mAlreadyAUserTextView.setOnClickListener(this);

        // Underline already in user text view
        mAlreadyAUserTextView.setPaintFlags(mAlreadyAUserTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                attemptRegister();
                break;
            case R.id.already_a_user_text_view:
                showSignInFragment();
                break;
        }
    }

    private void attemptRegister() {
        final String email = mEmailField.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();
        final String passwordConfirmation = mPasswordConfirmationField.getText().toString().trim();

        if (fieldsValid(email, password, passwordConfirmation)) {
            showProgressDialog();

            // Check that the email is not is use
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("email", email);
            query.findInBackground(new FindCallback<ParseUser>() {

                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        if (objects.isEmpty()) {
                            // Yayyy! Email is available for user to use
                            registerUser(email, password);
                        } else {
                            // Already a user by that email
                            hideProgressDialog();
                            mToaster.showShortMessage(R.string.email_in_use);
                        }
                    } else {
                        // There was an error with the query
                        handleError(e);
                    }
                }
            });
        }
    }

    private void registerUser(String email, String password) {
        GCMUser user = new GCMUser();
        user.setEmail(email);
        user.setUsername(email);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // User has been signed up successfully
                    handleSuccessfulSignUp();
                } else {
                    // Uh-oh there was an error
                    handleError(e);
                }
            }
        });
    }

    private boolean fieldsValid(String email, String password, String passwordConfirmation) {

        if (StringValidator.isNullOrEmpty(email, password, passwordConfirmation)) {
            mToaster.showShortMessage(R.string.empty_fields);
            return false;
        } else if (!StringValidator.isValidEmail(email)) {
            mToaster.showShortMessage(R.string.invalid_email);
            return false;
        } else if (!StringValidator.match(password, passwordConfirmation)) {
            mToaster.showShortMessage(R.string.passwords_do_not_match);
            return false;
        } else if (password.length() < MINIMUM_PASSWORD_LENGTH) {
            mToaster.showShortMessage(getString(R.string.password_must_be_at_least)
                    + MINIMUM_PASSWORD_LENGTH + getString(R.string.characters));
            return false;
        }

        return true;
    }

    private void showProgressDialog() {
        mSignUpDialog.show();
    }

    private void hideProgressDialog() {
        mSignUpDialog.dismiss();
    }

    private void handleSuccessfulSignUp() {
        hideProgressDialog();
        showMessagingActivity();
    }

    private void handleError(ParseException e) {
        hideProgressDialog();
        mToaster.showShortMessage(R.string.there_was_an_error);
        LOGGER.e(e, "Error signing up");
    }

    private void showSignInFragment() {
        ((LoginActivity) getActivity()).showSignInFragment();
    }

    private void showMessagingActivity() {
        ((LoginActivity) getActivity()).showMessagingActivity();
    }
}
